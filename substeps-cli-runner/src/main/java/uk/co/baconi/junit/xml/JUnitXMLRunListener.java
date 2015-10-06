package uk.co.baconi.junit.xml;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

public class JUnitXMLRunListener extends RunListener {

    private static final Logger LOG = LoggerFactory.getLogger(JUnitXMLRunListener.class);

    private final File xmlFile;

    private final Map<Description, TestCase> testCaseMap = new HashMap<>();
    private ITestContainer activeTs;

    private ITestContainer root;

    enum ResultEnum {
        PASS, FAILURE, IGNORED, DEFAULT
    }

    interface ITest {
        Element writeXml(Document doc, Element parent);

        ResultEnum getResult();
    }

    interface ITestContainer {
        void addTest(ITest test);

        void finish(Result result);

        Element writeXml(Document doc, Element parent);
    }

    public JUnitXMLRunListener(final String xmlFile) {
        this(new File(xmlFile));

        final File parent = this.xmlFile.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
    }

    private JUnitXMLRunListener(final File xmlFile) {
        this.xmlFile = xmlFile;

        LOG.trace("new JUnitXMLRunListener(" + xmlFile + ")");
    }

    @Override
    public void testRunStarted(final Description description) throws Exception {
        LOG.trace("testRunStarted(" + description.getClassName() + ")");

        if (root == null) {
            root = new TestSuite(description);
            activeTs = root;
        }
    }

    @Override
    public void testRunFinished(final Result result) throws Exception {
        LOG.trace("testRunFinished(" + result + ")");

        activeTs.finish(result);
        writeXml();
    }

    @Override
    public void testStarted(final Description description) throws Exception {
        LOG.info(description.getDisplayName());

        final TestCase activeTc = new TestCase(description);
        activeTs.addTest(activeTc);
        testCaseMap.put(description, activeTc);
    }

    @Override
    public void testFinished(final Description description) throws Exception {
        LOG.trace("testFinished(" + description + ")");

        final TestCase activeTc = testCaseMap.get(description);
        if (activeTc != null) {
            activeTc.pass();
        }
    }

    @Override
    public void testFailure(final Failure failure) throws Exception {
        LOG.trace("testFailure(" + failure + ")");

        final TestCase activeTc = testCaseMap.get(failure.getDescription());
        if (activeTc != null) {
            activeTc.fail(failure);
        }
    }

    @Override
    public void testAssumptionFailure(final Failure failure) {
        LOG.trace("testAssumptionFailure(" + failure + ")");

        final TestCase activeTc = testCaseMap.get(failure.getDescription());
        if (activeTc != null) {
            activeTc.fail(failure);
        }
    }

    @Override
    public void testIgnored(final Description description) throws Exception {
        LOG.trace("testIgnored(" + description + ")");

        final TestCase activeTc = testCaseMap.get(description);
        if (activeTc != null) {
            activeTc.ignore();
        }
    }

    private void writeXml() {
        LOG.trace("writeXml()");
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder builder = factory.newDocumentBuilder();
            final Document doc = builder.newDocument();

            doc.appendChild(root.writeXml(doc, null));

            final TransformerFactory tFactory = TransformerFactory.newInstance();
            final Transformer transformer = tFactory.newTransformer();
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            final DOMSource source = new DOMSource(doc);
            final FileOutputStream fos = new FileOutputStream(xmlFile);
            final StreamResult result = new StreamResult(fos);

            transformer.transform(source, result);
            fos.close();

            LOG.info("JUnit Report Created: " + xmlFile.getAbsolutePath());

        } catch (final Exception e) {

            LOG.error("Unable to write xml: ", e);

        }
    }
}
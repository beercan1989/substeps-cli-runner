package uk.co.baconi.junit.xml;

import uk.co.baconi.junit.xml.JUnitXMLRunListener.ITest;
import uk.co.baconi.junit.xml.JUnitXMLRunListener.ResultEnum;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

class TestCase implements ITest {

    private static final Logger LOG = LoggerFactory.getLogger(TestCase.class);

    private final String name;
    private final long starTime;

    private long endTime;
    private final String className;
    private ResultEnum result;
    private String message;
    private String trace;

    public TestCase(final Description desc) {

        this.className = desc.getClassName();
        this.starTime = System.currentTimeMillis();
        this.result = ResultEnum.DEFAULT;

        if (desc.getMethodName() == null || desc.getMethodName().isEmpty()) {
            this.name = this.className;
        } else {
            this.name = desc.getMethodName();
        }
    }

    public void pass() {
        this.result = ResultEnum.PASS;
        this.endTime = System.currentTimeMillis();
    }

    public void fail(final Failure failure) {
        this.result = ResultEnum.FAILURE;
        this.message = failure.getMessage();
        this.trace = failure.getTrace();
        this.endTime = System.currentTimeMillis();
    }

    public void ignore() {
        result = ResultEnum.IGNORED;
        this.endTime = System.currentTimeMillis();
    }

    @Override
    public Element writeXml(final Document doc, final Element parent) {
        final Element tcElem = doc.createElement("testcase");
        parent.appendChild(tcElem);
        tcElem.setAttribute("classname", className);
        tcElem.setAttribute("name", name);
        tcElem.setAttribute("time", String.valueOf((endTime - starTime) / 1000.0));

        switch (result) {
            case PASS:
                break;
            case FAILURE: {
                final Element failNode = doc.createElement("failure");
                failNode.setAttribute("message", message + "###" + trace);
                tcElem.appendChild(failNode);
                break;
            }
            case IGNORED: {
                final Element ignoredNode = doc.createElement("skipped");
                tcElem.appendChild(ignoredNode);
                break;
            }
            default:
                LOG.debug("Something wen't wrong, no result was set. 'className':'" + className + "', 'name':'" + name + "'");
                break;
        }
        return tcElem;

    }

    @Override
    public ResultEnum getResult() {
        return result;
    }
}
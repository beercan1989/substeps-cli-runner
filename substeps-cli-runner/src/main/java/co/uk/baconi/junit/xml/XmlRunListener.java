/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package co.uk.baconi.junit.xml;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Prints XML output of the test to a specified Writer.
 */

public class XmlRunListener extends RunListener {

    private static final double ONE_SECOND = 1000.0;

    /**
     * constant for unnnamed testsuites/cases
     */
    private static final String UNKNOWN = "unknown";

    /**
     * the system-err element
     */
    private static final String SYSTEM_ERR = "system-err";

    /**
     * the system-out element
     */
    private static final String SYSTEM_OUT = "system-out";

    /**
     * the testsuite element
     */
    private static final String TESTSUITE = "testsuite";

    /**
     * the testcase element
     */
    private static final String TESTCASE = "testcase";

    /**
     * the failure element
     */
    private static final String FAILURE = "failure";

    /**
     * name attribute for property, testcase and testsuite elements
     */
    private static final String ATTR_NAME = "name";

    /**
     * time attribute for testcase and testsuite elements
     */
    private static final String ATTR_TIME = "time";

    /**
     * errors attribute for testsuite elements
     */
    private static final String ATTR_ERRORS = "errors";

    /**
     * failures attribute for testsuite elements
     */
    private static final String ATTR_FAILURES = "failures";

    /**
     * tests attribute for testsuite elements
     */
    private static final String ATTR_TESTS = "tests";

    /**
     * type attribute for failure and error elements
     */
    private static final String ATTR_TYPE = "type";

    /**
     * message attribute for failure elements
     */
    private static final String ATTR_MESSAGE = "message";

    /**
     * classname attribute for testcase elements
     */
    private static final String ATTR_CLASSNAME = "classname";

    /**
     * timestamp of test cases
     */
    private static final String TIMESTAMP = "timestamp";


    private static DocumentBuilder getDocumentBuilder() {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (final Exception exc) {
            throw new ExceptionInInitializerError(exc);
        }
    }

    /**
     * The XML document.
     */
    private Document doc;
    /**
     * The wrapper for the whole testsuite.
     */
    private Element rootElement;
    /**
     * Element for the current test.
     */
    private final Map<Description, Element> testElements = new ConcurrentHashMap<Description, Element>();
    /**
     * tests that failed.
     */
    private final Set<Description> failedTests = new HashSet<Description>();
    /**
     * Timing helper.
     */
    private final Map<Description, Long> testStarts = new ConcurrentHashMap<Description, Long>();
    /**
     * Where to write the log to.
     */
    private OutputStream out;

    private Description lastDescription;

    public XmlRunListener(final String filePath) {
        if (filePath != null) {
            final File target = new File(filePath);

            final File parent = target.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }

            try {
                this.out = new BufferedOutputStream(new FileOutputStream(target));
            } catch (final FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * {@inheritDoc}.
     */
    private void setOutput(final OutputStream out) {
        this.out = out;
    }

    /**
     * {@inheritDoc}.
     */
    public void setSystemOutput(final String out) {
        formatOutput(SYSTEM_OUT, out);
    }

    /**
     * {@inheritDoc}.
     */
    public void setSystemError(final String out) {
        formatOutput(SYSTEM_ERR, out);
    }

    /**
     * The whole testsuite started.
     */
    @Override
    public void testRunStarted(final Description description) throws Exception {
        doc = getDocumentBuilder().newDocument();
        rootElement = doc.createElement(TESTSUITE);

        //add the timestamp
        final String timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date());
        rootElement.setAttribute(TIMESTAMP, timestamp);

        this.lastDescription = description;
    }

    /**
     * The whole testsuite ended.
     */
    @Override
    public void testRunFinished(final Result result) throws Exception {
        rootElement.setAttribute(ATTR_TESTS, "" + result.getRunCount());
        rootElement.setAttribute(ATTR_FAILURES, "" + result.getFailureCount());
        rootElement.setAttribute(ATTR_ERRORS, "" + result.getFailures().size());
        rootElement.setAttribute(ATTR_TIME, "" + (result.getRunTime() / ONE_SECOND));

        final String className = lastDescription.getClassName();

        rootElement.setAttribute(ATTR_NAME,
                className == null || "null".equalsIgnoreCase(className.trim()) ? UNKNOWN : className.trim()
        );

        if (out != null) {
            Writer wri = null;
            try {
                wri = new BufferedWriter(new OutputStreamWriter(out, "UTF8"));
                wri.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
                (new DOMElementWriter()).write(rootElement, wri, 0, "  ");
            } catch (final IOException exc) {
                throw new Exception("Unable to write log file", exc);
            } finally {
                if (wri != null) {
                    try {
                        wri.flush();
                    } catch (final IOException ex) {
                        // ignore
                    }
                }
                if (out != System.out && out != System.err) {
                    closeQuietly(wri);
                }
            }
        }
    }

    private void closeQuietly(final Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (final IOException ioe) {
            // ignore
        }
    }

    /**
     * Interface RunListener.
     * <p/>
     * <p>A new Test is started.
     */
    @Override
    public void testStarted(final Description test) throws Exception {
        testStarts.put(test, System.currentTimeMillis());
    }

    /**
     * Interface RunListener.
     * <p/>
     * <p>A Test is finished.
     *
     * @param test the test.
     */
    @Override
    public void testFinished(final Description test) throws Exception {
        // Fix for bug #5637 - if a junit.extensions.TestSetup is
        // used and throws an exception during setUp then startTest
        // would never have been called
        if (!testStarts.containsKey(test)) {
            testStarted(test);
        }
        final Element currentTest;
        if (!failedTests.contains(test)) {
            currentTest = doc.createElement(TESTCASE);
            final String n = test.getDisplayName();
            currentTest.setAttribute(ATTR_NAME, n == null ? UNKNOWN : n);
            // a TestSuite can contain Tests from multiple classes,
            // even tests with the same name - disambiguate them.
            currentTest.setAttribute(ATTR_CLASSNAME, test.getTestClass().getName());
            rootElement.appendChild(currentTest);
            testElements.put(test, currentTest);
        } else {
            currentTest = testElements.get(test);
        }

        final Long l = testStarts.get(test);
        currentTest.setAttribute(ATTR_TIME, "" + ((System.currentTimeMillis() - l) / ONE_SECOND));

        this.lastDescription = test;
    }

    /**
     * Interface RunListener for JUnit &lt;= 3.4.
     * <p/>
     * <p>A Test failed.
     */
    @Override
    public void testFailure(final Failure failure) throws Exception {
        if (failure.getDescription() != null) {
            testFinished(failure.getDescription());
            failedTests.add(failure.getDescription());
        }

        final Element nested = doc.createElement(FAILURE);
        final Element currentTest;
        if (failure.getDescription() != null) {
            currentTest = testElements.get(failure.getDescription());
        } else {
            currentTest = rootElement;
        }

        currentTest.appendChild(nested);

        final String message = failure.getMessage();
        if (message != null && message.length() > 0) {
            nested.setAttribute(ATTR_MESSAGE, failure.getMessage());
        }
        nested.setAttribute(ATTR_TYPE, failure.getClass().getName());

        final String strace = failure.getTrace();
        final Text trace = doc.createTextNode(strace);
        nested.appendChild(trace);
    }

    private void formatOutput(final String type, final String output) {
        final Element nested = doc.createElement(type);
        rootElement.appendChild(nested);
        nested.appendChild(doc.createCDATASection(output));
    }

}
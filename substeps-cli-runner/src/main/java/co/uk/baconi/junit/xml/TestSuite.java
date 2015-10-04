package co.uk.baconi.junit.xml;

import co.uk.baconi.junit.xml.JUnitXMLRunListener.ITest;
import co.uk.baconi.junit.xml.JUnitXMLRunListener.ITestContainer;
import co.uk.baconi.junit.xml.JUnitXMLRunListener.ResultEnum;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;

class TestSuite implements ITest, ITestContainer {
    private final String name;
    private final ArrayList<ITest> tests;
    private long runTime;
    private int failures;
    private int skipped;

    public TestSuite(final Description description) {
        name = description.getClassName() == null ? description.getClassName() : description.getClassName();
        tests = new ArrayList<>();
    }

    @Override
    public void addTest(final ITest test) {
        tests.add(test);
    }

    @Override
    public Element writeXml(final Document doc, final Element parent) {
        final Element tsElem = doc.createElement("testsuite");
        if (parent != null) {
            parent.appendChild(tsElem);
        }
        tsElem.setAttribute("name",		name);
        tsElem.setAttribute("tests",	String.valueOf(tests.size()));
        tsElem.setAttribute("failures",	String.valueOf(failures));
        tsElem.setAttribute("skipped",	String.valueOf(skipped));
        tsElem.setAttribute("time",		String.valueOf((runTime / 1000.0)));

        for (final ITest test : tests) {
            test.writeXml(doc, tsElem);
        }
        return tsElem;
    }

    @Override
    public ResultEnum getResult() {
        return ResultEnum.PASS; // TODO
    }

    static String getElapsedTime(final long start, final long end) {
        return String.valueOf(end - start);
    }

    @Override
    public void finish(final Result result) {
        runTime = result.getRunTime();
        skipped = result.getIgnoreCount();
        failures = result.getFailureCount();
    }

}
package com.ti.uiautomator.utils;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ti.uiautomator.Constants;

public class JUnitReportWriter {

    private final StringBuffer buffer = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?><testsuites>");

    private int passes = 0;
    private int failures = 0;
    private int durationInSeconds = 0;
    private String testName;

    private final StringBuffer testOutcomes = new StringBuffer();

    public JUnitReportWriter() {
    }

    public void incrementPasses() {
        passes++;
    }

    public void incrementFailures() {
        failures++;
    }

    public int getDurationInSeconds() {
        return durationInSeconds;
    }

    public void setDurationInSeconds(int durationInSeconds) {
        this.durationInSeconds = durationInSeconds;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public void appendTestOutcome(boolean passed, String testName, int duration, String description,
            String failureMessage) {

        testOutcomes.append("<testcase classname=\"" + testName + "\" name=\"" + description + "\" time=\"" + duration
                + "\"");

        if (passed) {
            testOutcomes.append("/>");
        } else {
            testOutcomes
                    .append("><failure message=\"" + description + "\">" + failureMessage + "</failure></testcase>");
        }
    }

    public void flush() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        buffer.append("<testsuite name=\"" + testName + "\" errors=\"0\" tests=\"" + (failures + passes)
                + "\" failures=\"" + failures + "\" time=\"" + durationInSeconds + "\" timestamp=\""
                + sdf.format(new Date()) + "\" >");

        if (testOutcomes.toString().equals("")) {
            appendTestOutcome(true, "dummy scenario", 0, "dummy when zero in file", null);
        }

        buffer.append(testOutcomes.toString());

        buffer.append("</testsuite></testsuites>");

        System.out.println(buffer.toString());

        BufferedWriter bufferedWriter =
                new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Constants.TEST_DIR_ON_DEVICE
                        + "junitreport.xml"), "utf-8"));
        bufferedWriter.write(buffer.toString());
        bufferedWriter.close();
    }
}

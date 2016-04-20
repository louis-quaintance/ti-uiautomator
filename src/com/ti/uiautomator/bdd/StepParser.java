package com.ti.uiautomator.bdd;

public class StepParser {

    public static String parseStep(String line) {
        return toCamelCase(line);
    }

    private static String toCamelCase(String s) {
        String[] parts = s.split(" ");
        String camelCaseString = "";
        for (String part : parts) {
            camelCaseString = camelCaseString + toProperCase(part);
        }
        return camelCaseString.substring(0, 1).toLowerCase() + camelCaseString.substring(1);
    }

    private static String toProperCase(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    public static void main(String[] args) {
        System.out.println(StepParser.parseStep("Given that the home screen is in a valid logged out state"));
    }

}

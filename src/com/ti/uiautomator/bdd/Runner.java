package com.ti.uiautomator.bdd;

import java.io.File;
import java.io.FileInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.android.uiautomator.core.UiDevice;
import com.ti.uiautomator.Constants;
import com.ti.uiautomator.bdd.domain.Scenario;
import com.ti.uiautomator.bdd.domain.Step;
import com.ti.uiautomator.utils.FileUtils;
import com.ti.uiautomator.utils.JUnitReportWriter;
import com.ti.uiautomator.utils.ReflectionUtils;

public class Runner {

    public static void run(Object instance, List<String> deviceTagsToRun, File screenshotFolder, UiDevice mDevice)
            throws Exception {

        Properties properties = new Properties();
        properties.load(new FileInputStream(new File(Constants.TEST_DIR_ON_DEVICE + "tests.properties")));

        String testName = instance.getClass().getSimpleName().toLowerCase();

        List<String> lines =
                FileUtils.readFile(Constants.TEST_DIR_ON_DEVICE + testName.replace("test", "") + ".feature");

        List<Scenario> scenarios = new ArrayList<Scenario>();

        JUnitReportWriter jUnitReportWriter = new JUnitReportWriter();
        jUnitReportWriter.setTestName(testName);

        long startTime = System.currentTimeMillis();

        buildListOfScenarios(lines, scenarios);

        List<Scenario> filteredScenarios = new ArrayList<Scenario>();

        filteredScenarios = filterScenariosUsingTags(deviceTagsToRun, scenarios, filteredScenarios);

        boolean failingScenarios = false;
        int numberOfTestsPassing = 0;
        String nameOfFailedStep = null;
        long startTimeOfScenario = System.currentTimeMillis();

        for (Scenario scenario : filteredScenarios) {

            Thread.sleep(5000);

            System.out.println("Beginning test for " + scenario.getName());

            takeAScreenshot(mDevice, new File(screenshotFolder, scenario.getName().replace(" ", "_")
                    + "-startOfScenario.png"));

            boolean scenarioPassed = true;

            for (Step step : scenario.getSteps()) {

                System.out.println(step.getCommand());

                System.out.println("");

                try {

                    Method[] declaredMethods = instance.getClass().getMethods();

                    boolean stepInvoked = false;

                    for (Method method : declaredMethods) {

                        Annotation[] declaredAnnotations = method.getDeclaredAnnotations();

                        for (Annotation annotation : declaredAnnotations) {

                            if (annotation instanceof StepAnnotation) {

                                if (step.getCommand().matches(((StepAnnotation) annotation).regex())) {
                                    stepInvoked = true;
                                    invokeStep(instance, step, method);
                                    break;
                                }

                                if (stepInvoked) {
                                    break;
                                }

                            } else if (annotation instanceof StepAnnotations) {

                                List<String> regexes = new ArrayList<String>();
                                regexes.addAll(Arrays.asList(((StepAnnotations) annotation).regexes()));

                                for (String regex : regexes) {
                                    if (step.getCommand().matches(regex)) {
                                        stepInvoked = true;
                                        invokeStep(instance, step, method);
                                        break;
                                    }
                                }
                                if (stepInvoked) {
                                    break;
                                }
                            }
                        }
                        if (stepInvoked) {
                            break;
                        }
                    }

                    if (!stepInvoked) {
                        throw new RuntimeException("No method was found for step: " + step.getCommand());
                    }

                } catch (Exception e) {

                    System.err.println("Failed test for " + scenario.getName());
                    System.err.println("Failed at step " + step.getCommand());

                    e.printStackTrace();

                    jUnitReportWriter.incrementFailures();

                    scenarioPassed = false;
                    failingScenarios = true;

                    nameOfFailedStep = step.getCommand();

                    takeAScreenshot(mDevice, new File(screenshotFolder, scenario.getName().replace(" ", "_") + ".png"));

                    break;
                }
            }

            int scenarioDuration = (int) ((System.currentTimeMillis() - startTimeOfScenario) / 1000);

            if (scenarioPassed) {
                jUnitReportWriter.incrementPasses();
                jUnitReportWriter.appendTestOutcome(true, scenario.getName(), scenarioDuration, "Success", "");
                numberOfTestsPassing++;
                System.out.println("Successfully completed test for " + scenario.getName());
            } else {
                jUnitReportWriter.incrementFailures();
                jUnitReportWriter.appendTestOutcome(false, scenario.getName(), scenarioDuration, nameOfFailedStep, "");
                break;
            }
        }

        long endTime = System.currentTimeMillis();

        jUnitReportWriter.setDurationInSeconds((int) ((endTime - startTime) / 1000));

        jUnitReportWriter.flush();

        if (failingScenarios) {
            System.out.println("Tests did not pass," + numberOfTestsPassing + " passed out of "
                    + filteredScenarios.size());
            System.exit(1);
        } else {
            System.exit(0);
        }
    }

    private static void takeAScreenshot(UiDevice mDevice, File screenshotFile) {
        if (screenshotFile.exists()) {
            try {
                screenshotFile.delete();
            } catch (Exception e) {
            }
        }
        mDevice.takeScreenshot(screenshotFile);
    }

    private static void invokeStep(Object instance, Step step, Method method) {

        List<String> matches = extractTheArgumentsToPassToMethod(step);
        ReflectionUtils.invokeMethod(instance, method.getName(), matches.toArray(new String[matches.size()]));
    }

    private static List<String> extractTheArgumentsToPassToMethod(Step step) {
        Pattern p = Pattern.compile("\"([^\"]*)\"");
        Matcher m = p.matcher(step.getCommand());
        List<String> matches = new ArrayList<String>();
        while (m.find()) {
            matches.add(m.group(1));
        }
        return matches;
    }

    private static List<Scenario> filterScenariosUsingTags(List<String> deviceTagsToRun, List<Scenario> scenarios,
            List<Scenario> filteredScenarios) {

        if (deviceTagsToRun == null || deviceTagsToRun.size() == 0) {
            filteredScenarios = scenarios;
        } else {
            for (Scenario scenario : scenarios) {
                for (String tag : scenario.getTags()) {
                    if (deviceTagsToRun.contains(tag) && !filteredScenarios.contains(scenario)
                            && !tag.equals("@pending")) {
                        filteredScenarios.add(scenario);
                        break;
                    }
                }
            }
        }
        return filteredScenarios;
    }

    private static void buildListOfScenarios(List<String> lines, List<Scenario> scenarios) {

        String currentScenarioName = null;
        String currentTags = null;
        List<Step> steps = new ArrayList<Step>();

        List<String> examples = new ArrayList<String>();

        boolean scenarioHasExamples = false;

        for (int i = 0; i < lines.size(); i++) {

            String line = lines.get(i);

            if (line.startsWith("Scenario") || line.startsWith("@")) {

                if (currentScenarioName != null) {

                    Scenario scenario = new Scenario();
                    scenario.setName(currentScenarioName);
                    scenario.setSteps(new ArrayList<Step>(steps));

                    if (currentTags != null) {
                        scenario.setTags(Arrays.asList(currentTags.split(",")));
                    }

                    if (scenarioHasExamples) {
                        scenarios.addAll(processExamples(currentScenarioName, scenario, examples));
                    } else {
                        scenarios.add(scenario);
                    }
                }

                if (line.startsWith("Scenario")) {
                    currentScenarioName = line;
                } else {
                    currentTags = line;
                }

                steps = new ArrayList<Step>();

                scenarioHasExamples = false;
                examples = new ArrayList<String>();

            } else if (line.startsWith("Examples")) {

                scenarioHasExamples = true;

            } else if (!line.startsWith("#")) {

                if (scenarioHasExamples) {
                    examples.add(line);
                } else {
                    Step step = new Step();
                    step.setCommand(line);
                    steps.add(step);
                }

                if (i + 1 == lines.size()) {

                    Scenario scenario = new Scenario();
                    scenario.setName(currentScenarioName);
                    scenario.setSteps(new ArrayList<Step>(steps));

                    if (currentTags != null) {
                        scenario.setTags(Arrays.asList(currentTags.split(",")));
                    }

                    if (scenarioHasExamples) {
                        scenarios.addAll(processExamples(currentScenarioName, scenario, examples));
                    } else {
                        scenarios.add(scenario);
                    }

                    currentTags = null;
                    scenarioHasExamples = false;
                    examples = new ArrayList<String>();
                }
            }
        }
    }

    private static List<Scenario> processExamples(String currentScenarioName, Scenario scenario, List<String> examples) {

        if (examples.size() <= 1) {
            throw new RuntimeException("No examples please check feature file in scenario " + currentScenarioName);
        }

        List<Scenario> processedScenarios = new ArrayList<Scenario>();

        List<String> titles = processExampleString(examples.get(0));

        for (String example : examples.subList(1, examples.size())) {

            List<String> exampleData = processExampleString(example);
            if (exampleData.size() != titles.size()) {
                throw new RuntimeException("Table in example is malformed, in scenario " + currentScenarioName);
            }

            Scenario newScenario = new Scenario();

            newScenario.setName(currentScenarioName + example);

            for (Step step : scenario.getSteps()) {

                Step processedStep = new Step();

                String command = new String(step.getCommand());

                for (int i = 0; i < titles.size(); i++) {
                    command = command.replaceAll("<" + titles.get(i) + ">", "\"" + exampleData.get(i) + "\"");
                }

                processedStep.setCommand(command);

                newScenario.getSteps().add(processedStep);
            }

            newScenario.setTags(new ArrayList<String>(scenario.getTags()));

            processedScenarios.add(newScenario);
        }

        return processedScenarios;
    }

    private static List<String> processExampleString(String example) {
        return Arrays.asList(example.substring(1, example.length() - 1).split("\\|"));
    }

}

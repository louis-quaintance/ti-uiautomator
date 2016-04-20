##Ti-uiautomator

Ever wanted to write functional tests for Android apps written natively using Java or Titanium, look no further.

This project was written after finding Calabash and Appium to be very unstable. The answer is to go native for each mobile platform.

Check out the features folder for how to write cucumber like steps.

And also check out src/com/ti/uiautomator/FunctionalTestBase for common steps you can use. The Java annotations contain regexes which the framework uses to match to the steps in the feature files.

###Setup Dev Env

Download eclipse and Java

Download ant, put it on your path

Import this project

###How does it work

The framework reads the feature files and maps steps to Java methods. It looks in the sub class first e.g. HomePageTest.java and then the super class e.g. FunctionalTestBase.java

```and the user takes screenshot "homepageOK"```

will run

```@StepAnnotation(regex = ".+ takes screenshot \".+\"")
public void takesScreenshot(String screenshotId) throws IOException, InterruptedException {
```



###How to run

Grab your device's id, using the command adb devices. Lets say its 015d3b65d15c0802

Now run:

./run.sh ZX1C22H2D6 5 HomeScreen "PACKAGENAME" "ACTIVITYTOLAUNCH"

The first param is the device id
The second param is the number of seconds you wish to give the app to load up
The third param is the functional area e.g. HomeScreen, Login (note this maps to a Test class in package com.ti.uiautomator

To add a device to test, create a folder named by device id in deviceConfig, inside it create a file called bddConfig.properties and copy and ammend the settings from the other files

This is a custom BDD framework which is very similar to Cucumber, see example feature file in the features folder. The logic for it can be found in src/to/uiautomator/bdd. It uses reflection to look for annotations, first in subclass then in the FunctionalTestBase.java which contains the common operations you might wish to use.

You can add comments using the # symbol in your feature files

You can also annotate your scenarios, to run scenarios for specific device types. The annotation each device supports are found in the bddConfig.properties file.

At the end of the test, the script downloads the screenshots taken during the test and the junit report which can be helpful in jenkins.

To keep the test run short, we tend to navigate back to the starting point in the app after each scenario. This will avoid multiple restarts of the app, which can cause the tests to take too long

###Jenkins

Jenkins can run the run.sh script after building to a device
At the end of the tests, an exit code is set, 0 for Success, 1 for failure. This will cause the job to fail or succeed correctly

###Adding new functional areas

Create a new class in package com.ti.uiautomator using the following naming convention FunctionalAreaNameTest.java e.g. LoginTest

This class must override the setUp() and implement runTests() calling super.runTests() in process, see LoginTest.java for example

Then write your features in the features folder e.g. if you were adding AppSettingsAreaTest.java create a feature file named appsettingsarea.feature (note lower case)

You can now run the tests by executing command: ./run.sh 015d3b65d15c0802 8 AppSettings "PACKAGENAME" "ACTIVITYTOLAUNCH"

###uiautomator viewer

Go to your Android sdk, go inside tools folder and run ./uiautomatorviewer. This will help you find elements on screen.

Note Appcelerator always adds a '.' on the end of an accessibility label. For example a text field in Titanium like so:

```
<TextField accessibilityLabel="username" id="username"/>
```

Will need to be located in the feature file using a step like so: .... enters "jeffhaynie" into field with id "username."

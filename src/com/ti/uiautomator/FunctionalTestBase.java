package com.ti.uiautomator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import junit.framework.Assert;

import com.android.uiautomator.core.UiDevice;
import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.core.UiWatcher;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;
import com.ti.uiautomator.bdd.Runner;
import com.ti.uiautomator.bdd.StepAnnotation;
import com.ti.uiautomator.bdd.domain.DeviceDetails;
import com.ti.uiautomator.pages.HomePage;
import com.ti.uiautomator.utils.FinderUtils;
import com.ti.uiautomator.utils.KeyboardUtil;
import com.ti.uiautomator.utils.RetryCommand;
import com.ti.uiautomator.utils.ScrollUtils;

public abstract class FunctionalTestBase extends UiAutomatorTestCase {

    protected UiDevice mDevice;
    protected HomePage homePage;
    protected DeviceDetails deviceDetails = new DeviceDetails();
    protected Properties testProperties = new Properties();

    /**
     * This field can be used to setup the folder to store screenshots in for eg
     */
    public String functionalAreaName;

    public File screenshotFolder;

    public UiDevice getDevice() {
        return UiDevice.getInstance();
    }

    @Override
    public void setUp() throws Exception {
        mDevice = getDevice();
        super.setUp();
    }

    private void removeLockScreenIfItsThere() {
        UiWatcher screenLockKeypadWatcher = new UiWatcher() {
            // @Override
            public boolean checkForCondition() {

                if (deviceDetails.isSwipeToRemoveLockScreen()) {
                    swipeToUnlockForAndroid5Plus();
                }

                if (new UiObject(new UiSelector().text("Emergency call").className("android.widget.Button")).exists()
                        || new UiObject(new UiSelector().descriptionContains("Pin unlock")).exists()
                        || new UiObject(new UiSelector().textContains("ABC")).exists()) {
                    try {

                        if (deviceDetails.isSwipeToRemoveLockScreen()) {
                            new UiObject(new UiSelector().className("android.widget.TextView").text("0")).click();
                            new UiObject(new UiSelector().className("android.widget.TextView").text("5")).click();
                            new UiObject(new UiSelector().className("android.widget.TextView").text("1")).click();
                            new UiObject(new UiSelector().className("android.widget.TextView").text("1")).click();
                        } else {
                            new UiObject(new UiSelector().className("android.widget.Button").text("0")).click();
                            new UiObject(new UiSelector().className("android.widget.Button").text("5 JKL")).click();
                            new UiObject(new UiSelector().className("android.widget.Button").text("1")).click();
                            new UiObject(new UiSelector().className("android.widget.Button").text("1")).click();
                        }

                        new UiObject(new UiSelector().descriptionContains("Enter")).click();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return true;
                }
                return false;
            }

            private void swipeToUnlockForAndroid5Plus() {
                if (new UiObject(new UiSelector().description("Camera")).exists()
                        && new UiObject(new UiSelector().description("Unlock")).exists()) {

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    mDevice.swipe(100, 300, 100, 100, 5);

                }
            }
        };

        UiDevice.getInstance().registerWatcher("ScreenLockKeypadWatcher", screenLockKeypadWatcher);
        UiDevice.getInstance().runWatchers();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        mDevice.setOrientationNatural();
        mDevice.unfreezeRotation();
    }

    /**
     * The entry point for all tests
     */
    public void runTests(Object instance) throws Exception {

        functionalAreaName = instance.getClass().getSimpleName();

        screenshotFolder = new File(Constants.TEST_DIR_ON_DEVICE + functionalAreaName);
        if (!screenshotFolder.exists()) {
            screenshotFolder.mkdir();
        }

        testProperties.load(new FileInputStream(new File(Constants.TEST_DIR_ON_DEVICE + "tests.properties")));

        Properties properties = new Properties();
        properties.load(new FileInputStream(new File(Constants.TEST_DIR_ON_DEVICE + "bddConfig.properties")));

        deviceDetails.setTablet(Boolean.valueOf(properties.getProperty("isTablet")));

        deviceDetails.setSwipeToRemoveLockScreen(properties.getProperty("swipeToRemoveLockScreen") != null ? Boolean
                .valueOf(properties.getProperty("swipeToRemoveLockScreen")) : false);

        String tags = properties.getProperty("tags");
        if (tags != null && tags.length() > 0) {
            deviceDetails.setTags(Arrays.asList(tags.split(",")));
        }

        mDevice = getUiDevice();
        mDevice.wakeUp();
        removeLockScreenIfItsThere();

        homePage = new HomePage(mDevice);
        if (mDevice.getDisplayWidth() > mDevice.getDisplayHeight()) {
            mDevice.unfreezeRotation();
            mDevice.setOrientationNatural();
        }

        Runner.run(instance, deviceDetails.getTags(), screenshotFolder, mDevice);
    }

    public void clickOnItemInSidebar(final String labelText, final int offset) throws UiObjectNotFoundException {

        new RetryCommand<UiObject>(5) {

            @Override
            public UiObject command() throws Exception {

                mDevice.waitForIdle();

                UiSelector scrollView = new UiSelector().className("android.widget.ListView");
                new UiScrollable(scrollView).scrollIntoView(new UiSelector().textContains(labelText));

                UiObject child = new UiScrollable(scrollView).getChild(new UiSelector().textContains(labelText));

                Assert.assertTrue(child.exists());
                Thread.sleep(3000);
                System.out.println("Finished asserting that item in sidebar with text " + labelText + " exists");
                child.clickAndWaitForNewWindow();
                mDevice.waitForIdle();

                return child;
            }

        }.run();
    }

    public String getTitaniumFriendlyModelName() {
        return android.os.Build.MODEL;
    }

    @StepAnnotation(regex = ".+ accept the alert (\"\"|\".+\")")
    public void acceptTheAlert(String alertText) throws Exception {
        this.dismissAlert(alertText);
        mDevice.waitForIdle();
    }

    @StepAnnotation(regex = ".+ scrolls down")
    public void swipePageDown() throws UiObjectNotFoundException {
        ScrollUtils.scrollToEnd();
    }

    @StepAnnotation(regex = ".+ scrolls up")
    public void swipePageUp() throws UiObjectNotFoundException {
        ScrollUtils.scrollToBeginning();
    }

    @StepAnnotation(regex = ".+ sleeps for (\"\"|\".+\") seconds")
    public void sleepsFor(String seconds) throws Exception {
        Thread.sleep(Integer.valueOf(seconds) * 1000);
    }

    @StepAnnotation(regex = ".+ dismisses alert by clicking on the \".+\" button")
    public void dismissAlert(String alertLabel) throws Exception {
        UiObject button = new UiObject(new UiSelector().className("android.widget.Button").text(alertLabel));
        button.waitForExists(Constants.DEFAULT_UI_ELEMENT_WAIT_TIMEOUT);
        button.click();
    }

    @StepAnnotation(regex = ".+ they see alert \".+\"")
    public void seeAlertWithText(final String text) throws Exception {
        seeAlertWithTextWithRetries(text, 5);
    }

    public void seeAlertWithTextWithRetries(final String text, final int retries) throws Exception {

        new RetryCommand<UiObject>(retries) {

            @Override
            public UiObject command() throws Exception {

                mDevice.waitForIdle();

                // note trying to scroll when alert on screen causes issues
                UiObject label = new UiObject(new UiSelector().textContains(text));
                label.waitForExists(Constants.DEFAULT_UI_ELEMENT_WAIT_TIMEOUT);
                Assert.assertTrue(label.getText().contains(text));
                mDevice.waitForIdle();

                return label;
            }

        }.run();
    }

    @StepAnnotation(regex = ".+ do not see alert \".+\"")
    public void doNotSeeAlertWithText(final String text) throws Exception {
        boolean foundAlert = false;
        try {
            seeAlertWithTextWithRetries(text, 1);
            foundAlert = true;
        } catch (Throwable e) {
        }
        Assert.assertFalse("Incorrectly found alert on screen", foundAlert);
        System.out.println("Alert is correctly not displaying with text: " + text);
    }

    @StepAnnotation(regex = ".+ clicks on the device back button")
    public void clicksOnDeviceBackButton() throws Exception {
        Thread.sleep(3000);
        mDevice.pressBack();
    }

    @StepAnnotation(regex = ".+ can see text \".+\" on screen")
    public void seeText(final String text) throws Exception {

        new RetryCommand<String>(2) {

            @Override
            public String command() throws Exception {

                seesText(text);

                return "";
            }

        }.run();
    }

    private void seesText(String text) throws UiObjectNotFoundException {

        ScrollUtils.scrollIntoViewByText(text);
        System.out.println("finished scrolling");
        UiObject label = new UiObject(new UiSelector().textContains(text));
        label.waitForExists(Constants.DEFAULT_UI_ELEMENT_WAIT_TIMEOUT);
        Assert.assertTrue(label.getText().contains(text));
        mDevice.waitForIdle();
    }

    @StepAnnotation(regex = ".+ can see text \".+\" on screen with exact match")
    public void seeTextWithExactMatch(String text) throws Exception {
        ScrollUtils.scrollIntoViewByTextMatchingExactly(text);
        UiObject label = new UiObject(new UiSelector().text(text));
        label.waitForExists(Constants.DEFAULT_UI_ELEMENT_WAIT_TIMEOUT);
        Assert.assertTrue(label.getText().equals(text));
        mDevice.waitForIdle();
    }

    @StepAnnotation(regex = ".+ can see text \".+\"")
    public void userCanSeeText(String text) throws Exception {
        seeText(text);
    }

    @StepAnnotation(regex = ".+ can see text \".+\" in list view")
    public UiObject seeTextInListView(final String text) throws Exception {

        return new RetryCommand<UiObject>(1) {

            @Override
            public UiObject command() throws Exception {

                UiObject label = seesTextInListView(text);

                return label;
            }

        }.run();
    }

    private UiObject seesTextInListView(final String text) throws UiObjectNotFoundException {
        ScrollUtils.scrollIntoViewByTextInListView(text);
        UiObject label = new UiObject(new UiSelector().textContains(text));
        label.waitForExists(Constants.DEFAULT_UI_ELEMENT_WAIT_TIMEOUT);
        Assert.assertTrue(label.getText().contains(text));
        mDevice.waitForIdle();
        return label;
    }

    @StepAnnotation(regex = ".+ clicks on view with text \".+\" in list view")
    public void clicksOnViewWithTextInListView(final String text) throws Exception {

        new RetryCommand<UiObject>(1) {

            @Override
            public UiObject command() throws Exception {

                UiObject label = seesTextInListView(text);
                Thread.sleep(3000);
                label.click();
                mDevice.waitForIdle();
                return label;
            }

        }.run();

    }

    @StepAnnotation(regex = ".+ cannot see text \".+\" in list view")
    public void userCannotSeeTextInListView(final String text) throws Exception {
        new RetryCommand<String>(1) {
            private boolean seenText = false;

            @Override
            public String command() throws Exception {

                try {
                    seesTextInListView(text);
                    seenText = true;
                } catch (Throwable e) {
                    System.out.println("Correctly not found text");
                }

                if (seenText) {
                    throw new Error("The text '" + text + "' is present!");
                } else {
                    return "";
                }
            }
        }.run();
    }

    @StepAnnotation(regex = ".+ cannot see text \".+\" on screen")
    public void userCannotSeeText(final String text) throws Exception {
        new RetryCommand<String>(1) {
            private boolean seenText = false;

            @Override
            public String command() throws Exception {

                try {
                    seesText(text);
                    seenText = true;
                } catch (Throwable e) {
                    System.out.println("Correctly not found text");
                }

                if (seenText) {
                    throw new Error("The text '" + text + "' is present!");
                } else {
                    return "";
                }

            }
        }.run();
    }

    @StepAnnotation(regex = ".+ takes screenshot \".+\"")
    public void takesScreenshot(String screenshotId) throws IOException, InterruptedException {
        mDevice.waitForIdle();
        captureScreenshot(getDevice(), new File(screenshotFolder, screenshotId + ".png"));
    }

    private void captureScreenshot(UiDevice mDevice, File screenshotFile) {
        if (screenshotFile.exists()) {
            try {
                screenshotFile.delete();
            } catch (Exception e) {
            }
        }
        mDevice.takeScreenshot(screenshotFile);
    }

    @StepAnnotation(regex = ".+ clicks on view with id \".+\"")
    public void clicksOnViewWithId(String id) throws Exception {
        Thread.sleep(2000);
        UiObject view = FinderUtils.findById(getDevice(), id);
        FinderUtils.sleepUntilTiEventsAdded();
        view.clickAndWaitForNewWindow();
        mDevice.waitForIdle();
    }

    @StepAnnotation(regex = ".+ clicks on view with id \".+\" if it exists")
    public void clicksOnViewWithIdIfItExists(String id) throws Exception {
        Thread.sleep(2000);
        try {
            UiObject view = FinderUtils.findById(getDevice(), id);
            FinderUtils.sleepUntilTiEventsAdded();
            view.clickAndWaitForNewWindow();
        } catch (Throwable e) {
            // deliberately swallow
        }

        mDevice.waitForIdle();
    }

    @StepAnnotation(regex = ".+ clicks on view with text \".+\"")
    public void clicksOnViewWithText(String text) throws Exception {
        Thread.sleep(2000);
        UiObject view = FinderUtils.findByText(mDevice, text);
        FinderUtils.sleepUntilTiEventsAdded();
        view.clickAndWaitForNewWindow();
        mDevice.waitForIdle();
    }

    @StepAnnotation(regex = ".+ clicks on view with text \".+\" matching exactly")
    public void clicksOnViewWithTextMatchingExactly(String text) throws Exception {
        Thread.sleep(2000);
        UiObject view = FinderUtils.findByTextMatchingExactly(mDevice, text);
        FinderUtils.sleepUntilTiEventsAdded();
        view.clickAndWaitForNewWindow();
        mDevice.waitForIdle();
    }

    @StepAnnotation(regex = ".+ swipes \"(left|right)\" on view with id \".+\"")
    public void swipesLeftOnViewWithID(String direction, String id) throws Exception {
        Thread.sleep(2000);
        UiObject view = FinderUtils.findById(mDevice, id);
        FinderUtils.sleepUntilTiEventsAdded();

        if (direction.equals("left")) {
            Assert.assertTrue("Swipe should be swiped", view.swipeLeft(100));
        } else {
            Assert.assertTrue("Swipe should be swiped", view.swipeRight(100));
        }

        Thread.sleep(500);

        mDevice.waitForIdle();
    }

    @StepAnnotation(regex = ".+ enters (\"\"|\".+\") into field with id \".+\"")
    public void enterTextIntoField(final String text, final String id) throws Exception {

        new RetryCommand<UiObject>(5) {

            @Override
            public UiObject command() throws Exception {

                mDevice.waitForIdle();

                UiObject view = KeyboardUtil.clearField(id);

                Thread.sleep(2000);

                view.clickBottomRight();

                view.setText(text);
                // when still in edit mode the text is prefixed with Editing. so we do an endsWith
                // check

                Thread.sleep(2000);

                return view;
            }

        }.run();
    }

    @StepAnnotation(regex = ".+ enters (\"\"|\".+\") into password field with id \".+\"")
    public void enterTextIntoPasswordField(final String text, final String id) throws Exception {

        new RetryCommand<UiObject>(5) {

            @Override
            public UiObject command() throws Exception {

                mDevice.waitForIdle();

                UiObject view = FinderUtils.findById(getDevice(), id);

                String currentText = view.getText();

                // brings up keyboard
                view.clickBottomRight();

                for (int i = 0; i < currentText.length(); i++) {
                    UiDevice.getInstance().pressDelete();
                }

                view.setText(text);

                return view;
            }

        }.run();
    }

    @StepAnnotation(regex = ".+ clears the field with id \".+\"")
    public void clearsFieldWithId(final String id) throws Exception {
        KeyboardUtil.clearsFieldWithId(id);
    }

    @StepAnnotation(regex = ".+ the view with id \".+\" is no longer visible")
    public void viewWithIdNotAvailable(String id) throws Exception {
        assertTrue("View with id " + id + " exists erroneously", FinderUtils.doesNotExist(getDevice(), id));
    }

    @StepAnnotation(regex = ".+ cannot see a view with id \".+\"")
    public void userCannotSeeAViewWithId(String id) throws Exception {
        viewWithIdNotAvailable(id);
    }

    @StepAnnotation(regex = ".+ the view with id \".+\" is visible")
    public void viewWithIdIsAvailable(String id) throws Exception {
        FinderUtils.findById(getDevice(), id);
    }

    @StepAnnotation(regex = ".+ the webview with id \".+\" is visible")
    public void webViewWithIdIsAvailable(String id) throws Exception {
        FinderUtils.findWebKitWebViewById(getDevice(), id);
    }

    @StepAnnotation(regex = ".+ the image view with id \".+\" is visible")
    public void imageViewWithIdIsAvailable(String id) throws Exception {
        FinderUtils.findImageViewById(getDevice(), id);
    }

    @StepAnnotation(regex = ".+ the view with id \".+\" with text \".+\" is visible")
    public void viewWithIdIsAvailable(String id, String text) throws Exception {
        FinderUtils.findById(getDevice(), id).getText().equalsIgnoreCase(text);
    }

}

package com.ti.uiautomator.utils;

import com.android.uiautomator.core.UiDevice;
import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;
import com.ti.uiautomator.Constants;

public class FinderUtils {

    private static int NO_OF_RETRIES = 5;

    public static void sleepUntilTiEventsAdded() throws InterruptedException {
        Thread.sleep(5000);
    }

    public static UiObject findById(final UiDevice uiDevice, final String id) throws UiObjectNotFoundException,
            InterruptedException {

        return new RetryCommand<UiObject>(NO_OF_RETRIES) {

            @Override
            public UiObject command() throws Exception {
                ScrollUtils.scrollIntoViewById(id);

                UiObject link =
                        waitToExist(uiDevice, new UiObject(new UiSelector().description(id)),
                                Constants.DEFAULT_UI_ELEMENT_WAIT_TIMEOUT);
                return link;
            }

        }.run();
    }

    public static UiObject findWebKitWebViewById(final UiDevice uiDevice, final String id) throws UiObjectNotFoundException,
    InterruptedException {

    	return new RetryCommand<UiObject>(NO_OF_RETRIES) {

    @Override
    public UiObject command() throws Exception {
        ScrollUtils.scrollIntoWebKitWebViewById(id);

        UiObject link =
                waitToExist(uiDevice, new UiObject(new UiSelector().description(id)),
                        Constants.DEFAULT_UI_ELEMENT_WAIT_TIMEOUT);
        return link;
    	}

    	}.run();
	}

    public static UiObject findImageViewById(final UiDevice uiDevice, final String id) throws UiObjectNotFoundException,
    InterruptedException {

    	return new RetryCommand<UiObject>(NO_OF_RETRIES) {

    @Override
    public UiObject command() throws Exception {
        ScrollUtils.scrollIntoImageViewById(id);

        UiObject link =
                waitToExist(uiDevice, new UiObject(new UiSelector().description(id)),
                        Constants.DEFAULT_UI_ELEMENT_WAIT_TIMEOUT);
        return link;
    	}

    	}.run();
	}

    public static UiObject waitToExist(UiDevice uiDevice, UiObject uiObject, long timeout) {

        uiDevice.waitForIdle();

        if (uiObject.waitForExists(timeout)) {
            return uiObject;
        }
        throw new RuntimeException("Element could not be found " + uiObject);
    }

    public static boolean doesNotExist(UiDevice uiDevice, String id) {
        try {
            ScrollUtils.scrollIntoViewById(id);

            waitToExist(uiDevice, new UiObject(new UiSelector().description(id)),
                    Constants.DEFAULT_UI_ELEMENT_WAIT_TIMEOUT);
            System.out.println("View with id " + id + " has incorrectly been found unexpectedly");
            return false;
        } catch (Throwable e) {
            System.out.println("View with id " + id + " has correctly not been found");
            return true;
        }
    }

    public static UiObject findById(UiDevice uiDevice, String id, boolean retryOnce) throws Exception {
        return findById(uiDevice, id);
    }

    public static UiObject findByText(final UiDevice uiDevice, final String text) throws Exception {

        return new RetryCommand<UiObject>(NO_OF_RETRIES) {

            @Override
            public UiObject command() throws Exception {
                ScrollUtils.scrollIntoViewByText(text);
                return waitToExist(uiDevice, new UiObject(new UiSelector().textContains(text)),
                        Constants.DEFAULT_UI_ELEMENT_WAIT_TIMEOUT);
            }

        }.run();
    }

    public static UiObject findByText(UiDevice uiDevice, String text, boolean retryOnce) throws Exception {
        return findByText(uiDevice, text);
    }

    public static UiObject findByTextMatchingExactly(final UiDevice uiDevice, final String text) {
        return new RetryCommand<UiObject>(NO_OF_RETRIES) {

            @Override
            public UiObject command() throws Exception {
                ScrollUtils.scrollIntoViewByTextMatchingExactly(text);
                return waitToExist(uiDevice, new UiObject(new UiSelector().text(text)),
                        Constants.DEFAULT_UI_ELEMENT_WAIT_TIMEOUT);
            }

        }.run();
    }
}

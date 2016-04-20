package com.ti.uiautomator.utils;

import com.android.uiautomator.core.UiDevice;
import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;

public class KeyboardUtil {

    public static UiObject clearField(final String id) throws UiObjectNotFoundException, InterruptedException {
        UiDevice.getInstance().waitForIdle();

        UiObject view = FinderUtils.findById(UiDevice.getInstance(), id);

        String currentText = view.getText();

        System.out.println(currentText);

        view.clearTextField();

        view.longClick();
        deleteText(currentText);

        currentText = view.getText();

        System.out.println("Text after deletion: " + currentText);

        // try twice to be sure, this is flaky in uiautomator
        view.longClick();
        deleteText(currentText);

        System.out.println("Text after deletion: " + currentText);

        Thread.sleep(2000);
        return view;
    }

    public static UiObject clearsFieldWithId(final String id) throws Exception {

        return new RetryCommand<UiObject>(5) {

            @Override
            public UiObject command() throws Exception {
                return clearField(id);
            }
        }.run();
    }

    public static void deleteText(String currentText) {
        for (int i = 0; i < currentText.length(); i++) {
            UiDevice.getInstance().pressDelete();
        }
    }
}

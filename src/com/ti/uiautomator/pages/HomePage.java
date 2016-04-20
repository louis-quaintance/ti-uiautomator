package com.ti.uiautomator.pages;

import com.android.uiautomator.core.UiDevice;
import com.android.uiautomator.core.UiObject;

public class HomePage extends BasePage {

    public HomePage(UiDevice uiDevice) {
        super(uiDevice);
    }

    public UiObject getLoginTile() throws Exception {
        return findById("signInTile.");
    }
}

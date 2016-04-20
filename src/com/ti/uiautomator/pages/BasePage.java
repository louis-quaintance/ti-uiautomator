package com.ti.uiautomator.pages;

import com.android.uiautomator.core.UiDevice;
import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.ti.uiautomator.utils.FinderUtils;

public abstract class BasePage {

    protected final UiDevice uiDevice;

    public BasePage(UiDevice uiDevice) {
        this.uiDevice = uiDevice;
    }

    public void sleepUntilTiEventsAdded() throws InterruptedException {
        FinderUtils.sleepUntilTiEventsAdded();
    }

    public UiObject findById(String id) throws UiObjectNotFoundException, InterruptedException {
        return FinderUtils.findById(uiDevice, id);
    }

    public UiObject waitToExist(UiObject uiObject, long timeout) {
        return FinderUtils.waitToExist(uiDevice, uiObject, timeout);
    }
}

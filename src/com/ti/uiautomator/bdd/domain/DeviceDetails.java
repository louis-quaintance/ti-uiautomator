package com.ti.uiautomator.bdd.domain;

import java.util.ArrayList;
import java.util.List;

public class DeviceDetails {

    private boolean isTablet;

    private boolean swipeToRemoveLockScreen;

    private List<String> tags = new ArrayList<String>();

    public boolean isTablet() {
        return isTablet;
    }

    public void setTablet(boolean isTablet) {
        this.isTablet = isTablet;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public boolean isSwipeToRemoveLockScreen() {
        return swipeToRemoveLockScreen;
    }

    public void setSwipeToRemoveLockScreen(boolean swipeToRemoveLockScreen) {
        this.swipeToRemoveLockScreen = swipeToRemoveLockScreen;
    }

}

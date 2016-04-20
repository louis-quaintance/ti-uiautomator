package com.ti.uiautomator.utils;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;

public class ScrollUtils {

    public static void scrollToEnd() throws UiObjectNotFoundException {
        UiSelector scrollView = new UiSelector().className("android.widget.ScrollView");
        if (new UiObject(scrollView).exists() && new UiObject(scrollView).isScrollable()) {
            new UiScrollable(scrollView.scrollable(true)).scrollToEnd(50);
        }
    }

    public static void scrollToBeginning() throws UiObjectNotFoundException {
        UiSelector scrollView = new UiSelector().className("android.widget.ScrollView");
        if (new UiObject(scrollView).exists() && new UiObject(scrollView).isScrollable()) {
            new UiScrollable(scrollView.scrollable(true)).scrollToBeginning(50);
        }
    }

    public static void scrollIntoViewById(String id) throws UiObjectNotFoundException {
        UiSelector scrollView = new UiSelector().className("android.widget.ScrollView");
        if (new UiObject(scrollView).exists() && new UiObject(scrollView).isScrollable()) {
            new UiScrollable(scrollView.scrollable(true)).scrollIntoView(new UiSelector().description(id));
        }
    }

    public static void scrollIntoViewByText(String text) throws UiObjectNotFoundException {
        UiSelector scrollView = new UiSelector().className("android.widget.ScrollView");
        if (new UiObject(scrollView).exists() && new UiObject(scrollView).isScrollable()) {
            new UiScrollable(scrollView.scrollable(true)).scrollIntoView(new UiSelector().textContains(text));
        }
    }

    public static void scrollIntoViewByTextMatchingExactly(String text) throws UiObjectNotFoundException {
        UiSelector scrollView = new UiSelector().className("android.widget.ScrollView");
        if (new UiObject(scrollView).exists() && new UiObject(scrollView).isScrollable()) {
            new UiScrollable(scrollView.scrollable(true)).scrollIntoView(new UiSelector().text(text));
        }
    }

    public static void scrollIntoViewByTextInListView(String text) throws UiObjectNotFoundException {
        UiSelector scrollView = new UiSelector().className("android.widget.ListView");
        if (new UiObject(scrollView).exists() && new UiObject(scrollView).isScrollable()) {
            new UiScrollable(scrollView.scrollable(true)).scrollIntoView(new UiSelector().textContains(text));
        }
    }
    
    public static void scrollIntoWebKitWebViewById(String id) throws UiObjectNotFoundException {
        UiSelector scrollView = new UiSelector().className("android.webkit.WebView");
        if (new UiObject(scrollView).exists() && new UiObject(scrollView).isScrollable()) {
            new UiScrollable(scrollView.scrollable(true)).scrollIntoView(new UiSelector().description(id));
        }
    }
    
    public static void scrollIntoImageViewById(String id) throws UiObjectNotFoundException {
        UiSelector scrollView = new UiSelector().className("android.widget.Image");
        if (new UiObject(scrollView).exists() && new UiObject(scrollView).isScrollable()) {
            new UiScrollable(scrollView.scrollable(true)).scrollIntoView(new UiSelector().description(id));
        }
    }
}

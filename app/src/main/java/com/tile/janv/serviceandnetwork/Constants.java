package com.tile.janv.serviceandnetwork;

/**
 * Created by janv on 18-Dec-15.
 */
public class Constants {

    private static String[] drawerSections = {
            "funny",
            "art",
            "movies",
            "books",
            "philosophy",
            "books",
            "marathons"
    };

    private Constants() {
    }

    public static String[] getDrawerSections() {
        return drawerSections;
    }

    public static String getSubreddit(int index) {
        return drawerSections[index];
    }
}

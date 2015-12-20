package com.tile.janv.redditviewer;

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
            "pics",
            "worldnews",
            "sports",
            "movies",
            "marathons"
    };

    /**
     * The number of Reddit items that are requested (maximum) on list call.
     */
    private static int POSTS_LIMIT = 20;

    private Constants() {
    }

    public static String[] getDrawerSections() {
        return drawerSections;
    }

    public static String getSubreddit(int index) {
        return drawerSections[index];
    }

    public static int getPostsLimit() {
        return POSTS_LIMIT;
    }

    public static void setPostsLimit(int postsLimit) {
        POSTS_LIMIT = postsLimit;
    }
}

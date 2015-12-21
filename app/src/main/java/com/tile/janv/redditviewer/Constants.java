package com.tile.janv.redditviewer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by janv on 18-Dec-15.
 */
public class Constants {

    private static List<SubredditsChangedListener> subredditsChangedListenerList = new ArrayList<>();

    private static String[] subreddits = {
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

    public static String[] getSubreddits() {
        return subreddits;
    }

    public static void setSubreddits(String[] subreddits) {
        Constants.subreddits = subreddits;
        for (SubredditsChangedListener listener : subredditsChangedListenerList) {
            listener.subredditsChanged();
        }
    }

    public static String getSubreddit(int index) {
        return subreddits[index];
    }

    public static int getPostsLimit() {
        return POSTS_LIMIT;
    }

    public static void setPostsLimit(int postsLimit) {
        POSTS_LIMIT = postsLimit;
    }

    public interface SubredditsChangedListener {
        void subredditsChanged();
    }

    public static void addSubredditChangedListener(SubredditsChangedListener listener) {
        subredditsChangedListenerList.add(listener);
    }

    public static void removeSubredditChangedListener(SubredditsChangedListener listener) {
        subredditsChangedListenerList.remove(listener);
    }
}

package com.tile.janv.serviceandnetwork.gson;

import com.tile.janv.serviceandnetwork.RedditListElement;

import java.util.List;

/**
 * Created by janv on 19-Dec-15.
 */
public class RedditListingResponseForGson {
    public String kind; //should be "Listing"
    public Inner data;

    public RedditListingResponseForGson() {
    }

    public static class Inner {
        public String after;
        public String before;
        public List<RedditListElement> children;

        public Inner() {
        }

    }

}

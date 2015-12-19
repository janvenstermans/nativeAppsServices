package com.tile.janv.serviceandnetwork.gson;

import com.tile.janv.serviceandnetwork.RedditListElement;

import java.util.List;

/**
 * Created by janv on 19-Dec-15.
 */
public class RedditListingData {
    public String after;
    public String before;
    public List<RedditDataContainer<RedditListElement>> children;

    public RedditListingData() {
    }
}

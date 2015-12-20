package com.tile.janv.serviceandnetwork.gson;

import com.tile.janv.serviceandnetwork.RedditListElement;
import com.tile.janv.serviceandnetwork.RedditService;

import java.util.List;

/**
 * Information returned on calling
 * {@link com.tile.janv.serviceandnetwork.RedditService#getSubredditList(String, String, RedditService.Callback)}
 *
 * Created by janv on 20-Dec-15.
 */
public class SubredditListResult {
    public String after;
    public List<RedditListElement> redditListElementList;
}

package com.tile.janv.redditviewer.gson;

import com.tile.janv.redditviewer.RedditListElement;
import com.tile.janv.redditviewer.RedditService;

import java.util.List;

/**
 * Information returned on calling
 * {@link RedditService#getSubredditList(String, String, RedditService.Callback)}
 *
 * Created by janv on 20-Dec-15.
 */
public class SubredditListResult {
    public String after;
    public List<RedditListElement> redditListElementList;
}

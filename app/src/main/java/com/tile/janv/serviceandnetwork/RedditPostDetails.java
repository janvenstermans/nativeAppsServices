package com.tile.janv.serviceandnetwork;

/**
 * Info of Reddit item for details view.
 * Not using getters and setters for faster android response
 *
 * Created by janv on 19-Dec-15.
 */
public class RedditPostDetails extends RedditListElement {

    public int commentsCount;
    public String previewImageUrl;

    public RedditPostDetails() {
    }

    public void copyListElement(RedditListElement element) {
        this.id = element.id;
        this.title = element.title;
        this.author = element.author;
        this.link = element.link;
        this.thumbnail = element.thumbnail;
        this.subreddit = element.subreddit;
        this.permalink = element.permalink;
    }
}

package com.tile.janv.serviceandnetwork;

/**
 * Info of Reddit item for list view.
 * Not using getters and setters for faster android response
 *
 * Created by janv on 19-Dec-15.
 */
public class RedditListElement {
    // reddit id
    public String id;
    public String title;
    public String author;
    public String link;
    public String thumbnail;

    public RedditListElement() {
    }
}

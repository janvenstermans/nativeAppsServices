package com.tile.janv.redditviewer;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

public class RedditViewerGenerator {

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(1, "com.tile.janv.redditviewer.model");

        Entity subreddit = schema.addEntity("Subreddit");
        subreddit.addIdProperty();
        subreddit.addStringProperty("name");

        Entity redditPost = schema.addEntity("RedditPost");
        redditPost.addIdProperty();
        redditPost.addStringProperty("redditId");
        redditPost.addStringProperty("title");
        redditPost.addStringProperty("author");
        redditPost.addStringProperty("link");
        redditPost.addStringProperty("thumbnail");
        redditPost.addStringProperty("permalink");

        // one to many in two steps
        Property subredditId = redditPost.addLongProperty("subredditId").getProperty();
        redditPost.addToOne(subreddit, subredditId);
        ToMany subredditToPost = subreddit.addToMany(redditPost, subredditId);
        subredditToPost.setName("posts");

        new DaoGenerator().generateAll(schema, "../app/src/main/java");
    }
}

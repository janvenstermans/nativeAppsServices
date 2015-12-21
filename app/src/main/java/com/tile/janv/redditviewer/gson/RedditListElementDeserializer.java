package com.tile.janv.redditviewer.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.tile.janv.redditviewer.RedditListElement;

import java.lang.reflect.Type;

/**
 * {@link JsonDeserializer} implementation for {@link RedditListElement}.
 *
 * Created by janv on 20-Dec-15.
 */
public class RedditListElementDeserializer implements JsonDeserializer<RedditListElement> {
    @Override
    public RedditListElement deserialize(JsonElement json, Type typeOfT,
                                           JsonDeserializationContext context)
            throws JsonParseException {

        final JsonObject jsonObject = json.getAsJsonObject();
        GsonUtil.checkKind(jsonObject, "t3");

        final JsonObject elementData = jsonObject.get("data").getAsJsonObject();
        final String idString = GsonUtil.getStringElementValue(elementData, "id");
        final String titleString = GsonUtil.getStringElementValue(elementData, "title");
        final String authorString = GsonUtil.getStringElementValue(elementData, "author");
        final String linkString = GsonUtil.getStringElementValue(elementData, "link");
        final String thumbnailString = GsonUtil.getStringElementValue(elementData, "thumbnail");
        final String subredditString = GsonUtil.getStringElementValue(elementData, "subreddit");
        final String permalinkString = GsonUtil.getStringElementValue(elementData, "permalink");

        final RedditListElement result = new RedditListElement();
        result.id = idString;
        result.title = titleString;
        result.author = authorString;
        result.link = linkString;
        result.thumbnail = thumbnailString;
        result.subreddit = subredditString;
        result.permalink = permalinkString;
        return result;
    }
}

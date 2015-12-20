package com.tile.janv.serviceandnetwork.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.tile.janv.serviceandnetwork.RedditListElement;

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

        final JsonObject listingData = jsonObject.get("data").getAsJsonObject();
        final String idString = GsonUtil.getStringElementValue(listingData, "id");
        final String titleString = GsonUtil.getStringElementValue(listingData, "title");
        final String authorString = GsonUtil.getStringElementValue(listingData, "author");
        final String linkString = GsonUtil.getStringElementValue(listingData, "link");
        final String thumbnailString = GsonUtil.getStringElementValue(listingData, "thumbnail");

        final RedditListElement result = new RedditListElement();
        result.id = idString;
        result.title = titleString;
        result.author = authorString;
        result.link = linkString;
        result.thumbnail = thumbnailString;
        return result;
    }
}

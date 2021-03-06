package com.tile.janv.redditviewer.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tile.janv.redditviewer.RedditListElement;
import com.tile.janv.redditviewer.RedditPostDetails;

/**
 * Created by janv on 20-Dec-15.
 */
public final class GsonUtil {

    private static Gson gson;

    private GsonUtil() {
    }

    public static void checkKind(JsonObject jsonObject, String expected) {
        if (!expected.equals(getStringElementValue(jsonObject, "kind"))) {
            throw new IllegalArgumentException();
        }
    }

    public static String getStringElementValue(JsonObject jsonObject, String element) {
        if (jsonObject != null) {
            JsonElement elementObject = jsonObject.get(element);
            if (elementObject != null) {
                return elementObject.getAsString();
            }
        }
        return null;
    }

    public static JsonObject getElementAsObject(JsonObject jsonObject, String element) {
        if (jsonObject != null) {
            JsonElement elementObject = jsonObject.get(element);
            if (elementObject != null) {
                return elementObject.getAsJsonObject();
            }
        }
        return null;
    }

    public static JsonArray getElementAsArrray(JsonObject jsonObject, String element) {
        if (jsonObject != null) {
            JsonElement elementObject = jsonObject.get(element);
            if (elementObject != null) {
                return elementObject.getAsJsonArray();
            }
        }
        return null;
    }

    public static Gson getGson() {
        //lazy initialization
        if (gson == null) {
            final GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(SubredditListResult.class, new SubredditListResultDeserializer());
            gsonBuilder.registerTypeAdapter(RedditListElement.class, new RedditListElementDeserializer());
            gsonBuilder.registerTypeAdapter(RedditPostDetails.class, new RedditPostDetailsDeserializer());
            gson = gsonBuilder.create();
        }
        return gson;
    }
}

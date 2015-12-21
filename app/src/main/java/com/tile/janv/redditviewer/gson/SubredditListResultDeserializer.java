package com.tile.janv.redditviewer.gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.tile.janv.redditviewer.RedditListElement;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link JsonDeserializer} implementation for {@link SubredditListResult}.
 *
 * Created by janv on 20-Dec-15.
 */
public class SubredditListResultDeserializer  implements JsonDeserializer<SubredditListResult> {
    @Override
    public SubredditListResult deserialize(JsonElement json, Type typeOfT,
                                           JsonDeserializationContext context)
            throws JsonParseException {

        final JsonObject jsonObject = json.getAsJsonObject();
        GsonUtil.checkKind(jsonObject, "Listing");

        final JsonObject listingData = jsonObject.get("data").getAsJsonObject();
        final String afterString = GsonUtil.getStringElementValue(listingData, "after");

        final JsonArray childrenArray = listingData.get("children").getAsJsonArray();
        final List<RedditListElement> listElements = new ArrayList<>();
        for (int i = 0; i < childrenArray.size(); i++) {
            final JsonElement jsonElement = childrenArray.get(i);
            RedditListElement element = context.deserialize(jsonElement, RedditListElement.class);
            listElements.add(element);
        }

        final SubredditListResult result = new SubredditListResult();
        result.after = afterString;
        result.redditListElementList = listElements;
        return result;
    }
}

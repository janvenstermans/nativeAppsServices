package com.tile.janv.redditviewer.gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.tile.janv.redditviewer.RedditListElement;
import com.tile.janv.redditviewer.RedditPostDetails;

import java.lang.reflect.Type;

/**
 * {@link JsonDeserializer} implementation for {@link RedditPostDetails}.
 *
 * Created by janv on 20-Dec-15.
 */
public class RedditPostDetailsDeserializer implements JsonDeserializer<RedditPostDetails> {
    @Override
    public RedditPostDetails deserialize(JsonElement json, Type typeOfT,
                                           JsonDeserializationContext context)
            throws JsonParseException {

        final JsonArray listingArray = json.getAsJsonArray();
        final JsonObject detailsListing = listingArray.get(0).getAsJsonObject();
        GsonUtil.checkKind(detailsListing, "Listing");
        final JsonObject commentsListing = listingArray.get(0).getAsJsonObject();
        GsonUtil.checkKind(commentsListing, "Listing");

        //details
        final JsonObject detailsListingData = detailsListing.get("data").getAsJsonObject();
        final JsonArray detailsChildrenArray = detailsListingData.get("children").getAsJsonArray();
        if (detailsChildrenArray.size() != 1) {
            throw new IllegalArgumentException("expect only one post, got " + detailsChildrenArray.size());
        }
        final JsonElement jsonPostDetails = detailsChildrenArray.get(0);
        final RedditListElement element = context.deserialize(jsonPostDetails, RedditListElement.class);
        // extra elements for details view
        final JsonObject jsonPostDetailsObject = jsonPostDetails.getAsJsonObject();
        GsonUtil.checkKind(jsonPostDetailsObject, "t3");
        final JsonObject elementData = jsonPostDetailsObject.get("data").getAsJsonObject();
        final String previewImageUrl = getPreviewImageUrl(elementData);

        //comments
        final JsonObject commentsListingData = commentsListing.get("data").getAsJsonObject();
        final JsonArray commentsChildrenArray = commentsListingData.get("children").getAsJsonArray();
        final int commentsCount = commentsChildrenArray.size();

        final RedditPostDetails result = new RedditPostDetails();
        result.copyListElement(element);
        result.commentsCount = commentsCount;
        result.previewImageUrl = previewImageUrl    ;
        return result;
    }

    /**
     *
     * @param elementData json object of the data of the post, i.e. level containing id, author, etc...
     * @return
     */
    private String getPreviewImageUrl (JsonObject elementData) {
        final JsonObject preview = GsonUtil.getElementAsObject(elementData, "preview");
        if (preview != null) {
            final JsonArray images = GsonUtil.getElementAsArrray(preview, "images");
            if (images.size() > 0) {
                // only return first
                final JsonObject image = images.get(0).getAsJsonObject();
                final JsonObject source = GsonUtil.getElementAsObject(image, "source");
                if (source != null) {
                    return GsonUtil.getStringElementValue(source, "url");
                }
            }
        }
        return null;
    }
}

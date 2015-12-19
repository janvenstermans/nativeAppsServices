package com.tile.janv.serviceandnetwork;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.tile.janv.serviceandnetwork.gson.GsonRequest;
import com.tile.janv.serviceandnetwork.gson.RedditListingResponseForGson;

import java.util.List;

/**
 * Service like class for getting Reddit responses.
 * Not yet an android class.
 *
 * Created by janv on 19-Dec-15.
 */
public class RedditService {

    private static Gson gson;

    private RequestQueue requestQueue;

    public RedditService(RequestQueue requestQueue) {
        this.requestQueue = requestQueue;
    }

    public void getSubredditList(String subreddit, final Callback<List<RedditListElement>> callback) {
        GsonRequest jsonObjectRequest = new GsonRequest(
                Request.Method.GET, String.format("https://www.reddit.com/r/%s.json", subreddit),
                RedditListingResponseForGson.class,
                new Response.Listener<RedditListingResponseForGson>() {
                    @Override
                    public void onResponse(RedditListingResponseForGson response) {
                        callback.onSuccess(response.data.children);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error);
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    public interface Callback<T> {
        void onSuccess(T result);
        void onError(VolleyError error);
    }

    public static Gson getGson() {
        //lazy initialization
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }
}

package com.tile.janv.serviceandnetwork;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.google.gson.Gson;
import com.tile.janv.serviceandnetwork.gson.GsonRequest;
import com.tile.janv.serviceandnetwork.gson.SubredditListResult;

/**
 * Service like class for getting Reddit responses.
 * Not yet an android class.
 *
 * Created by janv on 19-Dec-15.
 */
public class RedditService {

    private RequestQueue requestQueue;

    public RedditService(RequestQueue requestQueue) {
        this.requestQueue = requestQueue;
    }

    public void getSubredditList(String subreddit, String after, final Callback<SubredditListResult> callback) {
        String url = String.format("https://www.reddit.com/r/%s.json?limit=%d", subreddit, Constants.getPostsLimit());
        if (after != null) {
            url += "&after=" + after;
        }
        GsonRequest jsonObjectRequest = new GsonRequest(
                Request.Method.GET, url, SubredditListResult.class,
                new Response.Listener<SubredditListResult>() {
                    @Override
                    public void onResponse(SubredditListResult response) {
                        callback.onSuccess(response);
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

    public void getImage(String imageUrl, int maxWidth, int maxHeight,
                         ImageView.ScaleType scaleType, final Callback<Bitmap> callback) {
        ImageRequest imageRequest = new ImageRequest(imageUrl,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        callback.onSuccess(bitmap);
                    }
                }, maxWidth, maxHeight, scaleType, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error);
                    }
                });
        requestQueue.add(imageRequest);
    }

    public interface Callback<T> {
        void onSuccess(T result);
        void onError(VolleyError error);
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}

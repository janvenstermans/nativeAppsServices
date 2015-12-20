package com.tile.janv.serviceandnetwork;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.tile.janv.serviceandnetwork.gson.GsonRequest;
import com.tile.janv.serviceandnetwork.gson.SubredditListResult;

/**
 * Service like class for getting Reddit responses.
 * Not yet an android class.
 *
 * Created by janv on 19-Dec-15.
 */
public class RedditService {

    private static final String LOGGING_TAG = "RedditService";

    private RequestQueue requestQueue;

    public RedditService(RequestQueue requestQueue) {
        this.requestQueue = requestQueue;
    }

    public void getSubredditList(String subreddit, String after, final Callback<SubredditListResult> callback) {
        StringBuilder sb = new StringBuilder(String.format("https://www.reddit.com/r/%s.json?limit=%d", subreddit, Constants.getPostsLimit()));
        if (after != null) {
            sb.append("&after=" + after);
        }
        final String url = sb.toString();
        Log.i(LOGGING_TAG, String.format("call GET " + url));
        GsonRequest jsonObjectRequest = new GsonRequest(
                Request.Method.GET, url, SubredditListResult.class,
                new Response.Listener<SubredditListResult>() {
                    @Override
                    public void onResponse(SubredditListResult response) {
                        Log.i(LOGGING_TAG, String.format("successful GET " + url));
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(LOGGING_TAG, String.format("unsuccessful GET " + url), error);
                        callback.onError(error);
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    public void getSubredditPost(String subreddit, String postId, final Callback<RedditPostDetails> callback) {
        final String url = String.format("https://www.reddit.com/r/%s/%s.json", subreddit, postId);
        Log.i(LOGGING_TAG, String.format("call GET " + url));
        GsonRequest jsonObjectRequest = new GsonRequest(
                Request.Method.GET, url, RedditPostDetails.class,
                new Response.Listener<RedditPostDetails>() {
                    @Override
                    public void onResponse(RedditPostDetails response) {
                        Log.i(LOGGING_TAG, String.format("successful GET " + url));
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(LOGGING_TAG, String.format("unsuccessful GET " + url), error);
                        callback.onError(error);
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    public void getImage(final String imageUrl, int maxWidth, int maxHeight,
                         ImageView.ScaleType scaleType, final Callback<Bitmap> callback) {
        Log.i(LOGGING_TAG, String.format("call ImageRequest for " + imageUrl));
        ImageRequest imageRequest = new ImageRequest(imageUrl,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        Log.i(LOGGING_TAG, String.format("successful ImageRequest for " + imageUrl));
                        callback.onSuccess(bitmap);
                    }
                }, maxWidth, maxHeight, scaleType, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Log.i(LOGGING_TAG, String.format("unsuccessful ImageRequest for " + imageUrl), error);
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

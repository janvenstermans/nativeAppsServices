package com.tile.janv.redditviewer;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.tile.janv.redditviewer.gson.GsonRequest;
import com.tile.janv.redditviewer.gson.SubredditListResult;
import com.tile.janv.redditviewer.model.DaoSession;
import com.tile.janv.redditviewer.model.RedditPost;
import com.tile.janv.redditviewer.model.RedditPostDao;
import com.tile.janv.redditviewer.model.Subreddit;
import com.tile.janv.redditviewer.model.SubredditDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Service like class for getting Reddit responses.
 * Not yet an android class.
 *
 * Created by janv on 19-Dec-15.
 */
public class RedditService {

    private static final String LOGGING_TAG = "RedditService";

    private RequestQueue requestQueue;
    private final DaoSession daoSession;

    public RedditService(RequestQueue requestQueue, DaoSession daoSession) {
        this.requestQueue = requestQueue;
        this.daoSession = daoSession;
    }

    public void getSubredditList(String subreddit, String after, final Callback<SubredditListResult> callback) {
        if (after == null && daoSession != null) {
            List<RedditPost> redditListElements = getPostsOfSubredditFromDb(subreddit);
            if (redditListElements != null && redditListElements.size() > 0) {
                SubredditListResult result = new SubredditListResult();
                result.redditListElementList = toListElements(redditListElements);
                result.after = redditListElements.get(redditListElements.size() - 1).getRedditId();
                callback.onSuccess(result);
                return;
            }
        }
        getSubredditListFromRedditWebsite(subreddit, after, callback);
    }

    /**
     * Get a refreshed subreddit list.
     *
     * @param subreddit
     * @param callback
     */
    public void refreshSubredditList(String subreddit, final Callback<SubredditListResult> callback) {
        long subredditId = getSubredditFromDb(subreddit).getId();
        Log.i(LOGGING_TAG, String.format("About to delete subreddit posts of %s. Current count %d.",
                subreddit, getSubredditPostCountFromDb(subredditId)));
        deleteSubredditPostFromDb(subredditId);
        Log.i(LOGGING_TAG, String.format("After delete subreddit posts of %s: post count is %d.",
                subreddit, getSubredditPostCountFromDb(subredditId)));
        getSubredditListFromRedditWebsite(subreddit, null, callback);
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

    // helper methods

    private List<RedditPost> getPostsOfSubredditFromDb(String subreddit) {
        Subreddit subredditEntity = getSubredditFromDb(subreddit);
        if (subredditEntity != null) {
            return subredditEntity.getPosts();
        } else {
            // add subreddit
            Subreddit newSubreddit = new Subreddit();
            newSubreddit.setName(subreddit);
            daoSession.getSubredditDao().insertOrReplace(newSubreddit);
            return null;
        }
    }

    private long getSubredditPostCountFromDb(long subredditId) {
        return daoSession.getRedditPostDao().queryBuilder()
                .where(RedditPostDao.Properties.SubredditId.eq(subredditId)).count();
    }

    private void deleteSubredditPostFromDb(long subredditId) {
        daoSession.getRedditPostDao().queryBuilder()
                .where(RedditPostDao.Properties.SubredditId.eq(subredditId)).
                        buildDelete().executeDeleteWithoutDetachingEntities();
    }

    private Subreddit getSubredditFromDb(String subreddit) {
        return daoSession.getSubredditDao().queryBuilder()
                .where(SubredditDao.Properties.Name.eq(subreddit))
                .unique();
    }

    private void addPostsToDb(String subreddit, List<RedditListElement> redditListElements) {
        Subreddit subredditEntity = getSubredditFromDb(subreddit);
        for (RedditListElement element : redditListElements) {
            daoSession.getRedditPostDao().insertOrReplace(toDbElement(element, subredditEntity));
        }
    }

    private void getSubredditListFromRedditWebsite(final String subreddit, String after, final Callback<SubredditListResult> callback) {
        StringBuilder sb = new StringBuilder(String.format("https://www.reddit.com/r/%s/new.json?limit=%d", subreddit, Constants.getPostsLimit()));
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
                        addPostsToDb(subreddit, response.redditListElementList);
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

    //TODO: move to a UTIL class

    private List<RedditListElement> toListElements(List<RedditPost> redditListElements) {
        if (redditListElements == null) {
            return null;
        }
        List<RedditListElement> result = new ArrayList<>();
        for(RedditPost post : redditListElements) {
            result.add(toListElement(post));
        }
        return result;
    }

    private RedditListElement toListElement(RedditPost element) {
        if (element == null) {
            return null;
        }
        RedditListElement result = new RedditListElement();
        result.id = element.getRedditId();
        result.title = element.getTitle();
        result.author = element.getAuthor();
        result.link = element.getLink();
        result.permalink = element.getPermalink();
        result.thumbnail = element.getThumbnail();
        result.subreddit = element.getSubreddit().getName();
        return result;
    }

    private RedditPost toDbElement(RedditListElement element, Subreddit subreddit) {
        if (element == null) {
            return null;
        }
        RedditPost result = new RedditPost();
        result.setRedditId(element.id);
        result.setTitle(element.title);
        result.setAuthor(element.author);
        result.setLink(element.link);
        result.setPermalink(element.permalink);
        result.setThumbnail(element.thumbnail);
        result.setSubreddit(subreddit);
        return result;
    }
}

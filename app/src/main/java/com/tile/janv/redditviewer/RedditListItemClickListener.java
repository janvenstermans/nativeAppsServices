package com.tile.janv.redditviewer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Item touch listener for Reddit list.
 *
 * Created by janv on 13-Nov-15.
 */
public class RedditListItemClickListener implements RecyclerView.OnItemTouchListener {

    private RedditElementOnClickCallback callback;

    public interface RedditElementOnClickCallback {
        void onClick(String subreddit, String postId);
    }
    GestureDetector mGestureDetector;

    public RedditListItemClickListener(Context context, RedditElementOnClickCallback callback) {
        this.callback = callback;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }


    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View childView = rv.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && callback != null && mGestureDetector.onTouchEvent(e)) {
            RedditListAdapter.ViewHolder holder = (RedditListAdapter.ViewHolder) rv.getChildViewHolder(childView);
            callback.onClick(holder.subreddit, holder.postId);
            return true;
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}

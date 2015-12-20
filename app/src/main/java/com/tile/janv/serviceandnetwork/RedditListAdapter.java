package com.tile.janv.serviceandnetwork;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Reddit implementation of {@link RecyclerView.Adapter}.
 *
 * @see http://developer.android.com/training/material/lists-cards.html
 * @see http://android-pratap.blogspot.be/2015/06/endless-recyclerview-with-progress-bar.html
 *
 * Created by janv on 19-Dec-15.
 */
public class RedditListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // view states to differ between normal list item or progression item
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private List<RedditListElement> listElements;

    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private RedditService.OnLoadMoreListener onLoadMoreListener;

    private RedditService redditService;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView title;
        public TextView author;
        public CircleImageView circleImageView;
        public String subreddit;
        public String postId;
        public ViewHolder(final View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.reddit_listItem_title);
            author = (TextView) v.findViewById(R.id.reddit_listItem_author);
            circleImageView = (CircleImageView) v.findViewById(R.id.circle_image);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
        }
    }

    public RedditListAdapter(List<RedditListElement> listElements) {
        this.listElements = listElements;
    }

    public void onScrolled(int totalItemCountNew, int lastVisibleItemNew) {
        totalItemCount = totalItemCountNew;
        lastVisibleItem = lastVisibleItemNew;
        if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
            // End has been reached
            if (onLoadMoreListener != null) {
                addProgressionBar();
                onLoadMoreListener.onLoadMore();
            }
            loading = true;
        }
    }

    public void setRedditService(RedditService redditService) {
        this.redditService = redditService;
    }

    public void addListElements(List<RedditListElement> listElements) {
        removeProgressionBar();
        this.listElements.addAll(listElements);
        notifyDataSetChanged();
        loading = false;
    }

    public void setOnLoadMoreListener(RedditService.OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public List<RedditListElement> getListElements() {
        return listElements;
    }

    @Override
    public int getItemViewType(int position) {
        return this.listElements.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (viewType == VIEW_ITEM) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_subreddit, parent, false);
            // set the view's size, margins, paddings and layout parameters

            viewHolder = new ViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.progressionbar, parent, false);

            viewHolder = new ProgressViewHolder(v);
        }

        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            final ViewHolder viewHolder = (ViewHolder) holder;
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            viewHolder.title.setText(listElements.get(position).title);
            viewHolder.author.setText(listElements.get(position).author);
            viewHolder.subreddit = listElements.get(position).subreddit;
            viewHolder.postId = listElements.get(position).id;
            if (redditService != null) {
                redditService.getImage(listElements.get(position).thumbnail, 100, 100, null,
                    new RedditService.Callback<Bitmap>() {
                        @Override
                        public void onSuccess(Bitmap result) {
                            viewHolder.circleImageView.setImageBitmap(result);
                        }

                        @Override
                        public void onError(VolleyError error) {
                            // do nothing
                        }
                    });
            }
        } else if (holder instanceof ProgressViewHolder) {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return listElements.size();
    }

    private void addProgressionBar() {
        if (getListElements() != null) {
            //add null , so the adapter will check view_type and show progress bar at bottom
            getListElements().add(null);
            notifyItemInserted(getListElements().size() - 1);
        }
    }

    private void removeProgressionBar() {
        // check if last item is null
        if (getListElements() != null && getListElements().size() > 0 && getLastItem() == null) {
            //   remove progress item
            getListElements().remove(getListElements().size() - 1);
            notifyItemRemoved(getListElements().size());
        }
    }

    private RedditListElement getLastItem() {
        return getListElements().get(getListElements().size() - 1);
    }
}

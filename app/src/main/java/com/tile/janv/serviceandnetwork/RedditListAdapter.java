package com.tile.janv.serviceandnetwork;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Reddit implementation of {@link RecyclerView.Adapter}.
 *
 * @see http://developer.android.com/training/material/lists-cards.html
 *
 * Created by janv on 19-Dec-15.
 */
public class RedditListAdapter extends RecyclerView.Adapter<RedditListAdapter.ViewHolder> {

    private List<RedditListElement> listElements;

    private RedditService redditService;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView title;
        public TextView author;
        public CircleImageView circleImageView;
        public ViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.reddit_listItem_title);
            author = (TextView) v.findViewById(R.id.reddit_listItem_author);
            circleImageView = (CircleImageView) v.findViewById(R.id.circle_image);
        }
    }

    public RedditListAdapter(List<RedditListElement> listElements) {
        this.listElements = listElements;
    }

    public void setRedditService(RedditService redditService) {
        this.redditService = redditService;
    }

    public void addListElements(List<RedditListElement> listElements) {
        this.listElements.addAll(listElements);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_subreddit, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.title.setText(listElements.get(position).title);
        holder.author.setText(listElements.get(position).author);
        if (redditService != null) {
            redditService.getImage(listElements.get(position).thumbnail, 100, 100, null,
                    new RedditService.Callback<Bitmap>() {
                @Override
                public void onSuccess(Bitmap result) {
                    holder.circleImageView.setImageBitmap(result);
                }

                @Override
                public void onError(VolleyError error) {
                    // do nothing
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listElements.size();
    }
}

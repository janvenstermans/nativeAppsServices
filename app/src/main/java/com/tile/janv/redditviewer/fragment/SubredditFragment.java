package com.tile.janv.redditviewer.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.tile.janv.redditviewer.Constants;
import com.tile.janv.redditviewer.R;
import com.tile.janv.redditviewer.RedditListAdapter;
import com.tile.janv.redditviewer.RedditListElement;
import com.tile.janv.redditviewer.RedditListItemClickListener;
import com.tile.janv.redditviewer.RedditService;
import com.tile.janv.redditviewer.RedditViewerApplication;
import com.tile.janv.redditviewer.activity.SubredditListActivity;
import com.tile.janv.redditviewer.activity.RedditPostDetailActivity;
import com.tile.janv.redditviewer.gson.SubredditListResult;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class SubredditFragment extends Fragment {

    @Bind(R.id.list_is_empty_label)
    TextView listEmptyText;

    @Bind(R.id.subreddit_recycler_view)
    RecyclerView recyclerView;
    private RedditListAdapter recyclerViewAdapter;
    private LinearLayoutManager recyclerViewLayoutManager;
    private String postsAfter;

    private RedditService redditService;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String ARG_SECTION_NUMBER = "section_number";

    private static final String LOGGING_TAG = "SubredditFragment";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SubredditFragment newInstance(int sectionNumber) {
        SubredditFragment fragment = new SubredditFragment();
        Bundle args = new Bundle();
        args.putInt(SubredditFragment.ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public SubredditFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_subreddit, container, false);
        ButterKnife.bind(this, rootView);

        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        recyclerViewLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        // specify an adapter (see also next example)
        recyclerViewAdapter = new RedditListAdapter(new ArrayList<RedditListElement>());
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                recyclerViewAdapter.onScrolled(recyclerViewLayoutManager.getItemCount(),
                        recyclerViewLayoutManager.findLastVisibleItemPosition());
            }
        });
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                updateFragmentItemsVisibility();
            }
        });
        updateFragmentItemsVisibility();
        recyclerViewAdapter.setOnLoadMoreListener(new RedditService.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getJsonFromReddit();
            }
        });

        recyclerView.addOnItemTouchListener(new RedditListItemClickListener(getContext(), new RedditListItemClickListener.RedditElementOnClickCallback() {
            @Override
            public void onClick(String subreddit, String postId) {
                Log.i(LOGGING_TAG, String.format("list item from subreddit %s with id %s " +
                        "clicked/touched.", subreddit, postId));
                Intent showDetails = new Intent(getContext(), RedditPostDetailActivity.class);
                showDetails.putExtra(RedditPostDetailActivity.SUBREDDIT, subreddit)
                        .putExtra(RedditPostDetailActivity.POST_ID, postId);
                getActivity().startActivity(showDetails);
            }
        }));

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        redditService = new RedditService(Volley.newRequestQueue(getActivity()), ((RedditViewerApplication) getActivity().getApplication()).daoSession);
        recyclerViewAdapter.setRedditService(redditService);
        ((SubredditListActivity) getActivity()).onSectionAttached(getArgSectionNumber());
    }

    @Override
    public void onStart() {
        super.onStart();
        getJsonFromReddit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            redditService.refreshSubredditList(getSubreddit(), new RedditService.Callback<SubredditListResult>() {
                @Override
                public void onSuccess(SubredditListResult result) {
                    Log.i(LOGGING_TAG, String.format("got %d posts from reddit service.", result.redditListElementList.size()));
                    recyclerViewAdapter.setListElements(result.redditListElementList);
                    postsAfter = result.after;
                }

                @Override
                public void onError(VolleyError error) {
                    Log.i(LOGGING_TAG, "service call getSubredditList gave error.", error);
                }
            });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getJsonFromReddit() {
        redditService.getSubredditList(getSubreddit(), postsAfter, new RedditService.Callback<SubredditListResult>() {
            @Override
            public void onSuccess(SubredditListResult result) {
                Log.i(LOGGING_TAG, String.format("got %d posts from reddit service.", result.redditListElementList.size()));
                recyclerViewAdapter.addListElements(result.redditListElementList);
                postsAfter = result.after;
            }

            @Override
            public void onError(VolleyError error) {
                Log.i(LOGGING_TAG, "service call getSubredditList gave error.", error);
            }
        });
    }

    private void updateFragmentItemsVisibility() {
        if (recyclerViewAdapter.getItemCount() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            listEmptyText.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            listEmptyText.setVisibility(View.VISIBLE);
        }
    }

    private String getSubreddit() {
        return Constants.getSubreddit(getArgSectionNumber());
    }

    private int getArgSectionNumber() {
        return getArguments().getInt(ARG_SECTION_NUMBER);
    }
}

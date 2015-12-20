package com.tile.janv.serviceandnetwork;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.tile.janv.serviceandnetwork.gson.SubredditListResult;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class SubredditFragment extends Fragment {

    private TextView listEmptyText;

    private RecyclerView recyclerView;
    private RedditListAdapter recyclerViewAdapter;
    private LinearLayoutManager recyclerViewLayoutManager;
    private String postsAfter;

    private RedditService redditService;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String ARG_SECTION_NUMBER = "section_number";

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
        recyclerView = (RecyclerView) rootView.findViewById(R.id.subreddit_recycler_view);
        listEmptyText = (TextView) rootView.findViewById(R.id.list_is_empty_label);

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

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        redditService = new RedditService(Volley.newRequestQueue(getActivity()));
        recyclerViewAdapter.setRedditService(redditService);
        ((MainActivity) getActivity()).onSectionAttached(getArgSectionNumner());
    }

    @Override
    public void onStart() {
        super.onStart();
        getJsonFromReddit();
    }

    private int getArgSectionNumner() {
        return getArguments().getInt(ARG_SECTION_NUMBER);
    }

    private void getJsonFromReddit() {
        String subreddit = Constants.getSubreddit(getArgSectionNumner());
        redditService.getSubredditList(subreddit, postsAfter, new RedditService.Callback<SubredditListResult>() {
            @Override
            public void onSuccess(SubredditListResult result) {
                Toast.makeText(getContext(), "success on json call, recieved " +
                        result.redditListElementList.size() + " posts", Toast.LENGTH_SHORT).show();
                recyclerViewAdapter.addListElements(result.redditListElementList);
                postsAfter = result.after;
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(getContext(), "error on json call", Toast.LENGTH_SHORT).show();
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
}

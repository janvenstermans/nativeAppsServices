package com.tile.janv.serviceandnetwork;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class SubredditFragment extends Fragment {

    private TextView contentLabel;
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
        contentLabel = (TextView) rootView.findViewById(R.id.subreddit_label);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        redditService = new RedditService(Volley.newRequestQueue(getActivity()));
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
        redditService.getSubredditList(subreddit, new RedditService.Callback<List<RedditListElement>>() {
            @Override
            public void onSuccess(List<RedditListElement> result) {
                contentLabel.setText("success with json, " + result.size() + " posts");
            }

            @Override
            public void onError(VolleyError error) {
                contentLabel.setText("error with json");
            }
        });
    }
}

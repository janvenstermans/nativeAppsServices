package com.tile.janv.redditviewer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.Menu;
import android.widget.EditText;
import android.widget.TextView;

import com.tile.janv.redditviewer.Constants;
import com.tile.janv.redditviewer.InputFilterMin;
import com.tile.janv.redditviewer.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends AppCompatActivity {

    @Bind(R.id.settings_post_limit)
    EditText postLimit;

    @Bind(R.id.settings_subreddits)
//    EditText subreddits;
    TextView subreddits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        postLimit.setFilters(new InputFilter[]{new InputFilterMin(10)});
        postLimit.setText(Constants.getPostsLimit() + "");
        subreddits.setText(arrayToCommaSeparatedString(Constants.getSubreddits()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @OnClick(R.id.settings_update_button)
    public void onUpdateClick() {
        // post limit
        try {
            int newValue = Integer.parseInt(postLimit.getText().toString());
            Constants.setPostsLimit(newValue);
        } catch (NumberFormatException nfe) { }
        // subreddit
//        String subredditsString = subreddits.getText().toString();
        // trim elements when turning into array
//        String[] newSubreddits = subredditsString.trim().split("\\s*,\\s*");
//        Constants.setSubreddits(newSubreddits);
    }

    private String arrayToCommaSeparatedString(String[] input) {
        StringBuilder sb = new StringBuilder();
        if (input != null && input.length > 0) {
            for (String el : input) {
                sb.append(el);
                sb.append(",");
            }
            // remove last character
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }
}

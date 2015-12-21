package com.tile.janv.redditviewer.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.tile.janv.redditviewer.R;
import com.tile.janv.redditviewer.RedditPostDetails;
import com.tile.janv.redditviewer.RedditService;
import com.tile.janv.redditviewer.RedditViewerApplication;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RedditPostDetailActivity extends AppCompatActivity {

    public final static String SUBREDDIT = "com.tile.janv.serviceandnetwork.redditdetailactivity.SUBREDDIT";
    public final static String POST_ID = "com.tile.janv.serviceandnetwork.redditdetailactivity.POST_ID";

    @Bind(R.id.detail_title)
    TextView title;
    @Bind(R.id.detail_author)
    TextView author;
    @Bind(R.id.detail_comments)
    TextView comments;
    //image section
    @Bind(R.id.detail_imageView)
    ImageView image;
    @Bind(R.id.detail_progressionBar)
    ProgressBar progressBar;
    @Bind(R.id.detail_imageLoadFailed)
    TextView imageFailed;

    private RedditService redditService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reddit_detail);
        ButterKnife.bind(this);

        redditService = new RedditService(Volley.newRequestQueue(this), ((RedditViewerApplication) getApplication()).daoSession);
        redditService.getSubredditPost(getIntent().getStringExtra(SUBREDDIT), getIntent().getStringExtra(POST_ID), new RedditService.Callback<RedditPostDetails>() {
            @Override
            public void onSuccess(RedditPostDetails result) {
                title.setText(result.title);
                author.setText("Author: " + result.author);
                comments.setText("Comments count: " + result.commentsCount);
                loadImage(result);
            }

            @Override
            public void onError(VolleyError error) {
                title.setText("Can't find details");
            }
        });
    }

    // menu section

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reddit_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private int getScreenWidth() {
        Display display = getWindowManager().getDefaultDisplay();
        return display.getWidth();
    }

    private void loadImage(RedditPostDetails result) {
        String imageUrl = result.previewImageUrl != null ? result.previewImageUrl : result.thumbnail;
        image.setVisibility(View.GONE);
        imageFailed.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        redditService.getImage(imageUrl, getScreenWidth(), 0, null,
                new RedditService.Callback<Bitmap>() {
                    @Override
                    public void onSuccess(Bitmap result) {
                        image.setImageBitmap(result);
                        progressBar.setVisibility(View.GONE);
                        image.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(VolleyError error) {
                        // do nothing
                        progressBar.setVisibility(View.GONE);
                        imageFailed.setVisibility(View.VISIBLE);
                    }
                });
    }
}

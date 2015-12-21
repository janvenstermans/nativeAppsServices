package com.tile.janv.redditviewer.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.tile.janv.redditviewer.Constants;
import com.tile.janv.redditviewer.R;
import com.tile.janv.redditviewer.RedditService;
import com.tile.janv.redditviewer.fragment.NavigationDrawerFragment;
import com.tile.janv.redditviewer.fragment.SubredditFragment;

import java.util.ArrayList;
import java.util.List;

public class SubredditListActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private RedditService redditService;
    /** Flag indicating whether we have called bind on the service. */
    boolean mBound;
    /** Class for interacting with the main interface of the service.*/
    private ServiceConnection mConnection;
    /** List of callbacks to be called when Service becomes available.*/
    private List<GetRedditServiceCallback> callbackListAfterServiceConnection = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, SubredditFragment.newInstance(position))
                .commit();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // define what to do when service connection has been established
        mConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder service) {
                // This is called when the connection with the service has been
                // established, giving us the object we can use to
                // interact with the service.
                redditService = ((RedditService.RedditServiceBinder) service).getService();
                for (GetRedditServiceCallback callback : callbackListAfterServiceConnection) {
                    callback.onRedditServiceAvailable(redditService);
                }
                callbackListAfterServiceConnection.clear();
                mBound = true;
            }

            public void onServiceDisconnected(ComponentName className) {
                // This is called when the connection with the service has been
                // unexpectedly disconnected -- that is, its process crashed.
                redditService = null;
                mBound = false;
            }
        };
        // Bind to the service
        bindService(new Intent(this, RedditService.class), mConnection,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    /**
     *
     * @param number
     */
    public void onSectionAttached(int number) {
        mTitle = Constants.getSubreddits()[number];
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.menu_main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Returns {@link RedditService} when available.
     */
    public void getRedditService(GetRedditServiceCallback callback) {
        if (mBound) {
            callback.onRedditServiceAvailable(redditService);
        } else {
            callbackListAfterServiceConnection.add(callback);
        }
    }

    public interface GetRedditServiceCallback {
        void onRedditServiceAvailable(RedditService redditService);
    }
}

package com.tile.janv.redditviewer;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.tile.janv.redditviewer.model.DaoMaster;
import com.tile.janv.redditviewer.model.DaoSession;

/**
 * Created by janv on 20-Dec-15.
 */
public class RedditViewerApplication extends Application {

    public DaoSession daoSession;
    public RequestQueue requestQueue;

    @Override
    public void onCreate() {
        super.onCreate();

        // create objects for application use
        requestQueue = Volley.newRequestQueue(this);
        //create db and make a session to it.
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "reddit-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }
}

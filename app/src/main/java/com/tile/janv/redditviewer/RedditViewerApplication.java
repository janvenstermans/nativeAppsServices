package com.tile.janv.redditviewer;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.tile.janv.redditviewer.model.DaoMaster;
import com.tile.janv.redditviewer.model.DaoSession;

/**
 * Created by janv on 20-Dec-15.
 */
public class RedditViewerApplication extends Application {

    public DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        //create db and make a session to it.
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "lease-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }
}

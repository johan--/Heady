package com.heady;

import android.app.Application;

import com.heady.util.logger.Log;
import com.network.RetrofitApiClient;

import io.realm.Realm;


/**
 * Created by Yogi.
 */

public class HeadyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RetrofitApiClient.initRetrofitApiClient(this);
        //realm database
        Realm.init(this);

        // initialize logs
        Log.init(BuildConfig.DEBUG);
    }
}

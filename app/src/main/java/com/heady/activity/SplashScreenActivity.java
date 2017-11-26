package com.heady.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.heady.R;

/**
 * Created by Yogi.
 */

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        final Handler handler = new Handler();
        final Runnable openNextActivityThread = new Runnable() {
            @Override
            public void run() {
                CategoriesActivity.start(SplashScreenActivity.this, true);
            }
        };
        handler.postDelayed(openNextActivityThread, 350);
    }
}

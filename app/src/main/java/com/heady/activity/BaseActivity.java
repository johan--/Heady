package com.heady.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Yogi.
 */

public abstract class BaseActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        getIntentData(getIntent());
        setToolBar();
        initApi();
        setupRealm();
        setupView();

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        callServer();
    }

    protected void getIntentData(Intent intent) {

    }


    protected abstract void setupRealm();

    @LayoutRes
    protected abstract int getLayoutId();

    protected abstract void setToolBar();

    protected abstract void initApi();

    protected abstract void setupView();

    protected abstract void callServer();
}

package com.heady.activity;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;

/**
 * Created by Yogi.
 */

public abstract class BaseActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        setToolBar();
        initApi();
        setupRealm();
        setupView();
    }


    protected abstract void setupRealm();

    @LayoutRes
    protected abstract int getLayoutId();

    protected abstract void setToolBar();

    protected abstract void initApi();

    protected abstract void setupView();

    protected abstract void callServer();
}

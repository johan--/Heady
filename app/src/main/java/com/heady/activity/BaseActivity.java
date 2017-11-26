package com.heady.activity;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * Created by Yogi.
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected Realm realm;

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


    @Override
    public void onDestroy() {
        if (realm != null && !realm.isClosed()) {
            if (realm.isInTransaction()) {
                realm.cancelTransaction();
            }
            realm.close();
        }
        super.onDestroy();
    }


    protected abstract void setupRealm();

    @LayoutRes
    protected abstract int getLayoutId();

    protected abstract void setToolBar();

    protected abstract void initApi();

    protected abstract void setupView();

    protected abstract void callServer();
}

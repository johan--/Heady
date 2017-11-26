package com.heady.activity;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewStub;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.heady.R;
import com.heady.util.MultiStateRecyclerView;
import com.heady.util.logger.Log;

import butterknife.BindView;

/**
 * Created by Yogi.
 */

public abstract class BaseListActivity<Adapter extends BaseQuickAdapter> extends BaseActivity implements MultiStateRecyclerView.LoadMoreListener,
        MultiStateRecyclerView.RefreshListener,
        MultiStateRecyclerView.RetryListener,
        BaseQuickAdapter.OnItemClickListener,
        BaseQuickAdapter.OnItemChildClickListener {
    @BindView(R.id.recycler_view)
    protected MultiStateRecyclerView mRecyclerView;
    @BindView(R.id.rootView)
    protected View mRootView;
    protected Adapter mAdapter;
    private boolean isServerCalled;

    @Override
    public int getLayoutId() {
        return R.layout.activity_base_list;
    }

    @Override
    protected void setToolBar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(getToolBarTitle());
            ab.setDisplayHomeAsUpEnabled(false);
        }
    }

    @Override
    protected void setupView() {
        setupAdapter();
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Log.d();
        fetchData();
    }


    private void setupAdapter() {
        if (mAdapter == null) {
            mAdapter = getAdapterInstance();
        }
        mRecyclerView.setLoadMoreListener(this);
        mRecyclerView.setRefreshListener(this);
        mRecyclerView.setRetryListener(this);
        mRecyclerView.setAdapter(mAdapter, isLoadMoreEnabled());
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemChildClickListener(this);
    }

    public abstract Adapter getAdapterInstance();

    public abstract String getToolBarTitle();

    protected boolean isLoadMoreEnabled() {
        return false;
    }

    @Override
    public void onLoadMore(int pageNumber) {
        callServer();
    }

    @Override
    public void onRefresh() {
        mRecyclerView.resetPageNumber();
        callServer();
    }

    @Override
    public void onRetry() {
        callServer();
    }

    private void fetchData() {
        if (mAdapter == null) {
            throw new RuntimeException("initialise the Adapter first");
        }
        if (mRecyclerView.getPageNumber() == 1 && !isServerCalled) {
            callServer();
            isServerCalled = true;
        }
    }

    protected void showSnackBar(String message) {
        Snackbar.make(mRootView, message, Snackbar.LENGTH_LONG).show();
    }
}

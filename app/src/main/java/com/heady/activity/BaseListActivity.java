package com.heady.activity;

import android.support.annotation.DrawableRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.heady.R;
import com.heady.util.ItemOffsetDecoration;
import com.heady.util.MultiStateView;

/**
 * Created by Yogi.
 */

public abstract class BaseListActivity extends BaseActivity {

    protected MultiStateView mMultiStateView;
    // error views
    protected Button mErrorRetryButton;
    protected CoordinatorLayout mCoordinatorLayout;
    // views inflated by mMultiStateView
    protected View mErrorView;
    protected View mEmptyView;
    protected View mLoadingView;
    protected RecyclerView mRecyclerView;
    private TextView mErrorTextView;
    // empty views
    private ImageView mEmptyImageView;
    private TextView mEmptyTextView;

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

    protected abstract String getToolBarTitle();


    @Override
    protected void setupView() {
        mCoordinatorLayout = findViewById(R.id.coordinatorLayout);

        mMultiStateView = findViewById(R.id.multiStateView);

        // init error and empty view
        mErrorView = mMultiStateView.getView(MultiStateView.VIEW_STATE_ERROR);
        mEmptyView = mMultiStateView.getView(MultiStateView.VIEW_STATE_EMPTY);
        mLoadingView = mMultiStateView.getView(MultiStateView.VIEW_STATE_LOADING);

        // init views from loading, error and empty view
        mErrorTextView = mErrorView.findViewById(R.id.textViewError);
        mErrorRetryButton = mErrorView.findViewById(R.id.buttonRetryError);
        mErrorRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callServer();
            }
        });

        mEmptyImageView = mEmptyView.findViewById(R.id.imageViewEmpty);
        mEmptyTextView = mEmptyView.findViewById(R.id.textViewEmpty);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new ItemOffsetDecoration(this, R.dimen.padding_small));
        setupAdapter();
    }

    protected abstract void setupAdapter();

    /**
     * connection failed
     */
    protected void setErrorMessage(String errorMessage) {
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
        mErrorTextView.setText(errorMessage);
    }

    /**
     * set empty view data
     */
    protected void setEmptyViewData(String message, @DrawableRes int drawable) {
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
        mEmptyTextView.setText(message);
        if (drawable != 0)
            mEmptyImageView.setImageResource(drawable);
    }

    /**
     * hide progress
     */
    protected void hideProgress() {
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
    }

    /**
     * show progress
     */
    protected void showProgress() {
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_LOADING);
    }

    protected void showSnackBar(String message) {
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }


    protected void setToolBarTitle(String title) {
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(title);
        }
    }
}

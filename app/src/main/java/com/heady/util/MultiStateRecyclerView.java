package com.heady.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.heady.R;

/**
 * Created by Yogi.
 */

public class MultiStateRecyclerView extends FrameLayout implements SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {

    // stuffs RecyclerView needs
    protected RecyclerView.LayoutManager mLayoutManager;
    protected RecyclerView.ItemAnimator mItemAnimator;
    protected RecyclerView.ItemDecoration mItemDecoration;
    // protected RecyclerView.OnScrollListener mScrollListener;
    protected BaseQuickAdapter mAdapter;
    // private static final int DELAY = 50;
    // changes the view state of the layout to loading, content, empty or error
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean mSwipeRefreshEnabled = true;
    // Listeners
    private RefreshListener mRefreshListener;
    private RetryListener mRetryListener;
    private LoadMoreListener mLoadMoreListener;
    // error image and text views for error layouts
    private AppCompatImageView mErrorImageView;
    private AppCompatTextView mErrorTextView;
    private AppCompatButton mErrorRetryButton;
    private int mPageNumber = 1;
    private View mErrorView;
    // private View mFooterView;
    private View mLoadingView;
    private LoadMoreView mLoadMoreView;

    public MultiStateRecyclerView(Context context) {
        super(context, null);
    }

    public MultiStateRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private static boolean checkNotNull(Object object) {
        return object != null;
    }

    private static boolean checkNotNull(int i) {
        return !(i <= 0);
    }

    /**
     * initialise views and set desired attributes
     */
    private void init(Context context, AttributeSet attrs) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.msv_recyclerview, this, true);
        mRecyclerView = findViewById(R.id.recyclerView);
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        setDefaultParams(context);
        setEnabled(mSwipeRefreshEnabled);
        setSwipeRefreshColorScheme(R.color.accent, R.color.primary);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        inflateViews(inflater);
    }

    private void inflateViews(LayoutInflater inflater) {
        mErrorView = inflater.inflate(R.layout.view_state_error, this, false);
        mLoadingView = inflater.inflate(R.layout.view_state_loading, this, false);

        mErrorImageView = mErrorView.findViewById(R.id.imageViewError);
        mErrorTextView = mErrorView.findViewById(R.id.textViewError);
        mErrorRetryButton = mErrorView.findViewById(R.id.buttonRetryError);
        mErrorRetryButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRetryListener == null) {
                    throw new NullPointerException("Initialise RetryListener first");
                }
                mRetryListener.onRetry();
            }
        });
        setView(PlaceHolderView.LOADING);
    }

    /**
     * @param listener which will implement onRefresh
     */
    public void setRefreshListener(RefreshListener listener) {
        mRefreshListener = listener;
    }

    public void setLoadMoreListener(LoadMoreListener listener) {
        mLoadMoreListener = listener;
    }

    public void setRetryListener(RetryListener listener) {
        mRetryListener = listener;
    }

    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        mItemDecoration = itemDecoration;
        mRecyclerView.addItemDecoration(mItemDecoration);
    }

    public void addDividerItemDecoration() {
        removeItemDecoration();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider));
        mItemDecoration = dividerItemDecoration;
        mRecyclerView.addItemDecoration(mItemDecoration);
    }

    public void removeItemDecoration() {
        if (mRecyclerView == null || mItemDecoration == null)
            return;
        mRecyclerView.removeItemDecoration(mItemDecoration);
        mRecyclerView.setPadding(0, 0, 0, 0);
    }

    public void setDefaultParams(Context context) {
        if (mRecyclerView == null) {
            throw new NullPointerException("RecyclerView reference not found");
        }
        mRecyclerView.setHasFixedSize(true);
//        mRecyclerView.setItemViewCacheSize(20);
//        mRecyclerView.setDrawingCacheEnabled(true);
//        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        setLayoutManager(layoutManager);

        ItemOffsetDecoration itemOffsetDecoration = new ItemOffsetDecoration(getContext(), R.dimen.padding_small);
        addItemDecoration(itemOffsetDecoration);
        setItemAnimator(new DefaultItemAnimator());
    }

    public void setAdapter(BaseQuickAdapter adapter, boolean enableLoadMore) {
        mAdapter = adapter;
        mRecyclerView.setAdapter(mAdapter);
        if (enableLoadMore) {
            setEndlessScroll();
        }
    }

    /*====================================================
     ================= SwipeToRefreshLayout ==============
     ====================================================*/
    @Override
    public void onRefresh() {
        if (checkNotNull(mRefreshListener)) {
            mRefreshListener.onRefresh();
        } else {
            throw new NullPointerException("Please initialise StateListener in ParamBuilder");
        }
    }

    public void stopRefreshing() {
        if (mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(false);
    }

    public void startRefreshing() {
        if (!mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(true);
    }

    public void setSwipeRefreshColorScheme(@ColorRes int... colorScheme) {
        mSwipeRefreshLayout.setColorSchemeResources(colorScheme);
    }

    private void setEndlessScroll() {
        mLoadMoreView = new CustomLoadMoreView();
        mAdapter.setLoadMoreView(mLoadMoreView);
        mAdapter.setOnLoadMoreListener(this, mRecyclerView);
    }

    public void setLoadMoreComplete() {
        mAdapter.loadMoreComplete();
    }

    public void setLoadMoreFailed() {
        mAdapter.loadMoreFail();
    }

    public void setLoadMoreEnd() {
        mAdapter.loadMoreEnd();
    }

    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return mSwipeRefreshLayout;
    }

    public void setSwipeRefreshEnabled(boolean refreshEnabled) {
        mSwipeRefreshEnabled = refreshEnabled;
        this.getSwipeRefreshLayout().setEnabled(mSwipeRefreshEnabled);
    }

    /**
     * shows the loading view layout hiding the content layout.
     */
    public void setLoadingView() {
        setView(PlaceHolderView.LOADING);
        stopRefreshing();
    }

    /**
     * shows the content view layout hiding loading, error and empty view
     */
    public void setContentView() {
        if (getPageNumber() == 1) {
            setLoadMoreComplete();
        }
        stopRefreshing();
    }

    public void setEmptyView() {
        mAdapter.setEmptyView(mErrorView);
        mErrorRetryButton.setVisibility(INVISIBLE);
        stopRefreshing();
    }

    public void setView(@PlaceHolderView int errorView) {
        setView(errorView, "");
    }

    public void setView(@PlaceHolderView int errorView, String errorMessage) {
        stopRefreshing();
        if (mAdapter == null) {
            return;
        }
        if (getPageNumber() == 1) {
            setFullScreenErrorView(errorView, errorMessage);
            return;
        }

        setErrorInFooter(errorView, errorMessage);
    }

    private void setErrorInFooter(@PlaceHolderView int errorView, String errorMessage) {

        boolean isProgressVisible = false;
        boolean isTextViewVisible = false;

        switch (errorView) {
            case PlaceHolderView.ERROR:
                isProgressVisible = false;
                isTextViewVisible = true;
                if (!StringUtil.isNotEmpty(errorMessage)) {
                    errorMessage = getResources().getString(R.string.something_went_wrong);
                }
                setLoadMoreFailed();
                break;
            case PlaceHolderView.INTERNET:
                isProgressVisible = false;
                isTextViewVisible = true;
                if (!StringUtil.isNotEmpty(errorMessage)) {
                    errorMessage = getResources().getString(R.string.no_internet_connection);
                }
                setLoadMoreFailed();
                break;
            case PlaceHolderView.LOADING:
                isProgressVisible = true;
                isTextViewVisible = false;
                errorMessage = "";
                break;
            case PlaceHolderView.NO_DATA:
                isProgressVisible = false;
                isTextViewVisible = true;
                if (StringUtil.isEmpty(errorMessage)) {
                    errorMessage = getResources().getString(R.string.no_more_data);
                }
                // setLoadMoreComplete();
                setLoadMoreEnd();
                break;
            case PlaceHolderView.CONTENT:
                setLoadMoreComplete();
                break;
        }
        /*mFooterProgressBar.setVisibility(isProgressVisible ? VISIBLE : GONE);
        mFooterTextView.setVisibility(isTextViewVisible ? VISIBLE : GONE);
        mFooterTextView.setText(errorMessage);*/
    }

    private void setFullScreenErrorView(@PlaceHolderView int errorView, String errorMessage) {
        Drawable drawable = null;
        String message = null;
        switch (errorView) {
            case PlaceHolderView.ERROR:
                mAdapter.setEmptyView(mErrorView);
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_error_network);
                message = errorMessage;
                mErrorRetryButton.setVisibility(VISIBLE);
                break;
            case PlaceHolderView.INTERNET:
                mAdapter.setEmptyView(mErrorView);
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_error_network);
                message = getResources().getString(R.string.no_internet_connection);
                mErrorRetryButton.setVisibility(VISIBLE);
                break;
            case PlaceHolderView.LOADING:
                mAdapter.setEmptyView(mLoadingView);
                break;
            case PlaceHolderView.NO_DATA:
                mAdapter.setEmptyView(mErrorView);
                mErrorRetryButton.setVisibility(GONE);
                message = getResources().getString(R.string.no_data_found);
                // drawable = ContextCompat.getDrawable(getContext(), R.drawable.)
                mErrorRetryButton.setVisibility(GONE);
                break;
            case PlaceHolderView.CONTENT:
                break;
        }

        if (drawable != null) {
            mErrorImageView.setImageDrawable(drawable);
        }

        if (StringUtil.isNotEmpty(message)) {
            mErrorTextView.setText(message);
        }
    }

    @Override
    public void onLoadMoreRequested() {
        if (mLoadMoreListener != null) {
            mPageNumber += 1;
            mLoadMoreListener.onLoadMore(mPageNumber);
        }
        /*mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
            }
        }, DELAY);*/

    }

    public RecyclerView.LayoutManager getLayoutManager() {
        return mLayoutManager;
    }

    /* ================= RecyclerView Methods ==============================*/
    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        mLayoutManager = layoutManager;
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    public RecyclerView.ItemAnimator getItemAnimator() {
        return mItemAnimator;
    }

    public void setItemAnimator(RecyclerView.ItemAnimator itemAnimator) {
        mItemAnimator = itemAnimator;
        mRecyclerView.setItemAnimator(mItemAnimator);
    }

    public RecyclerView.ItemDecoration getItemDecoration() {
        return mItemDecoration;
    }

    public RecyclerView.Adapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(BaseQuickAdapter adapter) {
        setAdapter(adapter, true);
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public int getPageNumber() {
        return mPageNumber;
    }

    public void setPageNumber(int pageNumber) {
        mPageNumber = pageNumber;
    }

    public void resetPageNumber() {
        setPageNumber(1);
    }

    public interface RefreshListener {
        void onRefresh();
    }

    public interface RetryListener {
        void onRetry();
    }

    public interface LoadMoreListener {
        void onLoadMore(int pageNumber);
    }

}

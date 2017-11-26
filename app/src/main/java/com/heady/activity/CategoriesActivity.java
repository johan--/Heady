package com.heady.activity;

import android.app.Activity;
import android.content.Intent;

import com.heady.R;
import com.heady.adapter.CategoryAdapter;
import com.heady.adapter.model.Category;
import com.heady.util.NetworkUtil;
import com.heady.util.realm.CategoryManager;
import com.heady.util.realm.DbType;
import com.heady.util.realm.RankingManager;
import com.network.api.HeadyApi;
import com.network.interfaces.CommonResponseListener;
import com.network.model.HeadyModel;
import com.network.utils.RetrofitConstants;
import com.network.utils.error.RetrofitException;

import io.realm.RealmResults;

/**
 * Created by Yogi.
 */

public class CategoriesActivity extends BaseListActivity implements CommonResponseListener<HeadyModel> {
    private CategoryAdapter mCategoryAdapter;
    private HeadyApi mHeadyApi;
    private RankingManager rankingManager;
    private CategoryManager categoryManager;
    private RealmResults<Category> mCategoryRealmResults;

    public static void start(Activity activity, boolean finishPreviousActivity) {
        if (!(activity instanceof CategoriesActivity)) {
            Intent intent = new Intent(activity, CategoriesActivity.class);
            activity.startActivity(intent);
            if (finishPreviousActivity) {
                activity.finish();
            }
        }
    }

    @Override
    protected void setupRealm() {
        rankingManager = new RankingManager(DbType.Heady.RANKING);
        categoryManager = new CategoryManager(DbType.Heady.CATEGORIES);
        mCategoryRealmResults = categoryManager.getExpandableList();
    }

    @Override
    protected String getToolBarTitle() {
        return "Categories";
    }

    @Override
    protected void initApi() {
        mHeadyApi = new HeadyApi(this);
    }

    @Override
    protected void setupAdapter() {
        mCategoryAdapter = new CategoryAdapter(mCategoryRealmResults);
        mRecyclerView.setAdapter(mCategoryAdapter);
    }

    @Override
    protected void callServer() {
        if (NetworkUtil.isConnectedToInternet(this)) {
            showProgress();
            mHeadyApi.getProducts(RetrofitConstants.RequestMode.GET_PRODUCTS);
        } else {
            setErrorMessage(getString(R.string.no_internet_connection));
        }
    }

    @Override
    public void onSuccess(int pageNumber, HeadyModel headyModel, int requestMode) {
        hideProgress();
        rankingManager.setData(headyModel.rankings);
        categoryManager.setData(headyModel.categories);
        if (mCategoryAdapter != null) {
            mCategoryAdapter.notifyParentDataSetChanged();
        }
    }

    @Override
    public void onNoContent(int pageNumber, int requestMode) {
        setEmptyViewData("No categories found", R.drawable.ic_placeholder);
    }

    @Override
    public void onFailure(int pageNumber, String message, RetrofitException exception, int requestMode) {
        setErrorMessage(message);
    }
}

package com.heady.activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.heady.R;
import com.heady.adapter.ProductsAdapter;
import com.heady.util.NetworkUtil;
import com.heady.util.PlaceHolderView;
import com.heady.util.logger.Log;
import com.heady.util.realm.DbType;
import com.heady.util.realm.RealmManager;
import com.network.api.HeadyApi;
import com.network.interfaces.CommonResponseListener;
import com.network.model.HeadyModel;
import com.network.model.Products;
import com.network.model.Rankings;
import com.network.utils.RetrofitConstants;
import com.network.utils.error.RetrofitException;

import java.util.List;

import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Yogi.
 */

public class MainActivity extends BaseListActivity<ProductsAdapter> implements CommonResponseListener<HeadyModel> {
    private HeadyApi mHeadyApi;
    private RealmResults<Products> mProductList;

    @Override
    protected void initApi() {
        mHeadyApi = new HeadyApi(this);
    }

    @Override
    protected void setupRealm() {
        realm = RealmManager.instance(DbType.Heady.PRODUCTS);
        mProductList = realm.where(Products.class).findAll();
    }

    @Override
    protected void callServer() {
        if (NetworkUtil.isConnectedToInternet(this)) {
            mRecyclerView.setView(PlaceHolderView.LOADING);
            mHeadyApi.getProducts(RetrofitConstants.RequestMode.GET_PRODUCTS);
        } else {
            mRecyclerView.setView(PlaceHolderView.INTERNET);
        }
    }

    @Override
    protected int getTopLayoutId() {
        return R.layout.layout_filter;
    }

    @Override
    public ProductsAdapter getAdapterInstance() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        return new ProductsAdapter(mProductList);
    }

    @Override
    public String getToolBarTitle() {
        return "Heady";
    }

    @OnClick({R.id.filterButton, R.id.sortButton})
    protected void setOnclickEvent(View view) {
        switch (view.getId()) {
            case R.id.filterButton:
                break;
            case R.id.sortButton:
                showSortDialog();
                break;
        }
    }

    private void showSortDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sort By");
        List<Rankings> titles = realm.where(Rankings.class).findAll();
        for (int i = 0; i < titles.size(); i++) {
            Log.e(titles.get(i).ranking);
        }
//        builder.setSingleChoiceItems()
    }


    @Override
    public void onSuccess(int pageNumber, final HeadyModel headyModel, int requestMode) {
        mRecyclerView.setView(PlaceHolderView.CONTENT);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.copyToRealmOrUpdate(headyModel.categories);
            }
        });
    }

    @Override
    public void onNoContent(int pageNumber, int requestMode) {
        mRecyclerView.setView(PlaceHolderView.NO_DATA);
    }

    @Override
    public void onFailure(int pageNumber, String message, RetrofitException exception, int requestMode) {
        Log.e(message);
        mRecyclerView.setView(PlaceHolderView.ERROR, message);
    }


    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

    }
}

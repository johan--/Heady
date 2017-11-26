package com.heady.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.heady.R;
import com.heady.adapter.ProductsAdapter;
import com.heady.util.NetworkUtil;
import com.heady.util.PlaceHolderView;
import com.heady.util.logger.Log;
import com.heady.util.realm.CategoryManager;
import com.heady.util.realm.DbType;
import com.heady.util.realm.RankingManager;
import com.network.api.HeadyApi;
import com.network.interfaces.CommonResponseListener;
import com.network.model.HeadyModel;
import com.network.model.Products;
import com.network.utils.RetrofitConstants;
import com.network.utils.error.RetrofitException;

import java.util.List;

import io.realm.RealmResults;

/**
 * Created by Yogi.
 */

public class MainActivity extends BaseListActivity<ProductsAdapter> implements CommonResponseListener<HeadyModel> {
    private HeadyApi mHeadyApi;
    private RealmResults<Products> mProductList;
    private RankingManager rankingManager;
    private CategoryManager categoryManager;
    private int sortCheckedItem = -1;

    public static void start(Activity activity, boolean finishPreviousActivity) {
        if (!(activity instanceof MainActivity)) {
            Intent intent = new Intent(activity, MainActivity.class);
            activity.startActivity(intent);
            if (finishPreviousActivity) {
                activity.finish();
            }
        }
    }

    @Override
    protected void initApi() {
        mHeadyApi = new HeadyApi(this);
    }

    @Override
    protected void setupRealm() {
        rankingManager = new RankingManager(DbType.Heady.RANKING);
        categoryManager = new CategoryManager(DbType.Heady.CATEGORIES);
        mProductList = categoryManager.getProducts();
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
    public ProductsAdapter getAdapterInstance() {
//        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        return new ProductsAdapter(mProductList);
    }

    @Override
    public String getToolBarTitle() {
        return "Heady";
    }

    @Override
    public void onSuccess(int pageNumber, final HeadyModel headyModel, int requestMode) {
        Log.d();
        mRecyclerView.setView(PlaceHolderView.CONTENT);
        rankingManager.setData(headyModel.rankings);
        categoryManager.setData(headyModel.categories);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:

                break;
            case R.id.action_sort:
                showSortDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSortDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sort By");
        final List<String> rankingTitles = rankingManager.getRankingTitles();
        CharSequence[] array = new CharSequence[rankingTitles.size()];
        rankingTitles.toArray(array);
        builder.setSingleChoiceItems(array, sortCheckedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                sortCheckedItem = i;
                Log.e(rankingTitles.get(i));
                dialog.dismiss();
                mProductList = categoryManager.getProductsSorted("");
                mAdapter.notifyDataSetChanged();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}

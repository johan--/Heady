package com.heady.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;

import com.heady.R;
import com.heady.adapter.ProductsAdapter;
import com.heady.util.logger.Log;
import com.heady.util.realm.CategoryManager;
import com.heady.util.realm.DbType;
import com.network.model.Categories;
import com.network.model.Products;

import java.util.List;

import io.realm.RealmResults;

/**
 * Created by Yogi.
 */

public class ProductListActivity extends BaseListActivity {
    private RealmResults<Products> mProductList;
    private CategoryManager categoryManager;
    private int sortCheckedItem = -1;
    private int categoryId;
    private ProductsAdapter mAdapter;

    public static void start(Activity activity, int categoryId) {
        if (!(activity instanceof ProductListActivity)) {
            Intent intent = new Intent(activity, ProductListActivity.class);
            intent.putExtra(Categories.ID, categoryId);
            activity.startActivity(intent);
        }
    }

    @Override
    protected void getIntentData(Intent intent) {
        super.getIntentData(intent);
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            categoryId = bundle.getInt(Categories.ID);
        }
    }


    @Override
    protected void setupRealm() {
        categoryManager = new CategoryManager(DbType.Heady.CATEGORIES);
        if (categoryId != -1) {
            mProductList = categoryManager.getCategoryProducts(categoryId);
            setToolBarTitle(categoryManager.findModel(categoryId).name);
        } else {
            mProductList = categoryManager.getAllProducts();
            setToolBarTitle("All Products");
        }
    }

    @Override
    protected void setupAdapter() {
        if (mProductList != null && mProductList.size() > 0) {
            hideProgress();
            mAdapter = new ProductsAdapter(mProductList);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            setEmptyViewData("No Products found under this Category", R.drawable.ic_placeholder);
        }
    }


    @Override
    protected String getToolBarTitle() {
        return "Show Product";
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        menu.findItem(R.id.action_sort).setVisible(categoryId == -1);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort:
                showSortDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSortDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sort By");
        final List<String> rankingTitles = categoryManager.getRankingTitles();
        CharSequence[] array = new CharSequence[rankingTitles.size()];
        rankingTitles.toArray(array);
        builder.setSingleChoiceItems(array, sortCheckedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                sortCheckedItem = i;
                dialog.dismiss();
                // TODO: 3/12/17 update logic later
                switch (i) {
                    case 0:
                        mProductList = categoryManager.getProductsSorted("viewCount");
                        break;
                    case 1:
                        mProductList = categoryManager.getProductsSorted("orderCount");
                        break;
                    case 2:
                        mProductList = categoryManager.getProductsSorted("shares");
                        break;
                }
                if (mAdapter != null && mProductList != null) {
                    Log.e(mProductList.size());
                    mAdapter.updateData(mProductList);
                    mAdapter.notifyDataSetChanged();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void initApi() {
    }

    @Override
    protected void callServer() {

    }

}

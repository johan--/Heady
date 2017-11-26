package com.heady.activity;

import android.content.Context;
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
import com.heady.util.realm.RankingManager;
import com.network.model.Categories;
import com.network.model.Products;

import java.util.List;

import io.realm.RealmList;

/**
 * Created by Yogi.
 */

public class MainActivity extends BaseListActivity {
    private RealmList<Products> mProductList;
    private RankingManager rankingManager;
    private CategoryManager categoryManager;
    private int sortCheckedItem = -1;
    private int categoryId;

    public static void start(Context context, int categoryId) {
        if (!(context instanceof MainActivity)) {
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra(Categories.ID, categoryId);
            context.startActivity(intent);
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
        rankingManager = new RankingManager(DbType.Heady.RANKING);
        categoryManager = new CategoryManager(DbType.Heady.CATEGORIES);
        mProductList = categoryManager.getCategoryProducts(categoryId);
    }

    @Override
    protected void setupAdapter() {
        if (mProductList != null && mProductList.size() > 0) {
            hideProgress();
            mRecyclerView.setAdapter(new ProductsAdapter(mProductList));
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
        final List<String> rankingTitles = rankingManager.getRankingTitles();
        CharSequence[] array = new CharSequence[rankingTitles.size()];
        rankingTitles.toArray(array);
        builder.setSingleChoiceItems(array, sortCheckedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                sortCheckedItem = i;
                Log.e(rankingTitles.get(i));
                dialog.dismiss();
//                mProductList = categoryManager.getProductsSorted("");
//                mAdapter.notifyDataSetChanged();
                // TODO: 27/11/2017  
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

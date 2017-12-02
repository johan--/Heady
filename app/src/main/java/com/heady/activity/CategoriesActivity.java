package com.heady.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.heady.R;
import com.heady.adapter.CategoryAdapter;
import com.heady.util.NetworkUtil;
import com.heady.util.logger.Log;
import com.heady.util.realm.CategoryManager;
import com.heady.util.realm.DbType;
import com.heady.util.realm.RankingManager;
import com.network.api.HeadyApi;
import com.network.interfaces.CommonResponseListener;
import com.network.model.Categories;
import com.network.model.HeadyModel;
import com.network.model.Products;
import com.network.utils.RetrofitConstants;
import com.network.utils.error.RetrofitException;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by Yogi.
 */

public class CategoriesActivity extends BaseListActivity implements CommonResponseListener<HeadyModel>, BaseQuickAdapter.OnItemClickListener {
    private CategoryAdapter mCategoryAdapter;
    private HeadyApi mHeadyApi;
    private RankingManager rankingManager;
    private CategoryManager categoryManager;
    private RealmResults<Categories> mCategoryRealmResults;

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
        mCategoryRealmResults = categoryManager.getMainCategories();
        mCategoryAdapter = new CategoryAdapter(mCategoryRealmResults);
        mCategoryAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mCategoryAdapter);
        mCategoryRealmResults.addChangeListener(new RealmChangeListener<RealmResults<Categories>>() {
            @Override
            public void onChange(@NonNull RealmResults<Categories> categories) {
                mCategoryRealmResults = categoryManager.getMainCategories();
                if (mCategoryAdapter != null) {
                    mCategoryAdapter.updateData(mCategoryRealmResults);
                    mCategoryAdapter.notifyDataSetChanged();
                }
            }
        });
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
    public void onSuccess(int pageNumber, final HeadyModel headyModel, int requestMode) {
        hideProgress();
        Log.d();
//        rankingManager.setData(headyModel.rankings);
        categoryManager.setData(headyModel.categories);
        categoryManager.getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                // TODO: 3/12/17 refactor later
                for (int i = 0; i < headyModel.rankings.size(); i++) {
                    RealmList<Products> products = headyModel.rankings.get(i).products;
                    for (int j = 0; j < products.size(); j++) {
                        Products product = categoryManager.findProduct(products.get(j).id);
                        if (products.get(j).viewCount != 0) {
                            product.viewCount = products.get(j).viewCount;
                        } else if (products.get(j).shares != 0) {
                            product.shares = products.get(i).shares;
                        } else if (products.get(j).orderCount != 0) {
                            product.orderCount = products.get(j).orderCount;
                        }
                        realm.insertOrUpdate(product);
                    }
                }
                realm.insertOrUpdate(headyModel.rankings);
            }
        });


    }

    @Override
    public void onNoContent(int pageNumber, int requestMode) {
        setEmptyViewData("No categories found", R.drawable.ic_placeholder);
    }

    @Override
    public void onFailure(int pageNumber, String message, RetrofitException exception, int requestMode) {
        setErrorMessage(message);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (mCategoryRealmResults == null)
            return;
        Categories categories = mCategoryRealmResults.get(position);
        if (categories == null)
            return;
        Log.e(categories.name + "\t" + categories.id);
        List<Integer> subCategories = categoryManager.getSubCategories(categories.id);
        if (subCategories == null) {
            Log.e();
            ProductListActivity.start(this, categories.id);
        } else {
            setToolBarTitle(categories.name);
            mCategoryRealmResults = categoryManager.getCategories(subCategories);
            if (mCategoryAdapter != null) {
                mCategoryAdapter.updateData(mCategoryRealmResults);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // TODO: 2/12/17  handle back press logic for categories
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_categories, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_show_products:
                ProductListActivity.start(this, -1);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

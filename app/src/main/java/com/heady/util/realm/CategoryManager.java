package com.heady.util.realm;

import android.support.annotation.NonNull;

import com.network.model.Categories;
import com.network.model.Products;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by Yogi.
 */

public class CategoryManager extends Manager<Categories> {
    public CategoryManager(@DbType.Heady String type) {
        super(type);
    }

    @Override
    public RealmResults<Categories> getData() {
        return realm.where(Categories.class).findAll();
    }

    @Override
    public void setData(final List<Categories> data) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.copyToRealmOrUpdate(data);
            }
        });
    }

    @Override
    public void setData(Categories data) {

    }

    @Override
    public Categories findModel(String permalink) {
        return realm.where(Categories.class).equalTo(Categories.ID, permalink).findFirst();
    }

    @Override
    public void deleteData() {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.where(Categories.class).findAll().deleteAllFromRealm();
            }
        });
    }

    @Override
    public void deleteModel(String permalink) {

    }

    @Override
    protected RealmConfiguration getRealmConfig() {
        return getRealmConfiguration(DbType.Heady.CATEGORIES);
    }

    public RealmResults<Products> getProducts() {
        return realm.where(Products.class).findAll();
    }

    public RealmResults<Products> getProductsSorted(String column) {
        RealmQuery<Products> where = realm.where(Products.class);
        return where.findAllSortedAsync("viewCount", Sort.ASCENDING);
    }
}

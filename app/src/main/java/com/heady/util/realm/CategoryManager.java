package com.heady.util.realm;

import android.support.annotation.NonNull;

import com.network.model.Categories;
import com.network.model.Products;
import com.network.model.Rankings;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
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
    public void setData(final Categories data) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.copyToRealmOrUpdate(data);
            }
        });
    }

    @Override
    public Categories findModel(int id) {
        return realm.where(Categories.class).equalTo(Categories.ID, id).findFirst();
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
    public void deleteModel(final int id) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.where(Categories.class).equalTo(Categories.ID, id).findFirst().deleteFromRealm();
            }
        });
    }

    @Override
    protected RealmConfiguration getRealmConfig() {
        return getRealmConfiguration(DbType.Heady.CATEGORIES);
    }

    //-----------------------Custom Queries------------------//

    /**
     * @return all products
     */
    public RealmResults<Products> getAllProducts() {
        return realm.where(Products.class).findAll();
    }

    public Products findProduct(int id) {
        return realm.where(Products.class).equalTo(Products.ID, id).findFirst();
    }

    /**
     * @return all products
     */
    public RealmResults<Products> getSelectedProducts(List<Integer> list) {
        Integer[] integers = new Integer[list.size()];
        list.toArray(integers);
        return realm.where(Products.class).in(Products.ID, integers).findAll();
    }

    /**
     * @param id category id
     * @return Product list of single category
     */
    public RealmResults<Products> getCategoryProducts(int id) {
        Categories first = realm.where(Categories.class).equalTo(Categories.ID, id).findFirst();
        if (first != null) {
            return first.products.where().findAll();
        }
        return null;
    }

    public RealmResults<Products> getProductsSorted(String column) {
        return realm.where(Products.class).findAllSortedAsync(column, Sort.DESCENDING);
    }

    /**
     * @return All Sub categories ids
     */
    private List<Integer> getAllSubCategories() {
        RealmResults<Categories> categories = realm.where(Categories.class).findAll();
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < categories.size(); i++) {
            RealmList<Integer> childCategories = categories.get(i).childCategories;
            list.addAll(childCategories);
        }
        return list;
    }

    /**
     * @return Main Categories which don't have parent categories
     */
    public RealmResults<Categories> getMainCategories() {
        List<Integer> list = getAllSubCategories();
        RealmQuery<Categories> q = realm.where(Categories.class);
        for (int i = 0; i < list.size(); i++) {
            q = q.notEqualTo(Categories.ID, list.get(i));
        }
        return q.findAll();
    }

    /**
     * @param id category id
     * @return Sub categories of Single Category
     */
    public List<Integer> getSubCategories(int id) {
        Categories first = realm.where(Categories.class).equalTo(Categories.ID, id).findFirst();
        return first != null && first.childCategories != null && first.childCategories.size() > 0 ? first.childCategories : null;
    }

    /**
     * @param list of sub categories ids
     * @return Categories of respective sub categories
     */
    public RealmResults<Categories> getCategories(List<Integer> list) {
        Integer[] integers = new Integer[list.size()];
        list.toArray(integers);
        RealmQuery<Categories> q = realm.where(Categories.class).in(Categories.ID, integers);
        return q.findAll();
    }


    public List<String> getRankingTitles() {
        RealmResults<Rankings> rankingsList = realm.where(Rankings.class).findAll();
        List<String> titles = new ArrayList<>();
        if (rankingsList == null) {
            return titles;
        }
        for (int i = 0; i < rankingsList.size(); i++) {
            titles.add(rankingsList.get(i).ranking);
        }
        return titles;
    }
}

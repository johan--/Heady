package com.heady.util.realm;

import android.support.annotation.NonNull;

import com.heady.adapter.model.Category;
import com.heady.adapter.model.SubCategory;
import com.heady.util.logger.Log;
import com.network.model.Categories;
import com.network.model.Products;

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
        setExpandableList();
    }

    @Override
    public void setData(Categories data) {

    }

    @Override
    public Categories findModel(int permalink) {
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

    public RealmResults<Products> getAllProducts() {
        return realm.where(Products.class).findAll();
    }

    public RealmList<Products> getCategoryProducts(int id) {
        Categories first = realm.where(Categories.class).equalTo(Categories.ID, id).findFirst();
        if (first != null) {
            return first.products;
        }
        return null;
    }

    public RealmResults<Products> getProductsSorted(String column) {
        RealmQuery<Products> where = realm.where(Products.class);
        return where.findAllSortedAsync("viewCount", Sort.ASCENDING);
    }

    private String findCategoryName(int id) {
        return findModel(id).name;
    }

    private void setExpandableList() {
        try {
            final RealmList<Category> categoryRealmList = new RealmList<>();

            RealmResults<Categories> data = getData();
            for (int i = 0; i < data.size(); i++) {
                RealmList<SubCategory> subCategoryList = new RealmList<>();
                RealmList<Integer> childCategories = data.get(i).childCategories;
                for (int j = 0; j < childCategories.size(); j++) {
                    String catName = findCategoryName(childCategories.get(j));
                    SubCategory subCategory = new SubCategory(childCategories.get(j), catName);
                    subCategoryList.add(subCategory);
                }
                Category category = new Category(data.get(i).id, data.get(i).name, subCategoryList);
                categoryRealmList.add(category);
            }

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(@NonNull Realm realm) {
                    for (Category recipe : categoryRealmList) {
                        Category realmRecipe = realm.where(Category.class).equalTo(Categories.ID, recipe.getId()).findFirst();
                        if (realmRecipe != null) {
                            recipe.setExpanded(realmRecipe.isExpanded());
                        }
                    }
                    realm.insertOrUpdate(categoryRealmList);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Error in saving expandable list data");
        }
    }

    public RealmResults<Category> getExpandableList() {
        return realm.where(Category.class).findAll();
    }
}

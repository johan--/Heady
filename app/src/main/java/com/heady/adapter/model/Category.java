package com.heady.adapter.model;

import io.realm.Parent;
import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by Yogi.
 */
@RealmClass
public class Category implements RealmModel, Parent<SubCategory> {
    @PrimaryKey
    private int id;
    private String name;
    private RealmList<SubCategory> subCategories = new RealmList<>();
    private boolean expanded;

    public Category() {
    }

    public Category(int id, String name, RealmList<SubCategory> subCategories) {
        this.id = id;
        this.name = name;
        this.subCategories = subCategories;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    @Override
    public RealmList<SubCategory> getChildList() {
        return subCategories;
    }

    public SubCategory getSubCategory(int position) {
        return subCategories.get(position);
    }

    @Override
    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

}

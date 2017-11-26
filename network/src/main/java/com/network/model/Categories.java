package com.network.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class Categories extends RealmObject {
    @Ignore
    public static final String ID = "id";
    @PrimaryKey
    public int id;
    public String name;
    public RealmList<Products> products;
    public RealmList<Integer> childCategories;
}

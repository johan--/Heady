package com.network.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class Products extends RealmObject {
    @Ignore
    public static final String ID = "id";
    @PrimaryKey
    public int id;
    public String name;
    public int viewCount;
    public int shares;
    public int orderCount;
    public String dateAdded;
    public RealmList<Variants> variants;
    public Tax tax;
}

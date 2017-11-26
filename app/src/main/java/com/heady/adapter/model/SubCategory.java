package com.heady.adapter.model;

import io.realm.Child;
import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by Yogi.
 */
@RealmClass
public class SubCategory implements RealmModel, Child {

    @PrimaryKey
    private int id;
    private String name;

    public SubCategory() {
        // realm constructor
    }

    public SubCategory(int id, String name) {
        this.id=id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}

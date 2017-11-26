package com.network.model;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class Variants extends RealmObject {
    @Ignore
    public static final String ID = "id";
    @PrimaryKey
    public int id;
    public String color;
    public int size;
    public int price;
}

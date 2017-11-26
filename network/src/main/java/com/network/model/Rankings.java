package com.network.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Yogi.
 */

public class Rankings extends RealmObject {
    @Ignore
    public static final String RANKING = "ranking";
    @PrimaryKey
    public String ranking;
    public RealmList<Products> products;
}

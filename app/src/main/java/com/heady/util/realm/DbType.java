package com.heady.util.realm;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.heady.util.realm.DbType.Heady.PRODUCTS;

/**
 * Created by Yogi.
 */
@Retention(RetentionPolicy.SOURCE)
public @interface DbType {
    @StringDef({PRODUCTS})
    @interface Heady {
        String PRODUCTS = "products";
    }
}

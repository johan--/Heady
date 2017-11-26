package com.heady.util.realm;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.heady.util.realm.DbType.Heady.CATEGORIES;
import static com.heady.util.realm.DbType.Heady.RANKING;

/**
 * Created by Yogi.
 */
@Retention(RetentionPolicy.SOURCE)
public @interface DbType {
    @StringDef({CATEGORIES, RANKING})
    @interface Heady {
        String CATEGORIES = "categories";
        String RANKING = "ranking";
    }
}

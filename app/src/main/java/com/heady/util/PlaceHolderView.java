package com.heady.util;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({PlaceHolderView.ERROR, PlaceHolderView.INTERNET, PlaceHolderView.NO_DATA, PlaceHolderView.LOADING, PlaceHolderView.CONTENT})
public @interface PlaceHolderView {
    int ERROR = 1;
    int INTERNET = 2;
    int NO_DATA = 3;
    int LOADING = 4;
    int CONTENT = 5;
}
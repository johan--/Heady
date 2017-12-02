package com.heady.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseViewHolder;
import com.heady.R;
import com.network.model.Categories;

import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

/**
 * Created by Yogi.
 */

public class CategoryAdapter extends RealmRecyclerViewAdapter<Categories, BaseViewHolder> {


    public CategoryAdapter(@Nullable RealmResults<Categories> realmResults) {
        super(R.layout.item_category, realmResults, Categories.ID);
    }

    @Override
    protected void convert(BaseViewHolder helper, Categories item) {
        helper.setText(R.id.categoryName, item.name);
    }
}

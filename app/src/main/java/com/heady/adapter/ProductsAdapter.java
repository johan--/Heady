package com.heady.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseViewHolder;
import com.heady.R;
import com.network.model.Categories;
import com.network.model.Products;

import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

/**
 * Created by Yogi.
 */

public class ProductsAdapter extends RealmRecyclerViewAdapter<Products, BaseViewHolder> {

    public ProductsAdapter(@Nullable RealmResults<Products> realmResults) {
        super(R.layout.item_product, realmResults, Categories.ID);
    }

    @Override
    protected void convert(BaseViewHolder holder, Products item) {
        holder.setText(R.id.productNameTextView, item.name);
    }
}

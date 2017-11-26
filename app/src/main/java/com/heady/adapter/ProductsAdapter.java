package com.heady.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSpinner;

import com.chad.library.adapter.base.BaseViewHolder;
import com.heady.R;
import com.network.model.Products;

import io.realm.RealmList;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by Yogi.
 */

public class ProductsAdapter extends RealmRecyclerViewAdapter<Products, BaseViewHolder> {

    public ProductsAdapter(@Nullable RealmList<Products> realmResults) {
        super(R.layout.item_product, realmResults, Products.ID);
    }

    @Override
    protected void convert(BaseViewHolder holder, Products item) {
        holder.setText(R.id.productNameTextView, item.name);
        AppCompatSpinner spinner = holder.getView(R.id.variantSpinner);
        if (item.variants != null) {
            holder.setVisible(R.id.variantSpinner, true);
            spinner.setAdapter(new ProductVariantsAdapter(mContext, item.variants, item.tax));
        } else {
            holder.setVisible(R.id.variantSpinner, false);
        }
    }

}

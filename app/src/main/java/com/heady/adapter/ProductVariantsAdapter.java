package com.heady.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.heady.R;
import com.network.model.Tax;
import com.network.model.Variants;

import java.util.List;

/**
 * Created by Yogi.
 */

public class ProductVariantsAdapter extends BaseAdapter {

    private Context mContext;
    private List<Variants> variantsList;
    private LayoutInflater mInflater;
    private Tax tax;

    ProductVariantsAdapter(Context mContext, List<Variants> variantsList, Tax tax) {
        this.mContext = mContext;
        this.variantsList = variantsList;
        this.tax = tax;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return variantsList.size();
    }

    @Override
    public Variants getItem(int i) {
        return variantsList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_product_variant, viewGroup, false);
            holder = new ViewHolder();
            holder.colorTextView = convertView.findViewById(R.id.colorTextView);
            holder.sizeTextView = convertView.findViewById(R.id.sizeView);
            holder.priceTextView = convertView.findViewById(R.id.priceTextView);
            holder.taxTextView = convertView.findViewById(R.id.taxTextView);
            // Bind the data efficiently with the holder.
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.colorTextView.setText(variantsList.get(i).color);
        holder.sizeTextView.setText(String.format("Size: %s", variantsList.get(i).size));
        holder.priceTextView.setText(String.format("Rs: %s", variantsList.get(i).price));
        holder.taxTextView.setText(String.format("+ %s : Rs. %s", tax.name, tax.value));

        return convertView;
    }

    static class ViewHolder {
        TextView priceTextView, sizeTextView, colorTextView, taxTextView;
    }
}

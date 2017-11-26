package com.heady.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.heady.R;
import com.network.model.Variants;

import java.util.List;

/**
 * Created by Yogi.
 */

public class ProductVariantsAdapter extends BaseAdapter {

    private Context mContext;
    private List<Variants> variantsList;
    private LayoutInflater mInflater;


    public ProductVariantsAdapter(Context mContext, List<Variants> variantsList) {
        this.mContext = mContext;
        this.variantsList = variantsList;
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
            // Bind the data efficiently with the holder.
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.colorTextView.setText(variantsList.get(i).color);
        holder.sizeTextView.setText("Size: " + variantsList.get(i).size);
        holder.priceTextView.setText("Rs: " + variantsList.get(i).price);

        return convertView;
    }

    static class ViewHolder {
        TextView priceTextView, sizeTextView, colorTextView;
    }
}

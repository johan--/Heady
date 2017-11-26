package com.heady.adapter.viewholder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.heady.R;
import com.heady.activity.MainActivity;
import com.heady.adapter.model.SubCategory;

import io.realm.ChildViewHolder;

/**
 * Created by Yogi.
 */

public class SubCategoryViewHolder extends ChildViewHolder implements View.OnClickListener {
    private final Context mContext;
    private TextView subCategoryTextView;
    private SubCategory subCategory;

    /**
     * Default constructor.
     *
     * @param context
     * @param itemView The {@link View} being hosted in this ViewHolder
     */
    public SubCategoryViewHolder(Context context, @NonNull View itemView) {
        super(itemView);
        mContext = context;
        subCategoryTextView = itemView.findViewById(R.id.subcategoryName);
        itemView.setOnClickListener(this);
    }

    public void bind(@NonNull SubCategory subCategory) {
        this.subCategory = subCategory;
        subCategoryTextView.setText(subCategory.getName());
    }

    @Override
    public void onClick(View view) {
        MainActivity.start(mContext, subCategory.getId());
    }

}

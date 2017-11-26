package com.heady.adapter;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.heady.R;
import com.heady.adapter.model.Category;
import com.heady.adapter.model.SubCategory;
import com.heady.adapter.viewholder.CategoryViewHolder;
import com.heady.adapter.viewholder.SubCategoryViewHolder;

import io.realm.OrderedRealmCollection;
import io.realm.RealmExpandableRecyclerAdapter;

/**
 * Created by Yogi.
 */

public class CategoryAdapter extends RealmExpandableRecyclerAdapter<Category, SubCategory, CategoryViewHolder, SubCategoryViewHolder> {

    /**
     * Primary constructor. Sets up parentList {@link #flatItemList} and {@link #flatItemList}.
     *
     * @param parentList List of all parents to be displayed in the RecyclerView that this
     *                   adapter is linked to
     */
    public CategoryAdapter(@NonNull OrderedRealmCollection<Category> parentList) {
        super(parentList);
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(parentViewGroup.getContext());
        View recipeView = mInflater.inflate(R.layout.item_category, parentViewGroup, false);
        return new CategoryViewHolder(parentViewGroup.getContext(),recipeView);
    }

    @NonNull
    @Override
    public SubCategoryViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(childViewGroup.getContext());
        View recipeView = mInflater.inflate(R.layout.item_sub_category, childViewGroup, false);
        return new SubCategoryViewHolder(childViewGroup.getContext(),recipeView);
    }

    @Override
    public void onBindParentViewHolder(@NonNull CategoryViewHolder parentViewHolder, int parentPosition, @NonNull Category parent) {
        parentViewHolder.bind(parent);
    }

    @Override
    public void onBindChildViewHolder(@NonNull SubCategoryViewHolder childViewHolder, int parentPosition, int childPosition, @NonNull SubCategory child) {
        childViewHolder.bind(child);
    }
}

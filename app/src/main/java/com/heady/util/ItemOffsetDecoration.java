package com.heady.util;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

/**
 * Created by Yogi.
 */

public class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

    private int mItemOffset;

    public ItemOffsetDecoration(int itemOffset) {
        mItemOffset = itemOffset;
    }

    public ItemOffsetDecoration(@NonNull Context context, @DimenRes int itemOffsetId) {
        this(context.getResources().getDimensionPixelSize(itemOffsetId));
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        if (parent.getLayoutManager() instanceof LinearLayoutManager) {
            if (((LinearLayoutManager) parent.getLayoutManager()).getOrientation() == LinearLayoutManager.VERTICAL) {
                outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);
            }
            if (((LinearLayoutManager) parent.getLayoutManager()).getOrientation() == LinearLayoutManager.HORIZONTAL) {
                outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);
            }
        }

        if (parent.getLayoutManager() instanceof GridLayoutManager)
            outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);

        if (parent.getLayoutManager() instanceof StaggeredGridLayoutManager)
            outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);

    }
}

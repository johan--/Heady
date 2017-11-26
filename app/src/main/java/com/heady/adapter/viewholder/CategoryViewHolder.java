package com.heady.adapter.viewholder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.heady.R;
import com.heady.activity.MainActivity;
import com.heady.adapter.model.Category;
import com.heady.util.realm.DbType;
import com.heady.util.realm.RealmManager;

import io.realm.ParentViewHolder;
import io.realm.Realm;

/**
 * Created by Yogi.
 */

public class CategoryViewHolder extends ParentViewHolder {
    private static final float INITIAL_POSITION = 0.0f;
    private static final float ROTATED_POSITION = 180f;
    @NonNull
    private final ImageView arrowExpandImageView;
    private TextView categoryTextView;
    private Category category;
    private Context mContext;

    /**
     * Default constructor.
     *
     * @param context
     * @param itemView The {@link View} being hosted in this ViewHolder
     */
    public CategoryViewHolder(Context context, @NonNull View itemView) {
        super(itemView);
        mContext = context;
        categoryTextView = itemView.findViewById(R.id.categoryName);
        arrowExpandImageView = itemView.findViewById(R.id.arrow_expand_imageview);
    }

    public void bind(@NonNull Category category) {
        this.category = category;
        categoryTextView.setText(category.getName());
        arrowExpandImageView.setVisibility(category.getChildList().size() > 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setExpanded(boolean expanded) {
        super.setExpanded(expanded);
        if (expanded) {
            arrowExpandImageView.setRotation(ROTATED_POSITION);
        } else {
            arrowExpandImageView.setRotation(INITIAL_POSITION);
        }
    }


    @Override
    public void onExpansionToggled(final boolean expanded) {
        super.onExpansionToggled(expanded);
        RotateAnimation rotateAnimation;
        if (expanded) { // rotate clockwise
            rotateAnimation = new RotateAnimation(ROTATED_POSITION,
                    INITIAL_POSITION,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        } else { // rotate counterclockwise
            rotateAnimation = new RotateAnimation(-1 * ROTATED_POSITION,
                    INITIAL_POSITION,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        }

        rotateAnimation.setDuration(200);
        rotateAnimation.setFillAfter(true);
        arrowExpandImageView.startAnimation(rotateAnimation);

        Realm realm = RealmManager.instance(DbType.Heady.CATEGORIES);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                category.setExpanded(!expanded);
            }
        });
        realm.close();
    }

    @Override
    public void onClick(@NonNull View v) {
        if (category.getChildList().size() > 0) {
            super.onClick(v);
        } else {
            MainActivity.start(mContext, category.getId());
        }
    }

    @Override
    public void setMainItemClickToExpand() {
        itemView.setOnClickListener(this);
    }
}

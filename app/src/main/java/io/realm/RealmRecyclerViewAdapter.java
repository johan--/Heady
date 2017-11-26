package io.realm;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;


/**
 * @author ranjit
 */
public abstract class RealmRecyclerViewAdapter<T extends RealmModel, VH extends BaseViewHolder> extends BaseQuickAdapter<T, VH> {

    private final boolean updateOnModification;
    private final OrderedRealmCollectionChangeListener listener;
    @Nullable
    private OrderedRealmCollection<T> adapterData;

    public RealmRecyclerViewAdapter(@LayoutRes int layoutResId, @Nullable OrderedRealmCollection<T> realmResults,
                                    String primaryColumnName) {
        super(layoutResId, realmResults);

        if (realmResults != null && !realmResults.isManaged())
            throw new IllegalStateException("Only use this adapter with managed RealmCollection, " +
                    "for un-managed lists you can just use the BaseRecyclerViewAdapter");
        this.adapterData = realmResults;
        this.listener = createListener();
        this.updateOnModification = true;
    }

    private OrderedRealmCollectionChangeListener createListener() {
        return new OrderedRealmCollectionChangeListener() {
            @Override
            public void onChange(Object collection, @Nullable OrderedCollectionChangeSet changeSet) {
                // null Changes means the async query returns the first time.
                if (changeSet == null) {
                    notifyDataSetChanged();
                    return;
                }
                // For deletions, the adapter has to be notified in reverse order.
                OrderedCollectionChangeSet.Range[] deletions = changeSet.getDeletionRanges();
                for (int i = deletions.length - 1; i >= 0; i--) {
                    OrderedCollectionChangeSet.Range range = deletions[i];
                    notifyItemRangeRemoved(range.startIndex, range.length);
                }

                OrderedCollectionChangeSet.Range[] insertions = changeSet.getInsertionRanges();
                for (OrderedCollectionChangeSet.Range range : insertions) {
                    notifyItemRangeInserted(range.startIndex, range.length);
                }

                if (!updateOnModification) {
                    return;
                }

                OrderedCollectionChangeSet.Range[] modifications = changeSet.getChangeRanges();
                for (OrderedCollectionChangeSet.Range range : modifications) {
                    notifyItemRangeChanged(range.startIndex, range.length);
                }
            }
        };
    }

    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (isDataValid()) {
            //noinspection ConstantConditions
            addListener(adapterData);
        }
    }

    @Override
    public void onDetachedFromRecyclerView(final RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if (isDataValid()) {
            //noinspection ConstantConditions
            removeListener(adapterData);
        }
    }

    /**
     * Returns the current ID for an item. Note that item IDs are not stable so you cannot rely on the item ID being the
     * same after notifyDataSetChanged() or {@link #updateData(OrderedRealmCollection)} has been called.
     *
     * @param index position of item in the adapter.
     * @return current item ID.
     */
    @Override
    public long getItemId(final int index) {
        return index;
    }

    @Override
    public int getItemCount() {
        //noinspection ConstantConditions
        return isDataValid() ? adapterData.size() : 0;
    }

    /**
     * Returns the item associated with the specified position.
     * Can return {@code null} if provided Realm instance by {@link OrderedRealmCollection} is closed.
     *
     * @param index index of the item.
     * @return the item at the specified position, {@code null} if adapter data is not valid.
     */
    @SuppressWarnings("WeakerAccess")
    @Nullable
    public T getItem(int index) {
        //noinspection ConstantConditions
        return isDataValid() ? adapterData.get(index) : null;
    }

    /**
     * Returns data associated with this adapter.
     *
     * @return adapter data.
     */
    @Nullable
    public OrderedRealmCollection<T> getData() {
        return adapterData;
    }

    /**
     * Updates the data associated to the Adapter. Useful when the query has been changed.
     * If the query does not change you might consider using the automaticUpdate feature.
     *
     * @param data the new {@link OrderedRealmCollection} to display.
     */
    @SuppressWarnings({"WeakerAccess", "unused"})
    public void updateData(@Nullable OrderedRealmCollection<T> data) {

        if (isDataValid()) {
            //noinspection ConstantConditions
            removeListener(adapterData);
        }
        if (data != null) {
            addListener(data);
        }

        this.adapterData = data;
        notifyDataSetChanged();
    }

    private void addListener(@NonNull OrderedRealmCollection<T> data) {
        if (data instanceof RealmResults) {
            RealmResults<T> results = (RealmResults<T>) data;
            //noinspection unchecked
            results.addChangeListener(listener);
        } else if (data instanceof RealmList) {
            RealmList<T> list = (RealmList<T>) data;
            //noinspection unchecked
            list.addChangeListener(listener);
        } else {
            throw new IllegalArgumentException("RealmCollection not supported: " + data.getClass());
        }
    }

    private void removeListener(@NonNull OrderedRealmCollection<T> data) {
        if (data instanceof RealmResults) {
            RealmResults<T> results = (RealmResults<T>) data;
            //noinspection unchecked
            results.removeChangeListener(listener);
        } else if (data instanceof RealmList) {
            RealmList<T> list = (RealmList<T>) data;
            //noinspection unchecked
            list.removeChangeListener(listener);
        } else {
            throw new IllegalArgumentException("RealmCollection not supported: " + data.getClass());
        }
    }

    private boolean isDataValid() {
        return adapterData != null && adapterData.isValid();
    }
}
    /*private static final List<Long> EMPTY_LIST = new ArrayList<>(0);

    protected RealmResults<T> realmResults;
    protected List ids;

    private RealmChangeListener<RealmResults<T>> listener;

    private long animatePrimaryColumnIndex;
    private RealmFieldType animatePrimaryIdType;
    private long animateExtraColumnIndex;
    private RealmFieldType animateExtraIdType;


    public RealmRecyclerViewAdapter(@LayoutRes int layoutResId, @Nullable RealmResults<T> realmResults,
                                    String primaryColumnName) {
        super(layoutResId, realmResults);
        if (realmResults != null && !realmResults.isManaged())
            throw new IllegalStateException("Only use this adapter with managed RealmCollection, " +
                    "for un-managed lists you can just use the BaseAdapter");
        setHasStableIds(true);
        this.listener = getRealmChangeListener();

        if (realmResults != null) {
            animatePrimaryColumnIndex = realmResults.getTable().getPrimaryKey();
        }
        if (animatePrimaryColumnIndex == Table.NO_MATCH) {
            throw new IllegalStateException(
                    "Animating the results requires a primaryKey.");
        }
        if (realmResults == null) {
            throw new IllegalStateException("RealmList should not be null.");
        }
        animatePrimaryIdType = realmResults.getTable().getColumnType(animatePrimaryColumnIndex);
        if (animatePrimaryIdType != RealmFieldType.INTEGER &&
                animatePrimaryIdType != RealmFieldType.STRING) {
            throw new IllegalStateException(
                    "Animating requires a primary key of type Integer/Long or String");
        }

        animateExtraColumnIndex = realmResults.getTable().getTable()
                .getColumnIndex(primaryColumnName);
        if (animateExtraColumnIndex == Table.NO_MATCH) {
            throw new IllegalStateException(
                    "Animating the results requires a valid animateColumnName.");
        }
        animateExtraIdType = realmResults.getTable().getColumnType(animateExtraColumnIndex);
        if (animateExtraIdType != RealmFieldType.INTEGER &&
                animateExtraIdType != RealmFieldType.STRING &&
                animateExtraIdType != RealmFieldType.DATE) {
            throw new IllegalStateException(
                    "Animating requires a animateColumnName of type Int/Long or String");
        }
        updateRealmResults(realmResults);
    }

    *//**
 * Ensure {@link #close()} is called whenever {@link Realm#close()} is called to ensure that the
 * {@link #realmResults} are invalidated and the change listener removed.
 * <p>
 * Update the RealmResults associated with the Adapter. Useful when the query has been changed.
 * If the query does not change you might consider using the automaticUpdate feature.
 *
 * @param queryResults the new RealmResults coming from the new query.
 * <p>
 * Update the RealmResults associated with the Adapter. Useful when the query has been changed.
 * If the query does not change you might consider using the automaticUpdate feature.
 * @param queryResults the new RealmResults coming from the new query.
 * <p>
 * Update the RealmResults associated with the Adapter. Useful when the query has been changed.
 * If the query does not change you might consider using the automaticUpdate feature.
 * @param queryResults the new RealmResults coming from the new query.
 * <p>
 * Update the RealmResults associated with the Adapter. Useful when the query has been changed.
 * If the query does not change you might consider using the automaticUpdate feature.
 * @param queryResults the new RealmResults coming from the new query.
 * <p>
 * Update the RealmResults associated with the Adapter. Useful when the query has been changed.
 * If the query does not change you might consider using the automaticUpdate feature.
 * @param queryResults the new RealmResults coming from the new query.
 * <p>
 * Update the RealmResults associated with the Adapter. Useful when the query has been changed.
 * If the query does not change you might consider using the automaticUpdate feature.
 * @param queryResults the new RealmResults coming from the new query.
 * <p>
 * Update the RealmResults associated with the Adapter. Useful when the query has been changed.
 * If the query does not change you might consider using the automaticUpdate feature.
 * @param queryResults the new RealmResults coming from the new query.
 * <p>
 * Update the RealmResults associated with the Adapter. Useful when the query has been changed.
 * If the query does not change you might consider using the automaticUpdate feature.
 * @param queryResults the new RealmResults coming from the new query.
 * <p>
 * Update the RealmResults associated with the Adapter. Useful when the query has been changed.
 * If the query does not change you might consider using the automaticUpdate feature.
 * @param queryResults the new RealmResults coming from the new query.
 * <p>
 * Update the RealmResults associated with the Adapter. Useful when the query has been changed.
 * If the query does not change you might consider using the automaticUpdate feature.
 * @param queryResults the new RealmResults coming from the new query.
 * <p>
 * Update the RealmResults associated with the Adapter. Useful when the query has been changed.
 * If the query does not change you might consider using the automaticUpdate feature.
 * @param queryResults the new RealmResults coming from the new query.
 * <p>
 * Update the RealmResults associated with the Adapter. Useful when the query has been changed.
 * If the query does not change you might consider using the automaticUpdate feature.
 * @param queryResults the new RealmResults coming from the new query.
 * <p>
 * Update the RealmResults associated with the Adapter. Useful when the query has been changed.
 * If the query does not change you might consider using the automaticUpdate feature.
 * @param queryResults the new RealmResults coming from the new query.
 * <p>
 * Update the RealmResults associated with the Adapter. Useful when the query has been changed.
 * If the query does not change you might consider using the automaticUpdate feature.
 * @param queryResults the new RealmResults coming from the new query.
 * <p>
 * Update the RealmResults associated with the Adapter. Useful when the query has been changed.
 * If the query does not change you might consider using the automaticUpdate feature.
 * @param queryResults the new RealmResults coming from the new query.
 * <p>
 * Update the RealmResults associated with the Adapter. Useful when the query has been changed.
 * If the query does not change you might consider using the automaticUpdate feature.
 * @param queryResults the new RealmResults coming from the new query.
 * <p>
 * Update the RealmResults associated with the Adapter. Useful when the query has been changed.
 * If the query does not change you might consider using the automaticUpdate feature.
 * @param queryResults the new RealmResults coming from the new query.
 * <p>
 * Update the RealmResults associated with the Adapter. Useful when the query has been changed.
 * If the query does not change you might consider using the automaticUpdate feature.
 * @param queryResults the new RealmResults coming from the new query.
 * <p>
 * Update the RealmResults associated with the Adapter. Useful when the query has been changed.
 * If the query does not change you might consider using the automaticUpdate feature.
 * @param queryResults the new RealmResults coming from the new query.
 * <p>
 * Update the RealmResults associated with the Adapter. Useful when the query has been changed.
 * If the query does not change you might consider using the automaticUpdate feature.
 * @param queryResults the new RealmResults coming from the new query.
 * <p>
 * Update the RealmResults associated with the Adapter. Useful when the query has been changed.
 * If the query does not change you might consider using the automaticUpdate feature.
 * @param queryResults the new RealmResults coming from the new query.
 * <p>
 * Update the RealmResults associated with the Adapter. Useful when the query has been changed.
 * If the query does not change you might consider using the automaticUpdate feature.
 * @param queryResults the new RealmResults coming from the new query.
 * <p>
 * Update the RealmResults associated with the Adapter. Useful when the query has been changed.
 * If the query does not change you might consider using the automaticUpdate feature.
 * @param queryResults the new RealmResults coming from the new query.
 * <p>
 * Update the RealmResults associated with the Adapter. Useful when the query has been changed.
 * If the query does not change you might consider using the automaticUpdate feature.
 * @param queryResults the new RealmResults coming from the new query.
 * <p>
 * Update the RealmResults associated with the Adapter. Useful when the query has been changed.
 * If the query does not change you might consider using the automaticUpdate feature.
 * @param queryResults the new RealmResults coming from the new query.
 * <p>
 * Update the RealmResults associated with the Adapter. Useful when the query has been changed.
 * If the query does not change you might consider using the automaticUpdate feature.
 * @param queryResults the new RealmResults coming from the new query.
 * <p>
 * Update the RealmResults associated with the Adapter. Useful when the query has been changed.
 * If the query does not change you might consider using the automaticUpdate feature.
 * @param queryResults the new RealmResults coming from the new query.
 * <p>
 * Update the RealmResults associated with the Adapter. Useful when the query has been changed.
 * If the query does not change you might consider using the automaticUpdate feature.
 * @param queryResults the new RealmResults coming from the new query.
 * <p>
 * Update the RealmResults associated with the Adapter. Useful when the query has been changed.
 * If the query does not change you might consider using the automaticUpdate feature.
 * @param queryResults the new RealmResults coming from the new query.
 * <p>
 * Update the RealmResults associated with the Adapter. Useful when the query has been changed.
 * If the query does not change you might consider using the automaticUpdate feature.
 * @param queryResults the new RealmResults coming from the new query.
 * <p>
 * Update the RealmResults associated with the Adapter. Useful when the query has been changed.
 * If the query does not change you might consider using the automaticUpdate feature.
 * @param queryResults the new RealmResults coming from the new query.
 * <p>
 * Update the RealmResults associated with the Adapter. Useful when the query has been changed.
 * If the query does not change you might consider using the automaticUpdate feature.
 * @param queryResults the new RealmResults coming from the new query.
 * <p>
 * Update the RealmResults associated with the Adapter. Useful when the query has been changed.
 * If the query does not change you might consider using the automaticUpdate feature.
 * @param queryResults the new RealmResults coming from the new query.
 * <p>
 * Update the RealmResults associated with the Adapter. Useful when the query has been changed.
 * If the query does not change you might consider using the automaticUpdate feature.
 * @param queryResults the new RealmResults coming from the new query.
 * <p>
 * Update the RealmResults associated with the Adapter. Useful when the query has been changed.
 * If the query does not change you might consider using the automaticUpdate feature.
 * @param queryResults the new RealmResults coming from the new query.
 * <p>
 * Update the RealmResults associated with the Adapter. Useful when the query has been changed.
 * If the query does not change you might consider using the automaticUpdate feature.
 * @param queryResults the new RealmResults coming from the new query.
 * <p>
 * Update the RealmResults associated with the Adapter. Useful when the query has been changed.
 * If the query does not change you might consider using the automaticUpdate feature.
 * @param queryResults the new RealmResults coming from the new query.
 * <p>
 * Update the RealmResults associated with the Adapter. Useful when the query has been changed.
 * If the query does not change you might consider using the automaticUpdate feature.
 * @param queryResults the new RealmResults coming from the new query.
 * <p>
 * Update the RealmResults associated with the Adapter. Useful when the query has been changed.
 * If the query does not change you might consider using the automaticUpdate feature.
 * @param queryResults the new RealmResults coming from the new query.
 *//*
    public void close() {
        updateRealmResults(null);
    }

    *//**
 * Update the RealmResults associated with the Adapter. Useful when the query has been changed.
 * If the query does not change you might consider using the automaticUpdate feature.
 *
 * @param queryResults the new RealmResults coming from the new query.
 *//*
    public void updateRealmResults(RealmResults<T> queryResults) {
        if (listener != null && realmResults != null) {
            realmResults.removeChangeListener(listener);
        }

        realmResults = queryResults;
        if (listener != null && realmResults != null) {
            realmResults.addChangeListener(listener);
        }

        ids = getIdsOfRealmResults();

        notifyDataSetChanged();
    }


    private RealmChangeListener<RealmResults<T>> getRealmChangeListener() {
        return new RealmChangeListener<RealmResults<T>>() {
            @Override
            public void onChange(RealmResults<T> element) {
                if (ids != null && !ids.isEmpty()) {
                    List newIds = getIdsOfRealmResults();
                    // If the list is now empty, just notify the mRecyclerView of the change.
                    if (newIds.isEmpty()) {
                        ids = newIds;
                        notifyDataSetChanged();
                        return;
                    }
                    Patch patch = DiffUtils.diff(ids, newIds);
                    List<Delta> deltas = patch.getDeltas();
                    ids = newIds;
                    if (!deltas.isEmpty()) {
                        for (Delta delta : deltas) {
                            if (delta.getType() == Delta.TYPE.INSERT) {
                                notifyItemRangeInserted(
                                        delta.getRevised().getPosition(),
                                        delta.getRevised().size());
                            } else {
                                if (delta.getType() == Delta.TYPE.DELETE) {
                                    notifyItemRangeRemoved(
                                            delta.getOriginal().getPosition(),
                                            delta.getOriginal().size());
                                } else {
                                    notifyItemRangeChanged(
                                            delta.getRevised().getPosition(),
                                            delta.getRevised().size());
                                }
                            }
                        }
                    }
                } else {
                    notifyDataSetChanged();
                    ids = getIdsOfRealmResults();
                }
            }
        };
    }


    private List getIdsOfRealmResults() {
        if (realmResults == null || realmResults.size() == 0) {
            return EMPTY_LIST;
        }


        List ids = new ArrayList(realmResults.size());
        for (int i = 0; i < realmResults.size(); i++) {
            ids.add(getRealmRowId(i));
        }
        return ids;
    }

    private Object getRealmRowId(int realmIndex) {
        Object rowPrimaryId;
        RealmObjectProxy proxy = (RealmObjectProxy) realmResults.get(realmIndex);
        Row row = proxy.realmGet$proxyState().getRow$realm();

        if (animatePrimaryIdType == RealmFieldType.INTEGER) {
            rowPrimaryId = row.getLong(animatePrimaryColumnIndex);
        } else if (animatePrimaryIdType == RealmFieldType.STRING) {
            rowPrimaryId = row.getString(animatePrimaryColumnIndex);
        } else {
            throw new IllegalStateException("Unknown animatedIdType");
        }

        if (animateExtraColumnIndex != -1) {
            String rowPrimaryIdStr = (rowPrimaryId instanceof String)
                    ? (String) rowPrimaryId : String.valueOf(rowPrimaryId);
            if (animateExtraIdType == RealmFieldType.INTEGER) {
                return rowPrimaryIdStr + String.valueOf(row.getLong(animateExtraColumnIndex));
            } else if (animateExtraIdType == RealmFieldType.STRING) {
                return rowPrimaryIdStr + row.getString(animateExtraColumnIndex);
            } else if (animateExtraIdType == RealmFieldType.DATE) {
                return rowPrimaryIdStr + row.getDate(animateExtraColumnIndex).getTime();
            } else {
                throw new IllegalStateException("Unknown animateExtraIdType");
            }
        } else {
            return rowPrimaryId;
        }
    }
}*/
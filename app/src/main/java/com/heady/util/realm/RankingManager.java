package com.heady.util.realm;

import android.support.annotation.NonNull;

import com.network.model.Rankings;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by Yogi.
 */

public class RankingManager extends Manager<Rankings> {
    public RankingManager(@DbType.Heady String type) {
        super(type);
    }

    @Override
    public RealmResults<Rankings> getData() {
        return realm.where(Rankings.class).findAll();
    }

    @Override
    public void setData(final List<Rankings> data) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.copyToRealmOrUpdate(data);
            }
        });
    }

    @Override
    public void setData(Rankings data) {

    }

    @Override
    public Rankings findModel(String permalink) {
        return realm.where(Rankings.class).equalTo(Rankings.RANKING, permalink).findFirst();
    }

    @Override
    public void deleteData() {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.where(Rankings.class).findAll().deleteAllFromRealm();
            }
        });
    }

    @Override
    public void deleteModel(String permalink) {

    }

    @Override
    protected RealmConfiguration getRealmConfig() {
        return getRealmConfiguration(DbType.Heady.RANKING);
    }

    @Override
    public Realm getRealm() {
        return null;
    }

    @Override
    public void destroy() {

    }

    @Override
    public void clearAll() {

    }

    public List<String> getRankingTitles() {
        RealmResults<Rankings> rankingsList = getData();
        List<String> titles = new ArrayList<>();
        if (rankingsList == null) {
            return titles;
        }
        for (int i = 0; i < rankingsList.size(); i++) {
            titles.add(rankingsList.get(i).ranking);
        }
        return titles;
    }
}

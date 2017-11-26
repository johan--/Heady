package com.heady.util.realm;

import android.support.annotation.NonNull;

import com.network.utils.realm.CustomRealmModule;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.exceptions.RealmException;

/**
 * Created by Yogi.
 */

public class RealmManager {
    private static RealmManager realmManager;

    private static RealmManager newInstance() {
        if (realmManager == null) {
            realmManager = new RealmManager();
        }
        return realmManager;
    }

    public static Realm instance(@DbType String type) {
        RealmConfiguration realmConfig = getConfig(type);
        if (realmConfig == null) {
            throw new RealmException("Please check that you've initialised the realm db");
        }

        return Realm.getInstance(realmConfig);
    }

    private static RealmConfiguration getConfig(String realmTableName) {
        return new RealmConfiguration.Builder()
                .name(realmTableName)
                .modules(Realm.getDefaultModule(), new CustomRealmModule())
                .deleteRealmIfMigrationNeeded()
                .build();
    }

    public static <Model extends RealmObject> void setData(Realm realm, final List<Model> list) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.copyToRealmOrUpdate(list);
            }
        });
    }

    public static <Model extends RealmObject> void setDataAsync(Realm realm, final List<Model> list) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.copyToRealmOrUpdate(list);
            }
        });
    }

    private RealmConfiguration getRealmConfiguration(String realmTableName) {
        return new RealmConfiguration.Builder()
                .name(realmTableName)
                .modules(Realm.getDefaultModule(), new CustomRealmModule())
                .deleteRealmIfMigrationNeeded()
                .build();
    }
}

package com.heady.util.realm;

import android.support.annotation.NonNull;

import com.network.utils.realm.CustomRealmModule;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.exceptions.RealmException;

/**
 * Created by Yogi.
 */

public abstract class Manager<Model extends RealmObject> implements DbTransaction<Model> {

    protected abstract RealmConfiguration getRealmConfig();

    @DbType
    protected final String mType;
    protected Realm realm;

    public Manager(@DbType String type) {
        mType = type;
        RealmConfiguration realmConfig = getRealmConfig();
        if (realmConfig == null) {
            throw new RealmException("Please check that you've initialised the realm db");
        }
        realm = Realm.getInstance(realmConfig);
    }

    @Override
    public Realm getRealm() {
        return realm;
    }

    protected static RealmConfiguration getRealmConfiguration(String realmTableName) {
        return new RealmConfiguration.Builder()
                .name(realmTableName)
                .modules(Realm.getDefaultModule(), new CustomRealmModule())
                .deleteRealmIfMigrationNeeded()
                .build();
    }

    /**
     * This will clear the complete realm database table for particular realm instance
     */
    @Override
    public void clearAll() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.deleteAll();
            }
        });
    }

    @Override
    public void destroy() {
        if (realm != null && !realm.isClosed()) {
            realm.removeAllChangeListeners();
            realm.close();
        }
    }

}

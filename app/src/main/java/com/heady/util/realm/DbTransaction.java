package com.heady.util.realm;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

public interface DbTransaction<Model extends RealmObject> {
    RealmResults<Model> getData();

    void setData(Model data);

    void setData(List<Model> data);

    Model findModel(int id);

    void deleteData();

    void deleteModel(int id);

    Realm getRealm();

    void destroy();

    void clearAll();
}
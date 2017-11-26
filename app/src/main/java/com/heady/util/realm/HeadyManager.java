//package com.heady.util.realm;
//
//import android.support.annotation.NonNull;
//
//import com.heady.util.logger.Log;
//import com.network.model.HeadyModel;
//import com.network.model.Products;
//import com.network.model.Rankings;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import io.realm.Realm;
//import io.realm.RealmConfiguration;
//import io.realm.RealmQuery;
//import io.realm.RealmResults;
//import io.realm.Sort;
//
///**
// * Created by Yogi.
// */
//
//public class HeadyManager extends Manager<HeadyModel> {
//    public HeadyManager(@DbType String type) {
//        super(type);
//    }
//
//    @Override
//    public RealmResults<HeadyModel> getData() {
//        return realm.where(HeadyModel.class).findAll();
//    }
//
//    @Override
//    public void setData(final HeadyModel data) {
//        realm.executeTransaction(new Realm.Transaction() {
//            @Override
//            public void execute(@NonNull Realm realm) {
//                realm.insertOrUpdate(data);
//            }
//        });
//    }
//
//    @Override
//    public void setData(List<HeadyModel> data) {
//
//    }
//
//    @Override
//    public HeadyModel findModel(String permalink) {
//        return null;
//    }
//
//    @Override
//    public void deleteData() {
//        realm.executeTransaction(new Realm.Transaction() {
//            @Override
//            public void execute(@NonNull Realm realm) {
//                realm.where(HeadyModel.class).findAll().deleteAllFromRealm();
//            }
//        });
//    }
//
//    @Override
//    public void deleteModel(String permalink) {
//
//    }
//
//    @Override
//    protected RealmConfiguration getRealmConfig() {
//        return getRealmConfiguration(DbType.Heady.HEADY);
//    }
//
//    public RealmResults<Products> getProducts() {
//        return realm.where(Products.class).findAll();
//    }
//
//    public List<String> getRankingTitles() {
//        RealmResults<Rankings> rankingsList = realm.where(Rankings.class).findAll();
//        List<String> titles = new ArrayList<>();
//        if (rankingsList == null) {
//            return titles;
//        }
//        for (int i = 0; i < rankingsList.size(); i++) {
//            titles.add(rankingsList.get(i).ranking);
//        }
//        return titles;
//    }
//
//    public RealmResults<Products> getProductsSorted(String ranking) {
//        Log.e(ranking);
//        RealmQuery<Products> where = realm.where(Products.class);
//        return where.findAllSortedAsync("viewCount", Sort.ASCENDING);
//    }
//}

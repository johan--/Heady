package com.network.interfaces;


import com.network.model.HeadyModel;

import retrofit2.http.GET;

/**
 * @author yogi
 */
public interface ApiManager {
    String BASE_URL = "https://stark-spire-93433.herokuapp.com/";


    interface Heady {

        @GET("json")
        RetrofitCall<HeadyModel> getProductList();
    }
}

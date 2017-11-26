package com.network.interfaces;


import com.network.utils.error.RetrofitException;

/**
 * @author yogi
 */
public interface CommonResponseListener<Response> {
    void onSuccess(int pageNumber, Response response, int requestMode);

    void onNoContent(int pageNumber, int requestMode);

    void onFailure(int pageNumber, String message, RetrofitException exception, int requestMode);
}

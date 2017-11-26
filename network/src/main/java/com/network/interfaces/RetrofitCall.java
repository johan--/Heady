package com.network.interfaces;

/**
 * @author yogi
 */
public interface RetrofitCall<T> {

    void cancel();

    void enqueue(RetrofitCallbackListener<T> callback);

    RetrofitCall<T> clone();
}

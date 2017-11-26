package com.network.interfaces;


import com.network.utils.error.RetrofitException;

/**
 * @author yogi
 */

public interface RetrofitCallbackListener<T> {
    /**
     * Called when response is received successfully for response code (200-300) except 204.
     */
    void success(T response);

    /**
     * Called for when enqueued call has failed to delivered successful response
     */
    void onFailure(RetrofitException exception);
}

package com.network.api;

import android.util.Log;

import com.network.RetrofitApiClient;
import com.network.interfaces.ApiManager;
import com.network.interfaces.CommonResponseListener;
import com.network.interfaces.RetrofitCall;
import com.network.interfaces.RetrofitCallbackListener;
import com.network.model.HeadyModel;
import com.network.utils.error.RetrofitException;

/**
 * Created by Yogi.
 */

public class HeadyApi {
    private CommonResponseListener<HeadyModel> mListener;

    public HeadyApi(CommonResponseListener<HeadyModel> mListener) {
        this.mListener = mListener;
    }

    public void getProducts(final int requestMode) {
        RetrofitCall<HeadyModel> call = RetrofitApiClient.createService(ApiManager.Heady.class).getProductList();
        call.enqueue(new RetrofitCallbackListener<HeadyModel>() {
            @Override
            public void success(HeadyModel response) {
                if (response == null || response.categories == null) {
                    RetrofitException retrofitException = RetrofitException.nullResponse();
                    mListener.onFailure(-1, retrofitException.getMessage(), retrofitException, requestMode);
                    return;
                }

                if (response.categories.size() > 0) {
                    mListener.onSuccess(-1, response, requestMode);
                } else {
                    mListener.onNoContent(-1, requestMode);
                }
            }

            @Override
            public void onFailure(RetrofitException exception) {
                mListener.onFailure(-1, exception.getMessage(), exception, requestMode);
            }
        });
    }
}

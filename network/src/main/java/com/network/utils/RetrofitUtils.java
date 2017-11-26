package com.network.utils;

import com.network.RetrofitApiClient;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

/**
 * @author yogi
 */
public class RetrofitUtils {
    public static RetrofitError parseError(Response<?> response) {
        Converter<ResponseBody, RetrofitError> converter =
                RetrofitApiClient.getRetrofit()
                        .responseBodyConverter(RetrofitError.class, new Annotation[0]);
        RetrofitError error;
        try {
            error = converter.convert(response.errorBody());
        } catch (IOException e) {
            return new RetrofitError();
        }
        return error;
    }

    public static String getFieldValues(String permalink) {

        return null;
    }
}

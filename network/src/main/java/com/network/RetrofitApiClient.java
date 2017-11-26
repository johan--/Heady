package com.network;

import android.content.Context;

import com.desidime.network.BuildConfig;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.network.interfaces.ApiManager;
import com.network.utils.HttpLoggingInterceptor;
import com.network.utils.error.ErrorHandlingAdapter;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit client
 *
 * @author yogi
 */
public class RetrofitApiClient {
    private static final String TAG = "RetrofitApiClient";
    private static final int READ_TIMEOUT = 10;
    private static final int WRITE_TIMEOUT = 30;
    private static Retrofit mRetrofit;
    private static OkHttpClient okHttpClient;
    private static Gson gson = getGSON();
    private static Context mApplicationContext;


    public static void initRetrofitApiClient(Context context) {
        mApplicationContext = context;
    }
    /**
     * Get application context
     *
     * @return @{@link Context}
     */
    public static Context getApplicationContext() {
        return mApplicationContext;
    }

    /**
     * Create service to generate client
     *
     * @param serviceClass @{@link Class}
     * @return @{@link Class}
     */
    public static <S> S createService(Class<S> serviceClass) {
        okHttpClient = createClient();
        mRetrofit = new Retrofit.Builder()
                .baseUrl(ApiManager.BASE_URL)
                .addCallAdapterFactory(new ErrorHandlingAdapter())
                .addConverterFactory(GsonConverterFactory.create(getGSON()))
                .client(okHttpClient)
                .build();
        return mRetrofit.create(serviceClass);
    }


    /**
     * Create client  @{@link OkHttpClient}
     *
     * @return okhttp client @{@link OkHttpClient}
     */
    private static OkHttpClient createClient() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
//        httpClient.networkInterceptors().add(new Interceptor() {
//            @Override
//            public Response intercept(Chain chain) throws IOException {
//                return addHeaders(chain, sendAccessToken);
//            }
//        });
        // set the logger to log url and response code in Crashlytics
//        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//        logging.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.BASIC);
//        httpClient.addInterceptor(logging);

        return httpClient
                //add timeout
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .build();
    }

    /**
     * @return {@link Retrofit} object.
     */
    public static Retrofit getRetrofit() {
        return mRetrofit;
    }

    /**
     * @return {@link OkHttpClient} object.
     */
    private static OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }


    private static Gson getGSON() {
        if (gson == null) {
            gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        }
        return gson;
    }
}

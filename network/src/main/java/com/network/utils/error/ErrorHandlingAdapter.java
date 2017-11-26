package com.network.utils.error;

import com.network.interfaces.RetrofitCall;
import com.network.interfaces.RetrofitCallbackListener;
import com.network.utils.RetrofitConstants;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * @author yogi
 */

public class ErrorHandlingAdapter extends CallAdapter.Factory {
    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations,
                                 Retrofit retrofit) {
        if (getRawType(returnType) != RetrofitCall.class) {
            return null;
        }
        if (!(returnType instanceof ParameterizedType)) {
            throw new IllegalStateException(
                    "CustomCall must have generic type (e.g., CustomCall<ResponseBody>)");
        }
        Type responseType = getParameterUpperBound(0, (ParameterizedType) returnType);
        Executor callbackExecutor = retrofit.callbackExecutor();
        return new ExecutorCallbackCall<>(callbackExecutor, responseType);
    }

    private static final class ExecutorCallbackCall<T> implements CallAdapter<T, RetrofitCall<T>> {
        private final Executor callbackExecutor;
        private final Type type;

        ExecutorCallbackCall(Executor callbackExecutor, Type type) {
            this.callbackExecutor = callbackExecutor;
            this.type = type;
        }

        @Override
        public Type responseType() {
            return type;
        }

        @Override
        public RetrofitCall<T> adapt(Call<T> call) {
            return new ExecutorCallbackAdapter<>(callbackExecutor, call);
        }
    }

    private static final class ExecutorCallbackAdapter<T> implements RetrofitCall<T>, RetrofitConstants.ErrorCodes {
        private final Executor callbackExecutor;
        private final Call<T> call;

        ExecutorCallbackAdapter(Executor callbackExecutor, Call<T> call) {
            this.callbackExecutor = callbackExecutor;
            this.call = call;
        }

        @Override
        public void cancel() {
            call.cancel();
        }

        @Override
        public void enqueue(final RetrofitCallbackListener<T> callback) {
            call.enqueue(new Callback<T>() {
                @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
                @Override
                public void onResponse(Call<T> call, final Response<T> response) {
                    callbackExecutor.execute(
                            new Runnable() {
                                @Override
                                public void run() {
                                    int code = response.code();

                                    // this will represent successful request execution
                                    if (response.isSuccessful()) {
                                        if (code == NO_CONTENT || response.body() == null) {
                                            // 204 - no content
                                            callback.onFailure(RetrofitException.noContent(response));
                                        } else {
                                            // 200 - success
                                            // 201 - for create
                                            callback.success(response.body());
                                        }
                                        return;
                                    }

                                    // will represent failure of some sort in request execution
                                    RetrofitException exception = null;
                                    if (code == UN_AUTHORIZED) {
                                        // 401 un-authorised
                                        exception = RetrofitException.unAuthorised(response);
                                    } else if (code >= CLIENT_ERROR && code < INTERNAL_SERVER_ERROR) {
                                        if (code == UN_PROCESSABLE) {
                                            // 422 = invalid data-type.
                                            // 422 e.g deal deleted. or no or invalid param passed, corrupt data, suspended user
                                            String message = null;
//                                            try {
//                                                if (response.errorBody() != null)
//                                                    message = new Errors(response.errorBody().bytes()).errorMessage;
//                                            } catch (IOException e) {
//                                                e.printStackTrace();
//                                            }
                                            exception = RetrofitException.unProcessableEntity(message, response);
                                        } else if (code == PAGE_NOT_FOUND) {
                                            // 404 - page not found
                                            exception = RetrofitException.noApiExists(response);
                                        } else if (code == FORBIDDEN) {
                                            // 403 - no rights - forbidden
                                            exception = RetrofitException.forbidden(response);
                                        } else {
                                            exception = RetrofitException.clientError(response);
                                        }
                                    } else if (code >= INTERNAL_SERVER_ERROR && code < SERVER_ISSUE) {
                                        if (code == SERVER_DOWN) {
                                            // 503 - server down
                                            exception = RetrofitException.serverDown(response);
                                        } else {
                                            // 500 - error on server
                                            exception = RetrofitException.serverError(response);
                                        }
                                    } else {
                                        exception = RetrofitException.unexpectedError(new RuntimeException("Unexpected response " + response));
                                    }
                                    callback.onFailure(exception);
                                }
                            });

                }

                @Override
                public void onFailure(Call<T> call, final Throwable t) {
                    callbackExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            // check if throwable is null
                            if (t == null) {
                                callback.onFailure(RetrofitException.unexpectedError(new RuntimeException("Unexpected error occurred")));
                                return;
                            }

                            // check if request is cancelled.
//                            if (RetrofitConstants.Params.CANCELED.equals(t.getMessage())) {
//                                callback.onFailure(RetrofitException.requestCancelled());
//                                return;
//                            }

                            RetrofitException exception;
                            if (t instanceof IOException) {
                                if (t instanceof SocketTimeoutException) {
                                    exception = RetrofitException.timeOutException((SocketTimeoutException) t);
                                } else {
                                    exception = RetrofitException.networkError((IOException) t);
                                }
                            } else {
                                exception = RetrofitException.unexpectedError(t);
                            }
                            callback.onFailure(exception);
                        }
                    });
                }
            });
        }

        @Override
        public RetrofitCall<T> clone() {
            return new ExecutorCallbackAdapter<>(callbackExecutor, call.clone());
        }
    }
}

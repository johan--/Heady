package com.network.utils.error;

import android.support.annotation.IntDef;

import com.desidime.network.R;
import com.network.RetrofitApiClient;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.SocketTimeoutException;

import retrofit2.Response;

/**
 * @author yogi
 */

public final class RetrofitException<T> {

    private final Response<T> mResponse;
    private final String mMessage;
    private final Throwable mException;
    private final
    @ErrorType
    int mType;

    private RetrofitException(String message, Response<T> response, @ErrorType int type, Throwable exception) {
        mResponse = response;
        mType = type;
        mMessage = message;
        mException = exception;
    }

    static <T> RetrofitException networkError(IOException exception) {
        return new RetrofitException<T>(RetrofitApiClient.getApplicationContext().getString(R.string.no_internet_connection), null, ErrorType.NETWORK, exception);
    }

    static <T> RetrofitException unexpectedError(Throwable exception) {
        return new RetrofitException<T>(exception.getMessage(), null, ErrorType.UNEXPECTED, exception);
    }

    static <T> RetrofitException timeOutException(SocketTimeoutException exception) {
        return new RetrofitException<T>(RetrofitApiClient.getApplicationContext().getString(R.string.time_out_message), null, ErrorType.TIMEOUT, exception);
    }

    static <T> RetrofitException noContent(Response<T> response) {
        return new RetrofitException<T>(RetrofitApiClient.getApplicationContext().getString(R.string.no_content), response, ErrorType.NO_CONTENT,
                new RuntimeException(RetrofitApiClient.getApplicationContext().getString(R.string.no_content)));
    }

    static <T> RetrofitException unAuthorised(Response<T> response) {
        return new RetrofitException<T>(RetrofitApiClient.getApplicationContext().getString(R.string.unauthorized), response, ErrorType.UNAUTHENTICATED,
                new RuntimeException(RetrofitApiClient.getApplicationContext().getString(R.string.unauthorized)));
    }

    /**
     * Called for error codes between (400, 500) responses, except 401, 422, 404 and 403
     */
    static <T> RetrofitException clientError(Response<T> response) {
        return new RetrofitException<T>(RetrofitApiClient.getApplicationContext().getString(R.string.something_went_wrong), response, ErrorType.CLIENT,
                new RuntimeException(RetrofitApiClient.getApplicationContext().getString(R.string.something_went_wrong)));
    }

    /**
     * Called for un processable entity - for response code 422
     */
    static <T> RetrofitException unProcessableEntity(String message, Response<T> response) {
        return new RetrofitException<T>(message, response, ErrorType.UNPROCESSED_ENTITY, new RuntimeException(message));
    }

    static <T> RetrofitException noApiExists(Response<T> response) {
        return new RetrofitException<T>(RetrofitApiClient.getApplicationContext().getString(R.string.something_went_wrong), response, ErrorType.NO_API_EXISTS,
                new RuntimeException(RetrofitApiClient.getApplicationContext().getString(R.string.something_went_wrong)));
    }

    static <T> RetrofitException forbidden(Response<T> response) {
        return new RetrofitException<T>(RetrofitApiClient.getApplicationContext().getString(R.string.bad_request_error), response, ErrorType.FORBIDDEN,
                new RuntimeException(RetrofitApiClient.getApplicationContext().getString(R.string.bad_request_error)));
    }

    static <T> RetrofitException serverDown(Response<T> response) {
        return new RetrofitException<T>(RetrofitApiClient.getApplicationContext().getString(R.string.server_error), response, ErrorType.SERVER_DOWN,
                new RuntimeException(RetrofitApiClient.getApplicationContext().getString(R.string.server_error)));
    }

    static <T> RetrofitException serverError(Response<T> response) {
        return new RetrofitException<T>(RetrofitApiClient.getApplicationContext().getString(R.string.server_error), response, ErrorType.SERVER_ERROR,
                new RuntimeException(RetrofitApiClient.getApplicationContext().getString(R.string.server_error)));
    }

    static <T> RetrofitException requestCancelled() {
        return new RetrofitException<T>(RetrofitApiClient.getApplicationContext().getString(R.string.request_cancelled), null, ErrorType.REQUEST_CANCELLED,
                new RuntimeException(RetrofitApiClient.getApplicationContext().getString(R.string.request_cancelled)));
    }

    public static <T> RetrofitException nullResponse() {
        return new RetrofitException<T>(RetrofitApiClient.getApplicationContext().getString(R.string.something_went_wrong),
                null,
                ErrorType.CLIENT,
                new NullPointerException(RetrofitApiClient.getApplicationContext().getString(R.string.something_went_wrong)));
    }

    /**
     * Handle all errors and create error messages accordingly
     *
     * @param exception
     * @return
     */
    public static String handleError(RetrofitException exception) {
        String message = null;
        switch (exception.getTypeOfError()) {
            case ErrorType.UNAUTHENTICATED:
                break;
            case ErrorType.TIMEOUT:
                break;
            case ErrorType.UNPROCESSED_ENTITY:
                break;
            case ErrorType.SERVER_DOWN:
                break;
            default:
                message = "Something went wrong";
        }
        return message;
    }

    /**
     * @return error messages returned from exception
     */
    public String getMessage() {
        return mMessage;
    }

    /**
     * @return exception thrown at request failure.
     */
    public Throwable getException() {
        return mException;
    }

    /**
     * Response object containing status code, headers, body, etc.
     */
    public Response<T> getResponse() {
        return mResponse;
    }

    /**
     * The event kind which triggered this error.
     */
    public
    @ErrorType
    int getTypeOfError() {
        return mType;
    }

    /**
     * HTTP response body converted to specified {@code type}. {@code null} if there is no
     * response.
     * this is as per standard DeserializeErrorBody
     * https://github.com/square/retrofit/blob/master/samples/src/main/java/com/example/retrofit/DeserializeErrorBody.java
     *
     * @throws IOException if unable to convert the body to the specified {@code type}.
     */
    public Errors getErrorBodyAs() throws IOException {
        if (mResponse == null || mResponse.errorBody() == null) {
            return null;
        }
        return new Errors(mResponse.errorBody().bytes());
    }

    /**
     * Identifies the event kind which triggered a {@link RetrofitException}.
     */
    @IntDef({
            ErrorType.NETWORK, ErrorType.UNEXPECTED, ErrorType.TIMEOUT, ErrorType.NO_CONTENT, ErrorType.UNAUTHENTICATED,
            ErrorType.CLIENT, ErrorType.UNPROCESSED_ENTITY, ErrorType.NO_API_EXISTS, ErrorType.FORBIDDEN,
            ErrorType.SERVER_DOWN, ErrorType.SERVER_ERROR, ErrorType.REQUEST_CANCELLED, ErrorType.REFRESH_TOKEN_FAILURE
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface ErrorType {
        /**
         * An {@link IOException} occurred while communicating to the server.
         */
        int NETWORK = 1;
        /**
         * An internal error occurred while attempting to execute a request. It is best practice to
         * re-throw this exception so your application crashes.
         */
        int UNEXPECTED = 2;
        /**
         * An {@link SocketTimeoutException} error thrown when the timeout occurs while communicating
         * with the server
         */
        int TIMEOUT = 3;
        /**
         * This will be thrown when no content available is sent form server
         */
        int NO_CONTENT = 5;
        /**
         * Called for 401 responses. when user is not authenticated to use the service
         * Access is denied. generate new refresh token
         */
        int UNAUTHENTICATED = 6;
        /**
         * Called for response codes between 400 - 500
         */
        int CLIENT = 7;
        /**
         * Called for response code 422
         * means the server understands the content type of the request entity
         * but was unable to process the contained instructions
         */
        int UNPROCESSED_ENTITY = 8;
        /**
         * called for response 404 where api endpoint does not exists
         * to indicate that the client was able to communicate with a given server,
         * but the server could not find what was requested.
         */
        int NO_API_EXISTS = 9;
        /**
         * called when api response code 403 is received for the reason that
         * the page or resource you were trying to reach is absolutely forbidden for some reason.
         */
        int FORBIDDEN = 10;
        /**
         * called when server is down or
         * server is currently unable to handle the HTTP request due to a temporary overloading or
         * maintenance of the server.
         */
        int SERVER_DOWN = 11;
        /**
         * called when something has gone wrong on the server
         */
        int SERVER_ERROR = 12;
        /**
         * called when request is cancelled
         */
        int REQUEST_CANCELLED = 13;

        // When api is not able to recraete refreshtoken
        int REFRESH_TOKEN_FAILURE = 14;
    }
}

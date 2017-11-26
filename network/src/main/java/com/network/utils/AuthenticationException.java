package com.network.utils;


/**
 * @author yogi
 */

public class AuthenticationException extends RuntimeException {
    private static final String TAG = "AuthenticationException";

    public AuthenticationException(int errorCode) {
        super();
        if (errorCode != RetrofitConstants.ErrorCodes.UN_AUTHORIZED) {
            // handle 401
        }
    }

    @Override
    public String getMessage() {
        return "UserDetail needs to login again.";
    }
}

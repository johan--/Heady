package com.network.utils;

/**
 * @author yogi
 */
public interface RetrofitConstants {

    interface ErrorCodes {
        short NO_CONTENT = 204;
        short INTERNAL_SERVER_ERROR = 500;
        short SERVER_DOWN = 503;
        short CLIENT_ERROR = 400;
        short UN_AUTHORIZED = 401;
        short FORBIDDEN = 403;
        short PAGE_NOT_FOUND = 404;
        short UN_PROCESSABLE = 422;
        short SERVER_ISSUE = 600;
    }


    interface RequestMode {
        int GET_PRODUCTS = 1001;
    }

}
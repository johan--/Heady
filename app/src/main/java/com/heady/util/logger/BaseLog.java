package com.heady.util.logger;

/**
 * @author Yogi
 */
class BaseLog {

    private static final int MAX_LENGTH = 4000;

    static void printDefault(int type, String tag, String msg) {

        int index = 0;
        int length = msg.length();
        int countOfSub = length / MAX_LENGTH;

        if (countOfSub > 0) {
            for (int i = 0; i < countOfSub; i++) {
                String sub = msg.substring(index, index + MAX_LENGTH);
                printSub(type, tag, sub);
                index += MAX_LENGTH;
            }
            printSub(type, tag, msg.substring(index, length));
        } else {
            printSub(type, tag, msg);
        }
    }

    private static void printSub(int type, String tag, String sub) {
        switch (type) {
            case Log.VERBOSE:
                android.util.Log.v(tag, sub);
                break;
            case Log.DEBUG:
                android.util.Log.d(tag, sub);
                break;
            case Log.INFO:
                android.util.Log.i(tag, sub);
                break;
            case Log.WARN:
                android.util.Log.w(tag, sub);
                break;
            case Log.ERROR:
                android.util.Log.e(tag, sub);
                break;
            case Log.ASSERT:
                android.util.Log.wtf(tag, sub);
                break;
        }
    }

}

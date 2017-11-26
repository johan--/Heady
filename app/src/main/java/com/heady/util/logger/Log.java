package com.heady.util.logger;

import android.support.annotation.Nullable;
import android.support.compat.BuildConfig;
import android.text.TextUtils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * This is a Log tool，with this you can the following
 * <ol>
 * <li>use Log.d(),you could print whether the method execute,and the default tag is current class's name</li>
 * <li>use Log.d(msg),you could print log as before,and you could location the method with a click in Android Studio Logcat</li>
 * <li>use Log.json(),you could print json string with well format automatic</li>
 * </ol>
 *
 * @author Yogi
 */
public final class Log {

    static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final String NULL_TIPS = "Log with null object";

    private static final String DEFAULT_MESSAGE = "execute";
    private static final String PARAM = "Param";
    private static final String NULL = "null";
    private static final String TAG_DEFAULT = "DesiDime";
    private static final String SUFFIX = ".java";

    static final int JSON_INDENT = 4;


    public static final int ASSERT = 7;
    public static final int DEBUG = 3;
    public static final int ERROR = 6;
    public static final int INFO = 4;
    public static final int VERBOSE = 2;
    public static final int WARN = 5;

    private static final int JSON = 8;

    private static final int STACK_TRACE_INDEX_5 = 5;
    private static final int STACK_TRACE_INDEX_4 = 4;

    private static String mGlobalTag;
    private static boolean mIsGlobalTagEmpty = true;
    private static boolean IS_SHOW_LOG = BuildConfig.DEBUG;

    public static void init(boolean isShowLog) {
        IS_SHOW_LOG = isShowLog;
    }

    public static void init(boolean isShowLog, @Nullable String tag) {
        IS_SHOW_LOG = isShowLog;
        mGlobalTag = tag;
        mIsGlobalTagEmpty = TextUtils.isEmpty(mGlobalTag);
    }

    public static void v() {
        printLog(VERBOSE, null, DEFAULT_MESSAGE);
    }

    public static void v(Object msg) {
        printLog(VERBOSE, null, msg);
    }

    public static void v(String tag, Object... objects) {
        printLog(VERBOSE, tag, objects);
    }

    public static void d() {
        printLog(DEBUG, null, DEFAULT_MESSAGE);
    }

    public static void d(Object msg) {
        printLog(DEBUG, null, msg);
    }

    public static void d(String tag, Object... objects) {
        printLog(DEBUG, tag, objects);
    }

    public static void i() {
        printLog(INFO, null, DEFAULT_MESSAGE);
    }

    public static void i(Object msg) {
        printLog(INFO, null, msg);
    }

    public static void i(String tag, Object... objects) {
        printLog(INFO, tag, objects);
    }

    public static void w() {
        printLog(WARN, null, DEFAULT_MESSAGE);
    }

    public static void w(Object msg) {
        printLog(WARN, null, msg);
    }

    public static void w(String tag, Object... objects) {
        printLog(WARN, tag, objects);
    }

    public static void e() {
        printLog(ERROR, null, DEFAULT_MESSAGE);
    }

    public static void e(Object msg) {
        printLog(ERROR, null, msg);
    }

    public static void e(String tag, Object... objects) {
        printLog(ERROR, tag, objects);
    }

    public static void a() {
        printLog(ASSERT, null, DEFAULT_MESSAGE);
    }

    public static void a(Object msg) {
        printLog(ASSERT, null, msg);
    }

    public static void a(String tag, Object... objects) {
        printLog(ASSERT, tag, objects);
    }

    public static void json(String jsonFormat) {
        printLog(JSON, null, jsonFormat);
    }

    public static void json(String tag, String jsonFormat) {
        printLog(JSON, tag, jsonFormat);
    }

    public static void debug() {
        printDebug(null, DEFAULT_MESSAGE);
    }

    public static void debug(Object msg) {
        printDebug(null, msg);
    }

    public static void debug(String tag, Object... objects) {
        printDebug(tag, objects);
    }

    public static void trace() {
        printStackTrace();
    }

    private static void printStackTrace() {

        if (!IS_SHOW_LOG) {
            return;
        }

        Throwable tr = new Throwable();
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        pw.flush();
        String message = sw.toString();

        String traceString[] = message.split("\\n\\t");
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        for (String trace : traceString) {
            sb.append(trace).append("\n");
        }
        String[] contents = wrapperContent(STACK_TRACE_INDEX_4, null, sb.toString());
        String tag = contents[0];
        String msg = contents[1];
        String headString = contents[2];
        BaseLog.printDefault(DEBUG, tag, headString + msg);
    }

    private static void printLog(int type, String tagStr, Object... objects) {

        if (!IS_SHOW_LOG) {
            return;
        }

        String[] contents = wrapperContent(STACK_TRACE_INDEX_5, tagStr, objects);
        String tag = contents[0];
        String msg = contents[1];
        String headString = contents[2];

        switch (type) {
            case VERBOSE:
            case DEBUG:
            case INFO:
            case WARN:
            case ERROR:
            case ASSERT:
                BaseLog.printDefault(type, tag, headString + msg);
                break;
            case JSON:
                JsonLog.printJson(tag, msg, headString);
                break;
        }

    }

    private static void printDebug(String tagStr, Object... objects) {
        String[] contents = wrapperContent(STACK_TRACE_INDEX_5, tagStr, objects);
        String tag = contents[0];
        String msg = contents[1];
        String headString = contents[2];
        BaseLog.printDefault(DEBUG, tag, headString + msg);
    }

    private static String[] wrapperContent(int stackTraceIndex, String tagStr, Object... objects) {

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        StackTraceElement targetElement = stackTrace[stackTraceIndex];
        String className = targetElement.getClassName();
        String[] classNameInfo = className.split("\\.");
        if (classNameInfo.length > 0) {
            className = classNameInfo[classNameInfo.length - 1] + SUFFIX;
        }

        if (className.contains("$")) {
            className = className.split("\\$")[0] + SUFFIX;
        }

        String methodName = targetElement.getMethodName();
        int lineNumber = targetElement.getLineNumber();

        if (lineNumber < 0) {
            lineNumber = 0;
        }

        String tag = (tagStr == null ? className : tagStr);

        if (mIsGlobalTagEmpty && TextUtils.isEmpty(tag)) {
            tag = TAG_DEFAULT;
        } else if (!mIsGlobalTagEmpty) {
            tag = mGlobalTag;
        }

        String msg = (objects == null) ? NULL_TIPS : getObjectsString(objects);
        String headString = "[ (" + className + ":" + lineNumber + ")#" + methodName + " ] : ";

        return new String[]{tag, msg, headString};
    }

    private static String getObjectsString(Object... objects) {

        if (objects.length > 1) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("\n");
            for (int i = 0; i < objects.length; i++) {
                Object object = objects[i];
                if (object == null) {
                    stringBuilder.append(PARAM).append("[").append(i).append("]").append(" = ").append(NULL).append("\n");
                } else {
                    stringBuilder.append(PARAM).append("[").append(i).append("]").append(" = ").append(object.toString()).append("\n");
                }
            }
            return stringBuilder.toString();
        } else {
            Object object = objects[0];
            return object == null ? NULL : object.toString();
        }
    }

}

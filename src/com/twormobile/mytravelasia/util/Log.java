package com.twormobile.mytravelasia.util;

/**
 * A custom logger that respects AppConstants.DEBUG. It will only show debug messages if DEBUG is set to true.
 *
 * @see {android.util.Log}
 * @author avendael
 */
public class Log {
    private Log() {}
    /**
     * @see {android.util.Log.v}
     */
    public static void v(String tag, String msg) {
        android.util.Log.v(tag, msg);
    }

    /**
     * @see {android.util.Log.v}
     */
    public static void v(String tag, String msg, Throwable tr) {
        android.util.Log.v(tag, msg, tr);
    }

    /**
     * Same as android.util.Log.d, but will not do anything if @see {Constants.DEBUG} is false.
     *
     * @see {android.util.Log.d}
     */
    public static void d(String tag, String msg) {
        if (AppConstants.DEBUG) android.util.Log.d(tag, msg);
    }

    /**
     * Same as android.util.Log.d, but will not do anything if @see {Constants.DEBUG} is false.
     *
     * @see {android.util.Log.d}
     */
    public static void d(String tag, String msg, Throwable tr) {
        if (AppConstants.DEBUG) android.util.Log.d(tag, msg, tr);
    }

    /**
     * @see {android.util.Log.i}
     */
    public static void i(String tag, String msg) {
        android.util.Log.i(tag, msg);
    }

    /**
     * @see {android.util.Log.i}
     */
    public static void i(String tag, String msg, Throwable tr) {
        android.util.Log.i(tag, msg, tr);
    }

    /**
     * @see {android.util.Log.w}
     */
    public static void w(String tag, String msg) {
        android.util.Log.w(tag, msg);
    }

    /**
     * @see {android.util.Log.w}
     */
    public static void w(String tag, String msg, Throwable tr) {
        android.util.Log.w(tag, msg, tr);
    }

    /**
     * @see {android.util.Log.e}
     */
    public static void e(String tag, String msg) {
        android.util.Log.e(tag, msg);
    }

    /**
     * @see {android.util.Log.e}
     */
    public static void e(String tag, String msg, Throwable tr) {
        android.util.Log.e(tag, msg, tr);
    }

    /**
     * @see {android.util.Log.wtf}
     */
    public static void wtf(String tag, String msg) {
        android.util.Log.wtf(tag, msg);
    }

    /**
     * @see {android.util.Log.wtf}
     */
    public static void wtf(String tag, String msg, Throwable tr) {
        android.util.Log.wtf(tag, msg, tr);
    }

    /**
     * @see {android.util.Log.println}
     */
    public static void println(int priority, String tag, String msg) {
        android.util.Log.println(priority, tag, msg);
    }

    /**
     * @see {android.util.Log.isLoggable}
     */
    public static void isLoggable(String tag, int level) {
        android.util.Log.isLoggable(tag, level);
    }

    /**
     * @see {android.util.Log.getStackTraceString}
     */
    public static void getStackTraceString(Throwable tr) {
        android.util.Log.getStackTraceString(tr);
    }
}

package com.desmond.asyncmanager;

import android.os.Handler;
import android.os.Looper;

/**
 * UI Thread Utility that has a handler attached to the UI thread's looper
 */
public class UIThreadUtility {
    private static Handler mHandler;

    static {
        mHandler = new Handler(Looper.getMainLooper());
    }

    static void post(Runnable run) {
        mHandler.post(run);
    }

    static void removePost(Runnable run) {
        mHandler.removeCallbacks(run);
    }
}

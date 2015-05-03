package com.desmond.uibackgroundjobmanager;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by desmond on 3/5/15.
 */
public class UIThreadUtility {
    private static Handler mHandler;

    public static Handler getHandler() {
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        return mHandler;
    }

    public static void post(Runnable run) {
        getHandler().post(run);
    }

    public static void removePost(Runnable run) {
        getHandler().removeCallbacks(run);
    }
}

package com.desmond.uibackgroundjob;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by desmond on 6/5/15.
 */
public class DemoApplication extends Application {

    private static final String USER_INFO = "info";
    private static final String USERNAME = "username";
    private static final String EMAIL = "email";

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static void saveUserName(String userName) {
        SharedPreferences settings =
                mContext.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(USERNAME, userName);
        editor.commit();
    }

    public static String getUserName() {
        SharedPreferences settings =
                mContext.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE);

        return settings.getString(USERNAME, "");
    }

    public static void saveUserEmail(String email) {
        SharedPreferences settings =
                mContext.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(EMAIL, email);
        editor.commit();
    }

    public static String getUserEmail() {
        SharedPreferences settings =
                mContext.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE);

        return settings.getString(EMAIL, "");
    }
}

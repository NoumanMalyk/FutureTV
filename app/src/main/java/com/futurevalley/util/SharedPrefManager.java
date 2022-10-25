package com.futurevalley.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    public static final String SHARED_PREF_NAME = "my_shared_pref";
    private static SharedPrefManager mInstance;
    private final Context mCtx;


    public SharedPrefManager(Context mCtx) {
        this.mCtx = mCtx;
    }

    public static synchronized SharedPrefManager getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(mCtx);
        }
        return mInstance;
    }


    public void setLoginResult(String login) {

        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("login", login);
        editor.apply();
    }

    public String getLoginResult() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString("login", "0");
    }


}
package com.jagerlipton.bgaprofilesaver;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {

    private final SharedPreferences mSharedPreferences;
    private static final String APP_PREFERENCES = "preferences";
    private static final String APP_PREFERENCES_BAUDRATEINDEX = "baudrateindex";
    private static final String APP_PREFERENCES_BAUDRATE = "baudrate";


    public  SharedPreferencesHelper(Context context) {
        mSharedPreferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

    }

    public Integer loadBaudrateIndex() {
        return mSharedPreferences.getInt(APP_PREFERENCES_BAUDRATEINDEX, 0);
    }

    public Integer loadBaudrate (Integer index) {
        return mSharedPreferences.getInt(APP_PREFERENCES_BAUDRATE, 9600);
    }

    public void saveBaudrateIndex(Integer index) {
        mSharedPreferences.edit().putInt(APP_PREFERENCES_BAUDRATEINDEX, index).apply();
    }

    public  void saveBaudrate(Integer speed) {
        mSharedPreferences.edit().putInt(APP_PREFERENCES_BAUDRATE, speed).apply();
    }





}

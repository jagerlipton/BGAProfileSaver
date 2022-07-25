package com.jagerlipton.bgaprofileeditor.data.storage;

import android.content.Context;
import android.content.SharedPreferences;

public class Storage implements IStorage {

    private final SharedPreferences sharedPreferences;
    private static final String APP_PREFERENCES = "preferences";
    private static final String APP_PREFERENCES_BAUDRATEINDEX = "baudrateindex";

    public Storage(Context context) {
        sharedPreferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    public Integer loadBaudrateIndex() {
        return sharedPreferences.getInt(APP_PREFERENCES_BAUDRATEINDEX, 0);
    }

    @Override
    public void saveBaudrateIndex(Integer index) {
        sharedPreferences.edit().putInt(APP_PREFERENCES_BAUDRATEINDEX, index).apply();
    }

}

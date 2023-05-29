package com.manuni.sunway;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MyApplication extends Application {
    public static final String THEME_MODE_KEY = "theme_mode";
    public static final int THEME_MODE_LIGHT = 0;
    public static final int THEME_MODE_DARK = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int themeMode = sharedPreferences.getInt(THEME_MODE_KEY,THEME_MODE_LIGHT);
        setTheme(themeMode==THEME_MODE_DARK?R.style.AppTheme_Dark:R.style.AppTheme_Light);


    }
}

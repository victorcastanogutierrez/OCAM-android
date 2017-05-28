package com.ocam.settings;


import android.content.Context;

public class SettingsFactory {

    public static Settings getPreferencesSettingsImpl(Context context) {
        return new PreferencesSettingsImpl(context);
    }
}

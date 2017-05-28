package com.ocam.settings;

import android.content.Context;
import android.content.SharedPreferences;

import com.ocam.model.UserTokenDTO;
import com.ocam.util.Constants;


/**
 * Implementación de acceso a settings mediante SharedPrefferences
 */
public class PreferencesSettingsImpl implements Settings {

    private Context context;

    public PreferencesSettingsImpl(Context context) {
        this.context = context;
    }

    @Override
    public void saveUserLogin(UserTokenDTO data) {
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constants.TOKEN_KEY, data.getToken());
        editor.putString(Constants.EMAIL_KEY, data.getEmail());
        editor.putString(Constants.REFRESH_TOKEN_KEY, data.getRefreshToken());
        editor.putString(Constants.LOGIN_KEY, data.getLogin());
        editor.commit();
    }

    @Override
    public UserTokenDTO getUserLogged() {
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
        if (sharedPref.contains(Constants.TOKEN_KEY) && sharedPref.contains(Constants.EMAIL_KEY)) {
            String token = sharedPref.getString(Constants.TOKEN_KEY, null);
            String refreshToken = sharedPref.getString(Constants.REFRESH_TOKEN_KEY, null);
            String email = sharedPref.getString(Constants.EMAIL_KEY, null);
            String login = sharedPref.getString(Constants.LOGIN_KEY, null);
            return new UserTokenDTO(token, refreshToken, email, login);
        } else {
            return null;
        }
    }


    public void removeSavedCredentials() {
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.commit();
    }

    @Override
    public void setMonitorizationHiker(String login, String token) {
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if (sharedPref.contains(Constants.LOGIN_MONITORIZATION)) {
            editor.remove(Constants.LOGIN_MONITORIZATION);
        }
        if (sharedPref.contains(Constants.TOKEN_MONITORIZATION)) {
            editor.remove(Constants.TOKEN_MONITORIZATION);
        }
        editor.putString(Constants.LOGIN_MONITORIZATION, login);
        editor.putString(Constants.TOKEN_MONITORIZATION, token);
        editor.commit();
    }

    @Override
    public String getMonitorizationHiker() {
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
        return sharedPref.getString(Constants.LOGIN_MONITORIZATION, null);
    }

    @Override
    public String getMonitorizationTokenHiker() {
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
        return sharedPref.getString(Constants.TOKEN_MONITORIZATION, null);
    }

    @Override
    public Integer getMinutesConfiguration() {
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
        if (sharedPref.contains(Constants.MINUTES_CONFIGURATION)) {
            return Integer.parseInt(sharedPref.getString(Constants.MINUTES_CONFIGURATION, null));
        }
        else {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(Constants.MINUTES_CONFIGURATION, Constants.DEFAULT_REPORTS_DELAY.toString());
            editor.commit();
            return Constants.DEFAULT_REPORTS_DELAY;
        }
    }

    @Override
    public void updateMinutesConfiguration(Integer minutes) {
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constants.MINUTES_CONFIGURATION, minutes.toString());
        editor.commit();
    }
}

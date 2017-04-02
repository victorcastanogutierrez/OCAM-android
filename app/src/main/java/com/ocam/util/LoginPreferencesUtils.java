package com.ocam.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.ocam.R;
import com.ocam.login.LoginActivity;
import com.ocam.model.UserTokenDTO;

/**
 * Created by Victor on 02/04/2017.
 */

public class LoginPreferencesUtils {

    /**
     * Método que guarda los datos de logiun de un usuario en las SharedPreferences
     * @param context
     * @param data
     */
    public static void saveUserLogin(Context context, UserTokenDTO data) {
        SharedPreferences sharedPref = ((LoginActivity) context).getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constants.TOKEN_KEY, data.getToken());
        editor.putString(Constants.EMAIL_KEY, data.getEmail());
        editor.commit();
    }
}

package com.ocam.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

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
        editor.putString(Constants.REFRESH_TOKEN_KEY, data.getRefreshToken());
        editor.commit();
    }

    /**
     * Retorna el token del usuario logueado en el sistema
     * @param context
     * @return
     */
    public static UserTokenDTO getUserLogged(Context context) {
        SharedPreferences sharedPref = ((LoginActivity) context).getPreferences(Context.MODE_PRIVATE);
        if (sharedPref.contains(Constants.TOKEN_KEY) && sharedPref.contains(Constants.EMAIL_KEY)) {
            String token = sharedPref.getString(Constants.TOKEN_KEY, null);
            String refreshToken = sharedPref.getString(Constants.REFRESH_TOKEN_KEY, null);
            String email = sharedPref.getString(Constants.EMAIL_KEY, null);
            return new UserTokenDTO(token, refreshToken, email);
        } else {
            return null;
        }
    }

    /**
     * Método para eliminar las credenciales guardadas
     * @param context
     */
    public static void removeSavedCredentials(Context context) {
        SharedPreferences sharedPref = ((LoginActivity) context).getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.commit();
    }
}

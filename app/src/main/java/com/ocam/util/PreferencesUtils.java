package com.ocam.util;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.ocam.R;
import com.ocam.login.LoginActivity;
import com.ocam.model.UserTokenDTO;

import static android.R.attr.data;

/**
 * Created by Victor on 02/04/2017.
 */

public class PreferencesUtils {

    /**
     * Método que guarda los datos de logiun de un usuario en las SharedPreferences
     * @param context
     * @param data
     */
    public static void saveUserLogin(Context context, UserTokenDTO data) {
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constants.TOKEN_KEY, data.getToken());
        editor.putString(Constants.EMAIL_KEY, data.getEmail());
        editor.putString(Constants.REFRESH_TOKEN_KEY, data.getRefreshToken());
        editor.putString(Constants.LOGIN_KEY, data.getLogin());
        editor.commit();
    }

    /**
     * Retorna el token del usuario logueado en el sistema
     * @param context
     * @return
     */
    public static UserTokenDTO getUserLogged(Context context) {
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

    /**
     * Método para eliminar las credenciales guardadas
     * @param context
     */
    public static void removeSavedCredentials(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * Guarda el login y el token del usuario que está siendo monitorizado. En el caso en que se cierre la aplicación
     * y por tanto no sea posible obtenerlo de la memoria del dispositivo, se guarda este valor en
     * SharedPreferences para que persista tras cerrar la app.
     * @param context
     * @param login
     */
    public static void setMonitorizationHiker(Context context, String login, String token) {
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

    /**
     * Retorna el nombre de usuario del último login en la aplicación.
     * Para aquellos casos en los que no sea posible obtenerlo del manager correspondiente
     * por haberse cerrado la app.
     * @param context
     * @return
     */
    public static String getMonitorizationHiker(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
        return sharedPref.getString(Constants.LOGIN_MONITORIZATION, null);
    }

    /**
     * Retorna el token de usuario del último login en la aplicación.
     * Para aquellos casos en los que no sea posible obtenerlo del manager correspondiente
     * por haberse cerrado la app.
     * @param context
     * @return
     */
    public static String getMonitorizationTokenHiker(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
        return sharedPref.getString(Constants.TOKEN_MONITORIZATION, null);
    }
}

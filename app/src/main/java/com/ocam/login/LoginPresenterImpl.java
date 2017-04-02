package com.ocam.login;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.ocam.manager.VolleyManager;
import com.ocam.model.UserTokenDTO;
import com.ocam.util.LoginPreferencesUtils;
import com.ocam.util.volley.GsonRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Victor on 01/04/2017.
 */
public class LoginPresenterImpl implements LoginPresenter {

    private LoginView loginView;
    private Boolean recuerdaDatos;
    private Context context;

    public LoginPresenterImpl(LoginView loginView, Context context) {
        this.loginView = loginView;
        this.context = context;
        this.recuerdaDatos = Boolean.FALSE;
    }

    @Override
    public void login(String username, String password, Boolean recuerda) {
        this.recuerdaDatos = recuerda;
        VolleyManager volleyManager = VolleyManager.getInstance(this.context);
        GsonRequest<UserTokenDTO> request = new GsonRequest<UserTokenDTO>("/api/auth/login",
                Method.POST, UserTokenDTO.class, getLoginHeaders(username, password),
                new myListener(), new myErrorListener());

        volleyManager.addToRequestQueue(request);
    }

    private class myListener implements Listener<UserTokenDTO>
    {
        @Override
        public void onResponse(UserTokenDTO response) {
            if (response == null) {
                loginView.showErrorMessage("Error inesperado. Prueba más tarde");
            } else {
                if (Boolean.TRUE.equals(recuerdaDatos)) {
                    LoginPreferencesUtils.saveUserLogin(context, response);
                }
                loginView.loginSuccess();
            }
            loginView.hideProgress();
        }
    }

    private class myErrorListener implements ErrorListener
    {
        @Override
        public void onErrorResponse(VolleyError error) {
            String errorMsg = null;
            if (error != null) {
                errorMsg = "Credenciales inválidas.";
                Log.d("Error", error.getMessage());
            } else {
                errorMsg = "Error inesperado. Prueba más tarde.";
            }
            loginView.showErrorMessage(errorMsg);
            loginView.hideProgress();
        }
    }

    private Map<String, String> getLoginHeaders(String email, String password) {
        Map<String, String> result = new HashMap<String, String>();
        result.put("username", Base64.encodeToString(email.getBytes(), Base64.DEFAULT));
        result.put("password", Base64.encodeToString(password.getBytes(), Base64.DEFAULT));
        return result;
    }
}

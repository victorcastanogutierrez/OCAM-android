package com.ocam.login;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.VolleyError;
import com.ocam.manager.UserManager;
import com.ocam.manager.VolleyManager;
import com.ocam.model.UserTokenDTO;
import com.ocam.util.Constants;
import com.ocam.util.LoginPreferencesUtils;
import com.ocam.volley.GsonRequest;
import com.ocam.volley.listeners.GenericErrorListener;
import com.ocam.volley.listeners.GenericResponseListener;
import com.ocam.volley.listeners.ICommand;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Victor on 01/04/2017.
 */
public class LoginPresenterImpl implements LoginPresenter {

    private LoginView loginView;
    private Boolean recuerdaDatos;
    private Context context;
    private VolleyManager volleyManager;
    private String username;

    public LoginPresenterImpl(LoginView loginView, Context context) {
        this.loginView = loginView;
        this.context = context;
        this.recuerdaDatos = Boolean.FALSE;
        this.volleyManager = VolleyManager.getInstance(context);
    }

    /**
     * Loguea un usuario en la aplicación
     * @param username
     * @param password
     * @param recuerda
     */
    @Override
    public void login(String username, String password, Boolean recuerda) {
        this.recuerdaDatos = recuerda;
        this.username = username;

        ICommand<UserTokenDTO> myCommand = new MyCommand();

        GsonRequest<UserTokenDTO> request = new GsonRequest<UserTokenDTO>(Constants.API_AUTH_LOGIN,
                Method.POST, UserTokenDTO.class, getLoginHeaders(username, password),
                new GenericResponseListener<>(myCommand), new GenericErrorListener<>(myCommand));

        volleyManager.addToRequestQueue(request);
    }

    /**
     * Comprueba si un usuario está ya logueado
     * En caso de estarlo renueva el token
     */
    @Override
    public void checkUserLogged() {
        UserTokenDTO userTokenDTO = LoginPreferencesUtils.getUserLogged(this.context);
        if (userTokenDTO == null) {
            loginView.hideProgress();
        } else {
            ICommand<UserTokenDTO> myCommand = new MyCommand();
            Map<String, String> headers = getAuthHeader(userTokenDTO);
            GsonRequest<UserTokenDTO> request = new GsonRequest<UserTokenDTO>(Constants.API_TOKEN,
                    Method.GET, UserTokenDTO.class, headers,
                    new GenericResponseListener<>(myCommand), new GenericErrorListener(myCommand));

            volleyManager.addToRequestQueue(request);
        }
    }

    /**
     * Retorna la cabecera de la petición necesaria para refrescar el token
     * @param userTokenDTO
     * @return
     */
    private Map<String, String> getAuthHeader(UserTokenDTO userTokenDTO) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(Constants.HEADER_AUTH_NAME, userTokenDTO.getRefreshToken());
        return headers;
    }

    private Map<String, String> getLoginHeaders(String email, String password) {
        Map<String, String> result = new HashMap<String, String>();
        result.put("username", Base64.encodeToString(email.getBytes(), Base64.DEFAULT));
        result.put("password", Base64.encodeToString(password.getBytes(), Base64.DEFAULT));
        return result;
    }

    /**
     * Clase para manejar las respuestas a la petición HTTP de login
     */
    private class MyCommand implements ICommand<UserTokenDTO> {

        @Override
        public void executeResponse(UserTokenDTO response) {
            if (response == null) {
                loginView.showErrorMessage("Error inesperado. Prueba más tarde");
            } else {
                if (Boolean.TRUE.equals(recuerdaDatos)) {
                    LoginPreferencesUtils.saveUserLogin(context, response);
                }
                //En caso que venga de introducir los datos
                //si no la propia response ya contiene el username
                if (username != null) {
                    response.setLogin(username);
                }
                UserManager userManager = UserManager.getInstance();
                Log.d("Loguea:", response.toString());
                userManager.setUserTokenDTO(response);
                loginView.loginSuccess(response);
            }
            loginView.hideProgress();
        }

        @Override
        public void executeError(VolleyError error) {
            String errorMsg = "Error inesperado. Prueba más tarde.";
            if (error != null && error.getMessage() != null) {
                Log.d("Error login:", ""+error.getMessage());
                if (error.getMessage().contains(Constants.UNAUTHORIZED_ERROR_CODE)) {
                    errorMsg = "Credenciales inválidas.";
                    //Si las credenciales guardadas son inválidas, las eliminamos
                    LoginPreferencesUtils.removeSavedCredentials(context);
                }
            }

            loginView.showErrorMessage(errorMsg);
            loginView.hideProgress();
        }
    }
}

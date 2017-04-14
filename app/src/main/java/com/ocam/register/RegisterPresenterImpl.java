package com.ocam.register;


import android.content.Context;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ocam.manager.VolleyManager;
import com.ocam.model.HikerDTO;
import com.ocam.util.Constants;
import com.ocam.volley.GsonRequest;
import com.ocam.volley.listeners.GenericErrorListener;
import com.ocam.volley.listeners.GenericResponseListener;
import com.ocam.volley.listeners.ICommand;

public class RegisterPresenterImpl implements RegisterPresenter {

    private Context context;
    private RegisterView registerView;
    private RegisterListener registerListener;

    public RegisterPresenterImpl(Context context, RegisterView registerView, RegisterListener registerListener) {
        this.context = context;
        this.registerView = registerView;
        this.registerListener =  registerListener;
    }

    @Override
    public void register(String login, String email, String password) {
        this.registerView.showProgress();
        ICommand<Void> myCommand = new RegisterCommand();
        GsonRequest<Void> request = new GsonRequest<Void>(
                Constants.API_REGISTER_HIKER,
                Request.Method.POST, Void.class, null, getBody(email, login, password),
                new GenericResponseListener<>(myCommand), new GenericErrorListener(myCommand));

        VolleyManager.getInstance(this.context).addToRequestQueue(request);
    }

    /**
     * Método que construye un String (JSON) con la información del hiker para la realización de la
     * POST a la API
     * @return
     */
    private String getBody(String email, String login, String password) {
        HikerDTO hikerDTO = new HikerDTO();
        hikerDTO.setEmail(email);
        hikerDTO.setUsername(login);
        hikerDTO.setPassword(password);
        return new Gson().toJson(hikerDTO).toString();
    }

    /**
     * Clase para manejar las respuestas a la petición HTTP de registro
     */
    private class RegisterCommand implements ICommand<Void> {

        @Override
        public void executeResponse(Void response) {
            registerView.hideProgress();
            registerListener.onRegister();
        }

        @Override
        public void executeError(VolleyError error) {
            registerView.hideProgress();
            JsonObject objError = new Gson().fromJson(error.getMessage(), JsonObject.class);
            registerView.notify(objError.get("message").getAsString());
        }
    }
}

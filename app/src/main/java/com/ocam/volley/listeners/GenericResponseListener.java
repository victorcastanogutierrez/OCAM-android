package com.ocam.volley.listeners;


import com.android.volley.Response;

/**
 * Clase genérica para la captura de respuestas 200 HTTP
 */
public class GenericResponseListener<T> implements Response.Listener<T> {

    private ICommand<T> command;

    public GenericResponseListener(ICommand<T> command) {
        this.command = command;
    }

    @Override
    public void onResponse(T response) {
        command.executeResponse(response);
    }
}

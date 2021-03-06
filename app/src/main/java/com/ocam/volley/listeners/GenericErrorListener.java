package com.ocam.volley.listeners;


import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ocam.util.Constants;

/**
 * Clase genérica para la captura de respuestas 400 HTTP
 */
public class GenericErrorListener<T> implements Response.ErrorListener {

    private ICommand<T> command;

    public GenericErrorListener(ICommand<T> command) {
        this.command = command;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        command.executeError(error);
    }
}

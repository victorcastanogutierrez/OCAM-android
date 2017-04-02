package com.ocam.volley.listeners;


import com.android.volley.VolleyError;

/**
 * Interfaz de comandos para ejecución en listeners HTTP
 * @param <T>
 */
public interface ICommand<T> {

    void executeResponse(T response);

    void executeError(VolleyError error);
}

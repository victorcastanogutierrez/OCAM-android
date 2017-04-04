package com.ocam.volley;


import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.ocam.manager.UserManager;
import com.ocam.util.Constants;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Clase genérica para realizar peticiones HTTP y parsear la respuesta en JSON a un objeto del
 * dominio. Obtenida de la documentación oficial de Google para Volley y adaptada al dominio de
 * la aplicación
 */
public class GsonRequest<T> extends Request<T> {
    private final Gson gson;
    private final Class<T> clazz;
    private final Map<String, String> headers;
    private final Listener<T> listener;

    /**
     * Make a GET request and return a parsed object from JSON.
     *
     * @param url URL of the request to make
     * @param clazz Relevant class object, for Gson's reflection
     * @param headers Map of request headers
     */
    public GsonRequest(String url, int method, Class<T> clazz, Map<String, String> headers,
                       Listener<T> listener, ErrorListener errorListener) {
        super(method, Constants.SERVER_URL + url, errorListener);
        this.clazz = clazz;
        this.headers = headers;
        this.listener = listener;
        this.gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
    }

    /**
     * Método que envía en todas las peticiones la cabecera con el token
     * @return
     * @throws AuthFailureError
     */
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> sendHeaders;
        if (headers != null) {
            sendHeaders = this.headers;
        } else {
            sendHeaders = super.getHeaders();
        }

        UserManager userManager = UserManager.getInstance();
        if (userManager.getUserTokenDTO() != null) {
            sendHeaders.put(Constants.HEADER_AUTH_NAME, userManager.getUserTokenDTO().getToken());
        }
        return sendHeaders;
    }

    @Override
    //This will make the volley error message to contain your server's error message
    protected VolleyError parseNetworkError(VolleyError volleyError) {
        if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
            volleyError = new VolleyError(new String(volleyError.networkResponse.data));
        }
        return volleyError;
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(
                    response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            return Response.success(
                    gson.fromJson(json, clazz),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }
}

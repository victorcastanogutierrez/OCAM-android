package com.ocam.volley;


import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.ocam.manager.UserManager;
import com.ocam.util.Constants;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
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
    private String JSONbody;
    private final Listener<T> listener;

    /**
     * Hace una petición a la URL, como método y clase para la respuesta indiciada por parámtro
     *
     * @param url
     * @param method
     * @param clazz
     * @param headers
     * @param listener
     * @param errorListener
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
     * Constructor añadiendo el parámetro JSONbody para peticiones POST
     * @param url
     * @param method
     * @param clazz
     * @param headers
     * @param JSONbody
     * @param listener
     * @param errorListener
     */
    public GsonRequest(String url, int method, Class<T> clazz, Map<String, String> headers,
                       String JSONbody, Listener<T> listener, ErrorListener errorListener) {
        this(url, method, clazz, headers, listener, errorListener);
        this.JSONbody = JSONbody;
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
            sendHeaders = new HashMap<String, String>();
        }

        UserManager userManager = UserManager.getInstance();
        if (userManager.getUserTokenDTO() != null) {
            if (super.getUrl().startsWith(Constants.SERVER_URL + "/api/")) {
                sendHeaders.put(Constants.HEADER_AUTH_NAME, userManager.getUserTokenDTO().getToken());
            }
        }
        return sendHeaders;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        try {
            return JSONbody == null ?
                    super.getBody() :
                    JSONbody.getBytes("utf-8");
        } catch (UnsupportedEncodingException uee) {
            VolleyLog.wtf("Error convirtiendo el body a UTF-8");
            return null;
        }
    }

    @Override
    public String getBodyContentType() {
        return "application/json; charset=utf-8";
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

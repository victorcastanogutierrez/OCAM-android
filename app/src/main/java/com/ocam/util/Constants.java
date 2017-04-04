package com.ocam.util;

/**
 * Constantes de la aplicación
 */
public class Constants {

    // API urls
    //public static final String SERVER_URL = "https://192.168.0.13:8443";
    public static final String SERVER_URL = "https://ocamserver.herokuapp.com";
    public static final String API_TOKEN = "/auth/token";
    public static final String API_AUTH_LOGIN = "/api/auth/login";
    public static final String API_FIN_HIKER_BY_EMAIL = "/api/existsMail";

    //Header auth
    public static final String HEADER_AUTH_NAME = "authorization";

    //Error codes
    public static final String UNAUTHORIZED_ERROR_CODE =  "401";


    //Shared preferences
    public static final String TOKEN_KEY = "USER-TOKEN";
    public static final String EMAIL_KEY = "USER-EMAIL";
    public static final String REFRESH_TOKEN_KEY = "REFRESH-TOKEN";
}

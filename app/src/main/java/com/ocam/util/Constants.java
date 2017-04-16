package com.ocam.util;

/**
 * Constantes de la aplicación
 */
public class Constants {

    // API urls
    public static final String SERVER_URL = "https://192.168.0.13:8443";
    //public static final String SERVER_URL = "https://ocamserver.herokuapp.com";
    public static final String API_TOKEN = "/auth/token";
    public static final String API_AUTH_LOGIN = "/api/auth/login";
    public static final String API_FIND_ALL_ACTIVITIES = "/api/findAllPendingRunningActivities";
    public static final String API_START_ACTIVITY = "/api/startActivity";
    public static final String API_FIND_ACTIVITY = "/api/activity";
    public static final String API_UPDATE_PASSWORD_ACTIVITY = "/api/updateActivityPassword";
    public static final String API_UNIRSE_ACTIVIDAD = "/api/joinActivity";
    public static final String API_FIND_ACTIVITY_REPORTS = "/api/lastActivityReports";
    public static final String API_REGISTER_HIKER = "/hiker";
    public static final String API_CLOSE_ACTIVITY = "/api/closeActivity";
    public static final String API_SAVE_REPORT = "/api/report/save";
    public static final String API_LEAVE_ACTIVITY = "/api/activity/leaveActivity";

    //Header auth
    public static final String HEADER_AUTH_NAME = "authorization";
    public static final String HEADER_REFRESH_NAME = "authorizationrefresh";
    public static final String HEADER_EMAIL_AUTHT = "emailauth";

    //Error codes
    public static final String UNAUTHORIZED_ERROR_CODE =  "401";

    //Shared preferences
    public static final String SHARED_PREFS = "sharedocam";
    public static final String TOKEN_KEY = "USER-TOKEN";
    public static final String EMAIL_KEY = "USER-EMAIL";
    public static final String REFRESH_TOKEN_KEY = "REFRESH-TOKEN";
    public static final String LOGIN_MONITORIZATION = "LOGIN-MONITORIZATION";
    public static final String TOKEN_MONITORIZATION = "TOKEN-MONITORIZATION";

    //Errors
    public static final String ERROR_LOADING_ACTIVITIES = "Error cargando las actividades";
    public static final String HTTP_422 = "UNPROCESSABLE_ENTITY";

    //Notifications
    public static final Integer ONGOING_NOTIFICATION_ID = 687326891;
    public static final Integer BROADCAST_INTENT = 234324243;

    public static final long REPORTS_PERIODICITY = 60 * 1000;
}

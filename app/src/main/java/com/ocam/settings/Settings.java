package com.ocam.settings;


import android.content.Context;

import com.ocam.model.UserTokenDTO;

/**
 * Métodos de acceso y update a settings de la aplicación
 */
public interface Settings {

    /**
     * Método que guarda los datos de logiun de un usuario en las SharedPreferences
     * @param context
     * @param data
     */
    void saveUserLogin(UserTokenDTO data);

    /**
     * Retorna el token del usuario logueado en el sistema
     * @param context
     * @return
     */
    UserTokenDTO getUserLogged();

    /**
     * Método para eliminar las credenciales guardadas
     * @param context
     */
    void removeSavedCredentials();

    /**
     * Guarda el login y el token del usuario que está siendo monitorizado. En el caso en que se cierre la aplicación
     * y por tanto no sea posible obtenerlo de la memoria del dispositivo, se guarda este valor en
     * SharedPreferences para que persista tras cerrar la app.
     * @param context
     * @param login
     */
    void setMonitorizationHiker(String login, String token);

    /**
     * Retorna el nombre de usuario del último login en la aplicación.
     * Para aquellos casos en los que no sea posible obtenerlo del manager correspondiente
     * por haberse cerrado la app.
     * @param context
     * @return
     */
    String getMonitorizationHiker();

    /**
     * Retorna el token de usuario del último login en la aplicación.
     * Para aquellos casos en los que no sea posible obtenerlo del manager correspondiente
     * por haberse cerrado la app.
     * @param context
     * @return
     */
    String getMonitorizationTokenHiker();

    /**
     * Obtiene los minutos entre reporte y reporte. En caso de no tener esta configuración guardada
     * se establece al valor predeterminado
     * @param context
     * @return
     */
    Integer getMinutesConfiguration();

    /**
     * Actualiza los minutos entre reporte y reporte
     * @param context
     * @param minutes
     */
    void updateMinutesConfiguration(Integer minutes);
}

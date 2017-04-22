package com.ocam.activity;


public interface ActivityView {

    /**
     * Muestra una notificación por pantalla al usuario
     * @param notificationText contenido del error
     */
    void notifyUser(String notificationText);

    /**
     * Muestra una barra de progreso con un layout superpuesto a la vista
     * para evitar la interacción del usuario
     */
    void displayProgress();

    /**
     * Oculta la barra de progreso y el dialog superpuesto
     */
    void hideProgress();

    /**
     * Actualiza los datos de la actividad y actualiza los botones de comenzar y monitorizar
     */
    void onActivityOpen();

    /**
     * Actualiza los datos de la actividad y la vista al unirse
     */
    void onHikerJoinActivity();

    /**
     * Ejecutado al cerrarse la actividad
     */
    void onActivityClosed();

    /**
     * Comienza el broadcast que se ejecuta para enviar los reportes al servidor
     * y envía una notificación al usuario
     */
    void iniciarMonitorizacion();

    /**
     * Ejecutado al dejar con éxito una actividad
     */
    void onLeaveActivity();

    /**
     * Abre un dialog informativo al usuario
     */
    void notifyUserDialog(String text);
}

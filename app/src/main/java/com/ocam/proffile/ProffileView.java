package com.ocam.proffile;


public interface ProffileView {

    /**
     * Muestra una notificación al usuario con el contenido especificado por parámetro
     * @param text
     */
    void notifyUser(String text);

    /**
     * Notifica al usuario mediante Snackbar
     * @param text
     */
    void notifyUserSnackbar(String text);

    /**
     * Muestra barra de progreso
     */
    void hideProgress();
}

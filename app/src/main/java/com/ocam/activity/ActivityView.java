package com.ocam.activity;


public interface ActivityView {

    /**
     * Muestra un error en pantalla mediante Toast
     * @param error contenido del error
     */
    void showError(String error);

    /**
     * Muestra una barra de progreso con un layout superpuesto a la vista
     * para evitar la interacción del usuario
     */
    void displayProgress();

    /**
     * Oculta la barra de progreso y el dialog superpuesto
     */
    void hideProgress();
}

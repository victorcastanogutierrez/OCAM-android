package com.ocam.register;


public interface RegisterView {

    /**
     * Muestra la progressBar y previene de interacción
     * por parte del usuario con la visat
     */
    void showProgress();

    /**
     * Oculta la progressBar
     */
    void hideProgress();

    /**
     * Notifica al usuario
     */
    void notify(String text);
}

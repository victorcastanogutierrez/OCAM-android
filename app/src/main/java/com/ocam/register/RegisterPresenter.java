package com.ocam.register;


public interface RegisterPresenter {

    /**
     * Registra al usuario con los datos aportados por parametro
     * @param login
     * @param email
     * @param password
     */
    void register(String login, String email, String password);
}

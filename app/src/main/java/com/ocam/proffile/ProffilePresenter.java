package com.ocam.proffile;


public interface ProffilePresenter {

    /**
     * Cambia la password al usuario verificando que la actual coincida
     * @param actual
     * @param newPassword
     * @param reNewPassword
     */
    void changePassword(String actual, String newPassword, String reNewPassword);
}

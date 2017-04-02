package com.ocam.login;

/**
 * Created by Victor on 01/04/2017.
 */
public class LoginPresenterImpl implements LoginPresenter {

    private LoginView loginView;

    public LoginPresenterImpl(LoginView loginView) {
        this.loginView = loginView;
    }

    @Override
    public Boolean login(String email, String password) {
        return null;
    }
}

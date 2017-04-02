package com.ocam.login;

import android.util.Log;
import android.view.View;

import com.ocam.model.UserDTO;

/**
 * Created by Victor on 01/04/2017.
 */
public class LoginPresenterImpl implements LoginPresenter {

    private LoginView loginView;

    public LoginPresenterImpl(LoginView loginView) {
        this.loginView = loginView;
    }


    @Override
    public void login(String email, String password) {
    }
}

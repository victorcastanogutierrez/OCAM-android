package com.ocam.login;


import android.content.Context;

/**
 * Created by Victor on 01/04/2017.
 */
public interface LoginPresenter {

    void login(String username, String password,  Boolean recuerda);

}

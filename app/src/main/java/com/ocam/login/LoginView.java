package com.ocam.login;

import com.ocam.model.UserTokenDTO;

/**
 * Created by Victor on 01/04/2017.
 */
public interface LoginView {

    void hideProgress();

    void showErrorMessage(String message);

    void loginSuccess(UserTokenDTO userTokenDTO);
}

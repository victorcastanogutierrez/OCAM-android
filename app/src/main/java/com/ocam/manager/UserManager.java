package com.ocam.manager;

import com.ocam.model.UserTokenDTO;

/**
 * Manager que implementa el patrón singleton para guardar los datos del usuario que está
 * en sesión en la aplicación
 */
public class UserManager {

    private static UserManager instance;
    private UserTokenDTO userTokenDTO;

    private UserManager() {

    }

    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public UserTokenDTO getUserTokenDTO() {
        return userTokenDTO;
    }

    public void setUserTokenDTO(UserTokenDTO userTokenDTO) {
        this.userTokenDTO = userTokenDTO;
    }
}

package com.ocam.volley.listeners;

import com.android.volley.VolleyError;
import com.ocam.manager.UserManager;
import com.ocam.model.UserTokenDTO;
import com.ocam.util.Constants;
import com.ocam.util.LoginPreferencesUtils;

/**
 * Created by Victor on 03/04/2017.
 */

public class RenewTokenCommand implements ICommand<UserTokenDTO>{

    private ICommand command;
    

    @Override
    public void executeResponse(UserTokenDTO response) {
        
    }

    @Override
    public void executeError(VolleyError error) {
        
    }
    
}

package com.ocam.configuracion;


import android.content.Context;

import com.ocam.util.PreferencesUtils;

public class ConfiguracionPresenterImpl implements ConfiguracionPresenter {

    private ConfiguracionView configuracionView;
    private Context context;

    public ConfiguracionPresenterImpl(Context context, ConfiguracionView configuracionView) {
        this.configuracionView = configuracionView;
        this.context = context;
    }

    @Override
    public void updateMinutes(Integer minutes) {
        if(!assertValidNumber(minutes)) {
            this.configuracionView.notifyUser("Debes introducir un número entre 1 y 360 minutos");
        } else {
            PreferencesUtils.updateMinutesConfiguration(this.context, minutes);
            this.configuracionView.notifyUser("Configuración actualizada");
        }
    }

    private Boolean assertValidNumber(Integer minutes) {
        return minutes >= 1 && minutes <= 360;
    }
}

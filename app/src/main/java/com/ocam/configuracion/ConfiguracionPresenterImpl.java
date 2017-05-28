package com.ocam.configuracion;


import android.content.Context;

import com.ocam.settings.PreferencesSettingsImpl;
import com.ocam.settings.Settings;
import com.ocam.settings.SettingsFactory;

public class ConfiguracionPresenterImpl implements ConfiguracionPresenter {

    private ConfiguracionView configuracionView;
    private Context context;
    private Settings settings;

    public ConfiguracionPresenterImpl(Context context, ConfiguracionView configuracionView) {
        this.configuracionView = configuracionView;
        this.context = context;
        this.settings = SettingsFactory.getPreferencesSettingsImpl(context);
    }

    @Override
    public void updateMinutes(Integer minutes) {
        if(!assertValidNumber(minutes)) {
            this.configuracionView.notifyUser("Debes introducir un número entre 1 y 360 minutos");
        } else {
            settings.updateMinutesConfiguration(minutes);
            this.configuracionView.notifyUser("Configuración actualizada");
        }
    }

    private Boolean assertValidNumber(Integer minutes) {
        return minutes >= 1 && minutes <= 360;
    }
}

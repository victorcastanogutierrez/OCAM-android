package com.ocam.configuracion;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.ocam.R;
import com.ocam.settings.PreferencesSettingsImpl;
import com.ocam.settings.Settings;
import com.ocam.settings.SettingsFactory;
import com.ocam.util.ViewUtils;


public class ConfiguracionFragment extends Fragment implements ConfiguracionView {

    private TextInputLayout txInputMinutos;
    private Button btConfirmar;
    private ConfiguracionPresenter configuracionPresenter;
    private Settings settings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.content_configuracion, container, false);
        this.txInputMinutos = (TextInputLayout) view.findViewById(R.id.idMinutos);
        this.btConfirmar = (Button) view.findViewById(R.id.btConfirmar);
        this.configuracionPresenter = new ConfiguracionPresenterImpl(getContext(), this);
        this.settings = SettingsFactory.getPreferencesSettingsImpl(getContext());
        setUpConfirmarBt();
        initTextView();
        setHasOptionsMenu(true);
        return view;
    }

    /**
     * Configura el método onClick para el botón de confirmación de cambios
     */
    private void setUpConfirmarBt() {
        btConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    int value = Integer.parseInt(txInputMinutos.getEditText().getText().toString());
                    saveChanges(value);
                    closeKeyBoard();
                }
                catch(NumberFormatException nfe)
                {
                    ViewUtils.showToast(getContext(), Toast.LENGTH_LONG, "Debes introducir un número");
                }
            }
        });
    }

    /**
     * Inicializa el campo con la configuración actual de minutos
     */
    private void initTextView() {
        this.txInputMinutos.getEditText().setText(settings.getMinutesConfiguration().toString());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Guarda la nueva configuración
     * @param newValue
     */
    private void saveChanges(Integer newValue) {
        this.configuracionPresenter.updateMinutes(newValue);
    }

    @Override
    public void notifyUser(String text) {
        ViewUtils.showToast(getContext(), Toast.LENGTH_LONG, text);
    }

    private void closeKeyBoard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}

package com.ocam.proffile;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.ocam.R;
import com.ocam.login.LoginActivity;
import com.ocam.manager.UserManager;
import com.ocam.model.UserTokenDTO;
import com.ocam.util.Constants;
import com.ocam.util.PreferencesUtils;
import com.ocam.util.ViewUtils;
import com.ocam.volley.listeners.ICommand;

public class FragmentProffile extends Fragment implements ProffileView {

    private TextInputLayout txInputEmail;
    private TextInputLayout txInputLogin;
    private TextInputLayout txInputPassword;
    private TextInputLayout txInputNewPassword;
    private TextInputLayout txInputReNewPassword;
    private Button btActualizar;
    private ProffilePresenter proffilePresenter;
    private ProgressBar mProgress;
    private Dialog mOverlayDialog;

    public FragmentProffile() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_proffile, container, false);
        this.proffilePresenter = new ProffilePresenterImpl(view.getContext(), this);
        this.txInputEmail = (TextInputLayout) view.findViewById(R.id.idEmail);
        this.txInputLogin = (TextInputLayout) view.findViewById(R.id.idUsername);
        this.txInputPassword = (TextInputLayout) view.findViewById(R.id.idPassword);
        this.txInputNewPassword = (TextInputLayout) view.findViewById(R.id.idNewPassword);
        this.txInputReNewPassword = (TextInputLayout) view.findViewById(R.id.idReNewPassword);
        this.btActualizar = (Button) view.findViewById(R.id.btActualizar);
        this.mProgress = (ProgressBar) view.findViewById(R.id.progressBar);
        this.mOverlayDialog = new Dialog(view.getContext(), android.R.style.Theme_Panel);
        setUpEmailUser();
        setUpActualizarListener();
        setHasOptionsMenu(true);
        return view;
    }

    /**
     * Deshabilitamos menús que no tienen sentido en esta vista
     * @param menu
     */
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.filterButton).setVisible(false);
        menu.findItem(R.id.refreshButton).setVisible(false);
    }

    /**
     * Muestra en pantalla los datos del hiker: login y email
     */
    private void setUpEmailUser()  {
        UserTokenDTO userTokenDTO = UserManager.getInstance().getUserTokenDTO();
        txInputEmail.getEditText().setText(userTokenDTO.getEmail());
        txInputLogin.getEditText().setText(userTokenDTO.getLogin());
    }

    /**
     * Listener del botón de actualizar password
     */
    private void setUpActualizarListener() {
        this.btActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayProgress();
                proffilePresenter.changePassword(txInputPassword.getEditText().getText().toString(),
                        txInputNewPassword.getEditText().getText().toString(),
                        txInputReNewPassword.getEditText().getText().toString());
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyUser(String text) {
        ViewUtils.showToast(getView().getContext(), Toast.LENGTH_LONG, text);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyUserSnackbar(String text) {
        Snackbar.make(getView(), text, Snackbar.LENGTH_LONG)
            .setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            })
            .setActionTextColor(getResources().getColor(android.R.color.holo_green_light ))
            .show();
    }

    /**
     * Muestra un dialog para prevenir al usuario de interactuar con la vista mientras carga.
     * Muestra la progressbar
     */
    private void displayProgress() {
        mOverlayDialog.setCancelable(false);
        mOverlayDialog.show();
        this.mProgress.setVisibility(View.VISIBLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void hideProgress() {
        mOverlayDialog.cancel();
        this.mProgress.setVisibility(View.INVISIBLE);
    }
}

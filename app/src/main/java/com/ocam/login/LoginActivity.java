package com.ocam.login;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ocam.R;
import com.ocam.model.UserTokenDTO;
import com.ocam.volley.NukeSSLCerts;
import com.ocam.util.ViewUtils;

public class LoginActivity extends Activity implements LoginView {

    private LoginPresenter loginPresenter;

    private TextInputLayout ilUsername;
    private TextInputLayout ilPassword;
    private ProgressBar mProgress;
    private Dialog mOverlayDialog;
    private CheckBox cbRecuerda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Elimina el titulo
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Confiar en todos los certificados: solo para desarrollo
        new NukeSSLCerts().nuke();
        this.loginPresenter = new LoginPresenterImpl(this, LoginActivity.this);

        setContentView(R.layout.activity_login);

        this.ilUsername = (TextInputLayout) findViewById(R.id.ilUsername);
        this.ilPassword = (TextInputLayout) findViewById(R.id.ilPassword);
        this.mProgress = (ProgressBar) findViewById(R.id.progressBar);
        this.cbRecuerda = (CheckBox) findViewById(R.id.cbRecuerda);
        this.mOverlayDialog = new Dialog(LoginActivity.this, android.R.style.Theme_Panel);

        this.loginPresenter.checkUserLogged();
        displayProgress();
    }

    /**
     * Método ejecutado cuando se pulsa el boton de login
     * Realiza las comprobaciones oportunas y llama al presentador para ejecutar la lógica de negocio
     * @param view
     */
    public void login(View view) {
        String username = ilUsername.getEditText().getText().toString();
        String password = ilPassword.getEditText().getText().toString();
        if (assertEmailPassword(username, password)) {
            ViewUtils.showToast(getApplicationContext(), Toast.LENGTH_SHORT, "Email y password requeridos");
        } else {
            this.displayProgress();
            this.loginPresenter.login(username, password, cbRecuerda.isChecked());
        }
    }

    private Boolean assertEmailPassword(String email, String password) {
        return email.isEmpty() || password.isEmpty();
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


    @Override
    public void hideProgress() {
        mOverlayDialog.cancel();
        this.mProgress.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showErrorMessage(String message) {
        ViewUtils.showToast(getApplicationContext(), Toast.LENGTH_SHORT, message);
    }

    /**
     * Método llamado cuando el login finaliza con éxito
     */
    @Override
    public void loginSuccess(UserTokenDTO userTokenDTO) {
        Log.d("Loguea", "Loguea");
    }
}

package com.ocam.login;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.ocam.R;
import com.ocam.activityList.ListActivity;
import com.ocam.manager.UserManager;
import com.ocam.model.UserTokenDTO;
import com.ocam.register.RegisterActivity;
import com.ocam.settings.Settings;
import com.ocam.settings.SettingsFactory;
import com.ocam.util.ConnectionUtils;
import com.ocam.settings.PreferencesSettingsImpl;
import com.ocam.util.ViewUtils;
import com.ocam.volley.NukeSSLCerts;
import com.squareup.picasso.Picasso;

public class LoginActivity extends Activity implements LoginView {

    private LoginPresenter loginPresenter;

    private TextInputLayout ilUsername;
    private TextInputLayout ilPassword;
    private ProgressBar mProgress;
    private Dialog mOverlayDialog;
    private CheckBox cbRecuerda;
    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new NukeSSLCerts().nuke();

        //Elimina el titulo
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);
        Picasso.with(LoginActivity.this)
                .load(R.drawable.testing1)
                .fit()
                .centerInside()
                .into((ImageView) findViewById(R.id.ivLogo));


        //Confiar en todos los certificados: solo para desarrollo
        this.loginPresenter = new LoginPresenterImpl(this, LoginActivity.this);

        this.ilUsername = (TextInputLayout) findViewById(R.id.ilUsername);
        this.ilPassword = (TextInputLayout) findViewById(R.id.ilPassword);
        this.mProgress = (ProgressBar) findViewById(R.id.progressBar);
        this.cbRecuerda = (CheckBox) findViewById(R.id.cbRecuerda);
        this.mOverlayDialog = new Dialog(LoginActivity.this, android.R.style.Theme_Panel);
        this.settings = SettingsFactory.getPreferencesSettingsImpl(LoginActivity.this);

        Boolean cierraSesion = getIntent().getBooleanExtra("CIERRA_SESION", Boolean.FALSE);
        if (Boolean.FALSE.equals(cierraSesion)) {
            displayProgress();
            this.loginPresenter.checkUserLogged();
        } else {
            settings.removeSavedCredentials();
        }
    }

    /**
     * Método ejecutado cuando se pulsa el boton de login
     * Realiza las comprobaciones oportunas y llama al presentador para ejecutar la lógica de negocio
     * @param view
     */
    public void login(View view) {
        if (ConnectionUtils.isConnected(LoginActivity.this)) {
            String username = ilUsername.getEditText().getText().toString();
            String password = ilPassword.getEditText().getText().toString();
            if (assertEmailPassword(username, password)) {
                ViewUtils.showToast(getApplicationContext(), Toast.LENGTH_SHORT, "Email y password requeridos");
            } else {
                this.displayProgress();
                this.loginPresenter.login(username, password, cbRecuerda.isChecked());
            }
        } else {
            ViewUtils.showToast(LoginActivity.this, Toast.LENGTH_LONG, "No tienes conexión a internet");
        }
    }

    /**
     * Método ejecutado cuando se pulsa el botón de registro
     * @param view
     */
    public void register(View view) {
        Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(i);
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
    public void loginSuccess() {
        logUser();
        Intent i = new Intent(LoginActivity.this, ListActivity.class);
        startActivity(i);
    }

    /**
     * Fabric: método que establece la información del usuario
     * cara a obtener mayor información de posibles crashes
     */
    private void logUser() {
        UserTokenDTO userManager = UserManager.getInstance().getUserTokenDTO();
        Crashlytics.setUserIdentifier(userManager.getToken());
        Crashlytics.setUserEmail(userManager.getEmail());
        Crashlytics.setUserName(userManager.getLogin());
    }
}

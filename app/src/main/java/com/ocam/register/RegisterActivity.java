package com.ocam.register;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ocam.R;
import com.ocam.activity.track.TrackActivity;
import com.ocam.util.ViewUtils;

import static com.ocam.R.id.ilUsername;


public class RegisterActivity extends AppCompatActivity implements RegisterView, RegisterListener {

    private RegisterPresenter registerPresenter;
    private ProgressBar progressBar;
    private Dialog mOverlayDialog;
    private TextInputLayout email;
    private TextInputLayout login;
    private TextInputLayout password;
    private TextInputLayout rePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setUpToolbar();
        this.registerPresenter = new RegisterPresenterImpl(RegisterActivity.this, this, this);
        this.progressBar = (ProgressBar) findViewById(R.id.registerProgress);
        this.email = (TextInputLayout) findViewById(R.id.email);
        this.login = (TextInputLayout) findViewById(R.id.login);
        this.password = (TextInputLayout) findViewById(R.id.password);
        this.rePassword = (TextInputLayout) findViewById(R.id.rePassword);
        this.mOverlayDialog = new Dialog(RegisterActivity.this, android.R.style.Theme_Panel);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Click en back button de la toolbar (vuelve al login)
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showProgress() {
        mOverlayDialog.setCancelable(false);
        mOverlayDialog.show();
        this.progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void hideProgress() {
        mOverlayDialog.cancel();
        this.progressBar.setVisibility(View.GONE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notify(String text) {
        ViewUtils.showToast(RegisterActivity.this, Toast.LENGTH_SHORT, text);
    }

    /**
     * Evento de la vista click en el botón de registro
     * @param view
     */
    public void registrarse(View view) {
        String email = this.email.getEditText().getText().toString();
        String login = this.login.getEditText().getText().toString();
        String password = this.password.getEditText().getText().toString();
        String repassword = this.rePassword.getEditText().getText().toString();
        if (assertFields(email, login, password, repassword)) {
            this.registerPresenter.register(login, email, password);
        }
    }

    /**
     * Comprueba que el contenido del formulario de registro sea correcto
     * @param email
     * @param login
     * @param password
     * @param repassword
     * @return
     */
    private Boolean assertFields(String email, String login, String password, String repassword) {
        if (email == null || login == null || password == null || repassword == null) {
            ViewUtils.showToast(RegisterActivity.this, Toast.LENGTH_SHORT, "Todos los campos son obligatorios");
            return Boolean.FALSE;
        }
        if (email.isEmpty() || login.isEmpty() || password.isEmpty() || repassword.isEmpty()) {
            ViewUtils.showToast(RegisterActivity.this, Toast.LENGTH_SHORT, "Todos los campos son obligatorios");
            return Boolean.FALSE;
        }
        if (!password.equals(repassword)) {
            ViewUtils.showToast(RegisterActivity.this, Toast.LENGTH_SHORT, "Las password no coinciden");
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /**
     * Configura la Toolbar de la actividad
     */
    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.registerToolbal);
        toolbar.setTitle("Registro");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onRegister() {
        ViewUtils.showToast(RegisterActivity.this, Toast.LENGTH_SHORT, "Se ha enviado un correo electrónico para verificar la cuenta");
    }
}


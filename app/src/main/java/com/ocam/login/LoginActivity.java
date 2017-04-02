package com.ocam.login;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.ocam.R;

public class LoginActivity extends Activity implements LoginView {

    private TextInputLayout ilEmail;
    private TextInputLayout ilPassword;
    private LoginPresenter loginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Elimina el titulo
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);

        this.loginPresenter = new LoginPresenterImpl(this);
        this.ilEmail = (TextInputLayout) findViewById(R.id.ilEmail);
        this.ilPassword = (TextInputLayout) findViewById(R.id.ilPassword);

    }

    public void login(View view) {
        String email = ilEmail.getEditText().getText().toString();
        String password = ilPassword.getEditText().getText().toString();
        this.loginPresenter.login(email, password);
    }
}

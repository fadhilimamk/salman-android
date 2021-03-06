package com.salmanitb.alumnisalman.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.salmanitb.alumnisalman.R;
import com.salmanitb.alumnisalman.helper.APIConnector;
import com.salmanitb.alumnisalman.model.CheckEmailResponse;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {

    public static String REGISTRATION_EMAIL =  "REGISTRATION_EMAIL";
    public static String REGISTRATION_PASSWORD = "REGISTRATION_PASSWORD";

    @BindView(R.id.input_email)
    EditText inputEmail;
    @BindView(R.id.input_password)
    EditText inputPassword;
    @BindView(R.id.input_repasword)
    EditText inputRepassword;
    @BindView(R.id.btn_register)
    Button btnRegister;
    @BindView(R.id.txt_login)
    TextView txtLogin;
    @BindView(R.id.loading)
    ProgressBar loading;

    @OnClick(R.id.txt_login)
    public void gotoLogin() {
        Intent i = new Intent(this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    @OnClick(R.id.btn_register)
    public void gotoRegisterForm() {
        final String email = inputEmail.getText().toString();
        final String password = inputPassword.getText().toString();
        final String repassword = inputRepassword.getText().toString();

        disableInput();
        APIConnector.getInstance().checkEmail(email, new APIConnector.ApiCallback<CheckEmailResponse>() {
            @Override
            public void onSuccess(CheckEmailResponse response) {
                enableInput();
                if (!response.isAvailable()) {
                    Toast.makeText(RegisterActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!checkInput(email, password, repassword))
                    return;

                Intent intent = new Intent(RegisterActivity.this, RegistrationIntroActivity.class);
                intent.putExtra(REGISTRATION_EMAIL, email);
                intent.putExtra(REGISTRATION_PASSWORD, password);

                inputEmail.setText("");
                inputPassword.setText("");
                inputRepassword.setText("");

                startActivity(intent);
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(RegisterActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                enableInput();
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
    }

    private boolean checkInput(String email, String password, String repassword) {
        if (email.trim().equals("") || password.equals("") || repassword.equals("")) {
            Toast.makeText(this, "Pastikan semua data sudah terisi!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!LoginActivity.validateEmail(email)) {
            Toast.makeText(this, "Pastikan alamat email sudah benar!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Kata sandi minimal 6 karakter!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password.equals(repassword)) {
            Toast.makeText(this, "Kata sandi tidak sama!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void disableInput() {
        inputEmail.setEnabled(false);
        inputPassword.setEnabled(false);
        inputRepassword.setEnabled(false);
        btnRegister.setEnabled(false);
        btnRegister.setBackgroundColor(getResources().getColor(R.color.separator));
        txtLogin.setEnabled(false);

        inputEmail.setVisibility(View.GONE);
        inputPassword.setVisibility(View.GONE);
        inputRepassword.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);
    }

    private void enableInput() {
        inputEmail.setEnabled(true);
        inputPassword.setEnabled(true);
        inputRepassword.setEnabled(true);
        btnRegister.setEnabled(true);
        btnRegister.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        txtLogin.setEnabled(true);

        inputEmail.setVisibility(View.VISIBLE);
        inputPassword.setVisibility(View.VISIBLE);
        inputRepassword.setVisibility(View.VISIBLE);
        loading.setVisibility(View.GONE);
    }

}

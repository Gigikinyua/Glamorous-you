package com.jemimah.glamorous_you.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.jemimah.glamorous_you.R;
import com.jemimah.glamorous_you.model.User;
import com.jemimah.glamorous_you.retrofit.RetrofitApiClient;
import com.jemimah.glamorous_you.utility.SessionManager;

import org.jetbrains.annotations.NotNull;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private TextView txtSignUp;
    private EditText etEmail, etPassword;
    String password;
    private Button btnLogin;
    private SweetAlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();

        String text = "<font color=#502B5C>Don't have an account yet? </font> <font color=#F50067>Sign up</font>";
        txtSignUp.setText(Html.fromHtml(text));
        txtSignUp.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            password = etPassword.getText().toString().trim();

            if (email.isEmpty()) {
                etEmail.setError("Please provide email address");
            } else if (password.isEmpty()) {
                etPassword.setError("Please enter password");
            } else {
                progressDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
                progressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                progressDialog.setTitleText("Signing you in...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                RetrofitApiClient.getInstance(LoginActivity.this).
                        getApi()
                        .login(email, password)
                        .enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(@NotNull Call<User> call, @NotNull Response<User> response) {
                                runOnUiThread(progressDialog::dismissWithAnimation);
                                if (response.isSuccessful()) {
                                    password = null;
                                    User user = response.body();
                                    runOnUiThread(() -> {
                                        SharedPreferences pref = LoginActivity.this.getSharedPreferences("User Data", MODE_PRIVATE);
                                        SharedPreferences.Editor edit = pref.edit();

                                        Gson gson = new Gson();
                                        String userObject = gson.toJson(user);
                                        edit.putString("user", userObject);
                                        edit.commit();

                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        finish();
                                    });
                                } else {
                                    if (response.code() == 412) {
                                        runOnUiThread(() -> new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                                                .setTitleText(getResources().getString(R.string.error_title))
                                                .setContentText(email + " has not been registered")
                                                .setConfirmText(getResources().getString(R.string.okay))
                                                .show()
                                        );
                                    } else {
                                        runOnUiThread(() -> new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                                                .setTitleText(getResources().getString(R.string.error_title))
                                                .setContentText("Please try again later")
                                                .setConfirmText(getResources().getString(R.string.okay))
                                                .show()
                                        );
                                    }
                                }
                            }

                            @Override
                            public void onFailure(@NotNull Call<User> call, @NotNull Throwable t) {
                                Log.e(TAG, "onFailure: ", t);
                                runOnUiThread(progressDialog::dismissWithAnimation);
                                runOnUiThread(() -> {
                                            new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                                                    .setTitleText(getResources().getString(R.string.error_title))
                                                    .setContentText(getResources().getString(R.string.internet_error))
                                                    .setConfirmText(getResources().getString(R.string.okay))
                                                    .show();
                                        }
                                );
                            }
                        });
            }
        });
    }

    private void initViews() {
        txtSignUp = findViewById(R.id.txtSignUp);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
    }
}
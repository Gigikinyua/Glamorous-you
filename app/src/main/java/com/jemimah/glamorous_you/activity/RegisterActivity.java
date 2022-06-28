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

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    private TextView txtSignIn;
    private EditText etFirstName, etSurname, etEmail, etPhone, etPassword, etConfirmPassword;
    private Button btnRegister;
    private SweetAlertDialog progressDialog;
    String password, password_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();

        String text = "<font color=#502B5C>Already have an account? </font> <font color=#F50067>Login</font>";
        txtSignIn.setText(Html.fromHtml(text));
        txtSignIn.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });

        btnRegister.setOnClickListener(v -> {
            String firstName = etFirstName.getText().toString().trim();
            String surname = etSurname.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            password = etPassword.getText().toString().trim();
            password_2 = etConfirmPassword.getText().toString().trim();

            if (firstName.isEmpty()) {
                etFirstName.setError("Please provide first name");
            } else if (surname.isEmpty()) {
                etSurname.setError("Please provide surname");
            } else if (email.isEmpty()) {
                etEmail.setError("Please provide email address");
            } else if (phone.isEmpty()) {
                etPhone.setError("Please provide phone address");
            } else if (password.isEmpty()) {
                etPassword.setError("Please enter password");
            } else if (password_2.isEmpty()) {
                etConfirmPassword.setError("Please re-enter password");
            } else if (!password_2.equals(password)) {
                etConfirmPassword.setError("Passwords do not match");
            } else {
                progressDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
                progressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                progressDialog.setTitleText("Setting up your account");
                progressDialog.setCancelable(false);
                progressDialog.show();
                RetrofitApiClient.getInstance(RegisterActivity.this).
                        getApi()
                        .registerUser(firstName, surname, phone, email, password)
                        .enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(@NotNull Call<User> call, @NotNull Response<User> response) {
                                runOnUiThread(progressDialog::dismissWithAnimation);
                                if (response.isSuccessful()) {
                                    password = null;
                                    password_2 = null;
                                    User user = response.body();
                                    runOnUiThread(() -> {
                                        SharedPreferences pref = RegisterActivity.this.getSharedPreferences("User Data", MODE_PRIVATE);
                                        SharedPreferences.Editor edit = pref.edit();

                                        Gson gson = new Gson();
                                        String userObject = gson.toJson(user);
                                        edit.putString("user", userObject);
                                        edit.commit();

                                        new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                            .setTitleText("Success")
                                            .setContentText("Your account has been registered successfully.")
                                            .setConfirmButton("Proceed", dialog -> {
                                                dialog.dismissWithAnimation();
                                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                                finish();
                                            })
                                            .show();
                                    });
                                } else {
                                    if (response.code() == 412) {
                                        runOnUiThread(() -> new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.ERROR_TYPE)
                                                .setTitleText(getResources().getString(R.string.error_title))
                                                .setContentText(email + " has already been registered")
                                                .setConfirmText(getResources().getString(R.string.okay))
                                                .show()
                                        );
                                    } else {
                                        runOnUiThread(() -> new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.ERROR_TYPE)
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
                                            new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.ERROR_TYPE)
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
        etFirstName = findViewById(R.id.etFirstName);
        etSurname = findViewById(R.id.etSurname);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        txtSignIn = findViewById(R.id.txtSignIn);
    }
}
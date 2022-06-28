package com.jemimah.glamorous_you.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.jemimah.glamorous_you.R;
import com.jemimah.glamorous_you.model.Business;
import com.jemimah.glamorous_you.model.County;
import com.jemimah.glamorous_you.model.Service;
import com.jemimah.glamorous_you.model.SubCounty;
import com.jemimah.glamorous_you.model.User;
import com.jemimah.glamorous_you.retrofit.RetrofitApiClient;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterBusinessActivity extends AppCompatActivity {
    private static final String TAG = "RegisterBusinessActivity";

    private EditText etBusinessName;
    private SearchableSpinner serviceTypeSpinner, countySpinner, subCountySpinner;
    private Button btnRegisterBusiness;

    private SweetAlertDialog progressDialog;

    private List<Service> services;
    private County[] counties;
    SubCounty[] subcounties;
    County county;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_business);

        iniViews();

        runOnUiThread(() -> {
            getServices();
            getCounties();
        });

        countySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                County county = counties[position];
                ArrayAdapter<SubCounty> subcountySpinnerAdapter = new ArrayAdapter<>(RegisterBusinessActivity.this,
                        R.layout.spinner_layout, R.id.tvName, county.getSubcounties());
                subCountySpinner.setAdapter(subcountySpinnerAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnRegisterBusiness.setOnClickListener(v -> {
            String businessName = etBusinessName.getText().toString().trim();
            int serviceCategory = 0, countyCode = 0, subcountyId = 0;
            boolean error = false;

            if (serviceTypeSpinner.getSelectedItem() != null) {
                Service service = services.get(subCountySpinner.getSelectedItemPosition());
                serviceCategory = service.getId();
            }
            if (countySpinner.getSelectedItem() != null) {
                county = counties[countySpinner.getSelectedItemPosition()];
                countyCode = county.getCode();
                subcounties = county.getSubcounties();
            }
            if (subCountySpinner.getSelectedItem() != null) {
                SubCounty subcounty = subcounties[subCountySpinner.getSelectedItemPosition()];
                subcountyId = subcounty.getId();
            }

            if (TextUtils.isEmpty(businessName)) {
                error = true;
                Toast.makeText(this, "Enter business name", Toast.LENGTH_SHORT).show();
            } else if (countyCode == 0) {
                error = true;
                Toast.makeText(this, "Select County", Toast.LENGTH_SHORT).show();
            } else if (subcountyId == 0) {
                error = true;
                Toast.makeText(this, "Select sub county", Toast.LENGTH_SHORT).show();
            } else if (serviceCategory == 0) {
                error = true;
                Toast.makeText(this, "Select the type of service.", Toast.LENGTH_SHORT).show();
            }

            if (!error) submitData(businessName, serviceCategory, countyCode, subcountyId);
        });
    }

    private void iniViews() {
        etBusinessName = findViewById(R.id.etBusinessName);
        serviceTypeSpinner = findViewById(R.id.serviceTypeSpinner);
        countySpinner = findViewById(R.id.countySpinner);
        subCountySpinner = findViewById(R.id.subCountySpinner);
        btnRegisterBusiness = findViewById(R.id.btnRegisterBusiness);

        SharedPreferences pref = getSharedPreferences("User Data", MODE_PRIVATE);
        String json = pref.getString("user", "");
        Gson gson=new Gson();
        user =gson.fromJson(json, User.class);
    }

    private void submitData(String businessName, int serviceCategory, int countyCode, int subcountyId) {
        progressDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        progressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        progressDialog.setTitleText("Setting up your Business");
        progressDialog.setCancelable(false);
        progressDialog.show();
        RetrofitApiClient.getInstance(RegisterBusinessActivity.this).
                getApi()
                .registerBusiness(businessName, serviceCategory, user.getId(), countyCode, subcountyId)
                .enqueue(new Callback<Business>() {
                    @Override
                    public void onResponse(@NotNull Call<Business> call, @NotNull Response<Business> response) {
                        runOnUiThread(progressDialog::dismissWithAnimation);
                        if (response.isSuccessful()) {
                            runOnUiThread(() -> new SweetAlertDialog(RegisterBusinessActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Success")
                                        .setContentText("Your new business has been registered successfully.")
                                        .setConfirmButton("Proceed", dialog -> {
                                            dialog.dismissWithAnimation();
                                            startActivity(new Intent(RegisterBusinessActivity.this, MainActivity.class));
                                            finish();
                                        })
                                        .show());
                        } else {
                                runOnUiThread(() -> new SweetAlertDialog(RegisterBusinessActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText(getResources().getString(R.string.error_title))
                                        .setContentText("Please try again later")
                                        .setConfirmText(getResources().getString(R.string.okay))
                                        .show()
                                );
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<Business> call, @NotNull Throwable t) {
                        Log.e(TAG, "onFailure: ", t);
                        runOnUiThread(progressDialog::dismissWithAnimation);
                        runOnUiThread(() -> {
                                    new SweetAlertDialog(RegisterBusinessActivity.this, SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText(getResources().getString(R.string.error_title))
                                            .setContentText(getResources().getString(R.string.internet_error))
                                            .setConfirmText(getResources().getString(R.string.okay))
                                            .show();
                                }
                        );
                    }
                });
    }

    private void getServices() {
        RetrofitApiClient.getInstance(RegisterBusinessActivity.this)
                .getApi()
                .getServices()
                .enqueue(new Callback<List<Service>>() {
                    @Override
                    public void onResponse(@NotNull Call<List<Service>> call, @NotNull Response<List<Service>> response) {
                        if (response.code() == 200) {
                            services = new ArrayList<>();
                            services = response.body();

                            runOnUiThread(() -> {
                                ArrayAdapter<Service> serviceArrayAdapter = new ArrayAdapter<>(
                                        RegisterBusinessActivity.this, R.layout.spinner_layout,
                                        R.id.tvName, services);
                                serviceTypeSpinner.setAdapter(serviceArrayAdapter);
                            });
                        } else {
                            runOnUiThread(() -> new SweetAlertDialog(RegisterBusinessActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText(getResources().getString(R.string.error_title))
                                    .setContentText(getResources().getString(R.string.sweetalert_error_message))
                                    .setConfirmText(getResources().getString(R.string.okay))
                                    .show());
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<List<Service>> call, @NotNull Throwable t) {
                        Log.e(TAG, "onFailure: Could not retrieve data. ", t);
                        runOnUiThread(() -> new SweetAlertDialog(RegisterBusinessActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText(getResources().getString(R.string.error_title))
                                .setContentText(getResources().getString(R.string.sweetalert_error_message))
                                .setConfirmText(getResources().getString(R.string.okay))
                                .show());
                    }
                });
    }

    private void getCounties() {
        RetrofitApiClient.getInstance(RegisterBusinessActivity.this)
                .getApi()
                .getCounties()
                .enqueue(new Callback<County[]>() {
                    @Override
                    public void onResponse(@NotNull Call<County[]> call, @NotNull Response<County[]> response) {
                        if (response.code() == 200) {
                            counties = response.body();

                            runOnUiThread(() -> {
                                ArrayAdapter<County> countySpinnerAdapter = new ArrayAdapter<>(
                                        RegisterBusinessActivity.this, R.layout.spinner_layout,
                                        R.id.tvName, counties);
                                countySpinner.setAdapter(countySpinnerAdapter);
                            });
                        } else {
                            runOnUiThread(() -> new SweetAlertDialog(RegisterBusinessActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText(getResources().getString(R.string.error_title))
                                    .setContentText(getResources().getString(R.string.sweetalert_error_message))
                                    .setConfirmText(getResources().getString(R.string.okay))
                                    .show());
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<County[]> call, @NotNull Throwable t) {
                        Log.e(TAG, "onFailure: Could not retrieve data. ", t);
                        runOnUiThread(() -> new SweetAlertDialog(RegisterBusinessActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText(getResources().getString(R.string.error_title))
                                .setContentText(getResources().getString(R.string.sweetalert_error_message))
                                .setConfirmText(getResources().getString(R.string.okay))
                                .show());
                    }
                });
    }
}
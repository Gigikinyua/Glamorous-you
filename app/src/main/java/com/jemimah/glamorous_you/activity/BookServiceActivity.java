package com.jemimah.glamorous_you.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.androidbuts.multispinnerfilter.MultiSpinnerSearch;
import com.google.gson.Gson;
import com.jemimah.glamorous_you.R;
import com.jemimah.glamorous_you.model.Appointment;
import com.jemimah.glamorous_you.model.Business;
import com.jemimah.glamorous_you.model.BusinessService;
import com.jemimah.glamorous_you.model.User;
import com.jemimah.glamorous_you.retrofit.RetrofitApiClient;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookServiceActivity extends AppCompatActivity {
    private static final String TAG = "BookServiceActivity";

    private Business activeBusiness;
    private Toolbar toolbar;
    private CalendarView calendarView;
    private TimePicker timePicker;
    private TextView txtAdultCount, txtChildrenCount, clientCountError, serviceSelectError;
    private ImageView imgAdultMinus, imgAdultPlus, imgChildrenMinus, imgChildrenPlus;

    int adultCount = 0, childCount = 0;

    private Button btnBookAppointment;
    private final String payment_type = "Cash";

    List<String> mylist;
    List<Long> selectedIds;

    private List<BusinessService> businessServices = new ArrayList<>();

    String appointment_date, appointment_time, selectedServiceNames = null;
    int selectedService;

    private User user;
    private Appointment appointment;

    private SweetAlertDialog progressDialog;

    private final Calendar timeSelect = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_service);

        initViews();

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle(activeBusiness.getName());

        businessServices = activeBusiness.getBusinessServices();

        List<KeyPairBoolData> businessServicesArray = new ArrayList<>();
        for (int i = 0; i < businessServices.size(); i++) {
            BusinessService businessService = businessServices.get(i);
            KeyPairBoolData h = new KeyPairBoolData();
            h.setId(businessService.getId());
            h.setName(businessService.getName());
            h.setSelected(false);
            businessServicesArray.add(h);
        }

        MultiSpinnerSearch services_spinner = findViewById(R.id.services_spinner);
        services_spinner.setSearchHint("Select service(s)");
        services_spinner.setSearchEnabled(false);
        services_spinner.setItems(businessServicesArray, items -> {
            mylist = new ArrayList<>();
            selectedIds = new ArrayList<>();
            //The followings are selected items.
            for (int i = 0; i < items.size(); i++) {
                mylist.add(items.get(i).getName());
                selectedIds.add(items.get(i).getId());
            }

            selectedServiceNames = TextUtils.join(", ", mylist);
            Log.d(TAG, "onCreate: " + selectedIds);
        });

        //set minimum date on calendar view
        calendarView.setMinDate((new Date().getTime()));

        imgAdultMinus.setOnClickListener(v -> {
            if (adultCount > 0) {
                adultCount--;
            }
            setText(adultCount, txtAdultCount);
        });

        imgAdultPlus.setOnClickListener(v -> {
            adultCount++;
            setText(adultCount, txtAdultCount);
        });

        imgChildrenMinus.setOnClickListener(v -> {
            if (childCount > 0) {
                childCount--;
            }
            setText(childCount, txtChildrenCount);
        });

        imgChildrenPlus.setOnClickListener(v -> {
            childCount++;
            setText(childCount, txtChildrenCount);
        });

        btnBookAppointment.setOnClickListener(v -> {
            //Get appointment date
            DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            appointment_date = formatter1.format(calendarView.getDate());

            //Get appointment time
            timeSelect.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
            timeSelect.set(Calendar.MINUTE, timePicker.getMinute());
            timeSelect.set(Calendar.SECOND, 0);

            DateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.US);
            appointment_time = formatter.format(timeSelect.getTime());

            serviceSelectError.setVisibility(View.GONE);
            clientCountError.setVisibility(View.GONE);
            if (childCount == 0 && adultCount == 0) {
                clientCountError.setVisibility(View.VISIBLE);
            } else if (selectedIds == null || selectedIds.isEmpty()) {
                serviceSelectError.setVisibility(View.VISIBLE);
            } else {
                Dialog d = new Dialog(BookServiceActivity.this);
                d.setContentView(R.layout.confirm_appointment_dialog);

                TextView txtBusiness = d.findViewById(R.id.txtBusiness);
                txtBusiness.setText("Business: " + activeBusiness.getName());

                TextView txtService = d.findViewById(R.id.txtService);
                txtService.setText("Services: " + selectedServiceNames);

                TextView txtClients = d.findViewById(R.id.txtClients);
                txtClients.setText("Clients: Adults (" + adultCount + ") Children (" + childCount + ")");

                TextView txtAppointmentDate = d.findViewById(R.id.txtAppointmentDate);
                txtAppointmentDate.setText("Appointment date: " + appointment_date);

                TextView txtAppointmentTime = d.findViewById(R.id.txtAppointmentTime);
                txtAppointmentTime.setText("Appointment time: " + appointment_time);

                TextView txtPaymentMode = d.findViewById(R.id.txtPaymentMode);
                txtPaymentMode.setText("Form Of Payment: " + payment_type);


                d.findViewById(R.id.btnCancel).setOnClickListener(v1 -> d.dismiss());
                d.findViewById(R.id.btnProceed).setOnClickListener(v2 -> {
                    d.dismiss();
                    bookAppointment();
                });

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(d.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                d.show();
                d.getWindow().setAttributes(lp);
            }
        });
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        calendarView = findViewById(R.id.calendarView);
        timePicker = findViewById(R.id.timePicker);
        txtAdultCount = findViewById(R.id.txtAdultCount);
        imgAdultMinus = findViewById(R.id.imgAdultMinus);
        imgAdultPlus = findViewById(R.id.imgAdultPlus);
        txtChildrenCount = findViewById(R.id.txtChildrenCount);
        imgChildrenMinus = findViewById(R.id.imgChildrenMinus);
        imgChildrenPlus = findViewById(R.id.imgChildrenPlus);
        btnBookAppointment = findViewById(R.id.btnBookAppointment);
        serviceSelectError = findViewById(R.id.serviceSelectError);
        clientCountError = findViewById(R.id.clientCountError);

        //get extras
        activeBusiness = (Business) getIntent().getExtras().getSerializable("business");
        appointment = (Appointment) getIntent().getExtras().getSerializable("appointment");
        if (activeBusiness == null) {
            activeBusiness = appointment.getBusiness();
        }

        SharedPreferences pref = BookServiceActivity.this.getSharedPreferences("User Data", MODE_PRIVATE);
        String json = pref.getString("user", "");
        Gson gson = new Gson();
        user = gson.fromJson(json, User.class);
    }

    private void bookAppointment() {
        Log.d(TAG, "bookAppointment: " + selectedIds);
        progressDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        progressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        progressDialog.setTitleText("Setting up your appointment...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        RetrofitApiClient.getInstance(BookServiceActivity.this).
                getApi()
                .bookAppointment(user.getId(), activeBusiness.getId(), selectedIds, adultCount,
                        childCount, appointment_date, appointment_time, payment_type)
                .enqueue(new Callback<List<Appointment>>() {
                    @Override
                    public void onResponse(@NotNull Call<List<Appointment>> call, @NotNull Response<List<Appointment>> response) {
                        runOnUiThread(progressDialog::dismissWithAnimation);
                        if (response.isSuccessful()) {
                            runOnUiThread(() -> new SweetAlertDialog(BookServiceActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Success")
                                    .setContentText("Your appointment has been booked. " +
                                            "The business owner will provide feedback shortly.")
                                    .setConfirmButton("Back to Home", dialog -> {
                                        dialog.dismissWithAnimation();
                                        startActivity(new Intent(BookServiceActivity.this, MainActivity.class));
                                        finish();
                                    })
                                    .show());
                        } else {
                            runOnUiThread(() -> new SweetAlertDialog(BookServiceActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText(getResources().getString(R.string.error_title))
                                    .setContentText("Please try again later")
                                    .setConfirmText(getResources().getString(R.string.okay))
                                    .show()
                            );
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<List<Appointment>> call, @NotNull Throwable t) {
                        Log.e(TAG, "onFailure: ", t);
                        runOnUiThread(progressDialog::dismissWithAnimation);
                        runOnUiThread(() -> {
                                    new SweetAlertDialog(BookServiceActivity.this, SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText(getResources().getString(R.string.error_title))
                                            .setContentText(getResources().getString(R.string.internet_error))
                                            .setConfirmText(getResources().getString(R.string.okay))
                                            .show();
                                }
                        );
                    }
                });
    }

    public void setText(int count, TextView textView) {
        textView.setText(String.valueOf(count));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
package com.jemimah.glamorous_you.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jemimah.glamorous_you.R;
import com.jemimah.glamorous_you.activity.BookServiceActivity;
import com.jemimah.glamorous_you.activity.MainActivity;
import com.jemimah.glamorous_you.fragment.BusinessAppointmentFragment;
import com.jemimah.glamorous_you.model.Appointment;
import com.jemimah.glamorous_you.retrofit.RetrofitApiClient;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Layout(R.layout.child_layout)
public class ChildView {
    private static String TAG ="ChildView";

    private SweetAlertDialog progressDialog;

    @View(R.id.txtCustomerName)
    TextView txtCustomerName;

    @View(R.id.txtPhone)
    TextView txtPhone;

    @View(R.id.btnStatus)
    Button btnStatus;

    @View(R.id.btnCancel)
    Button btnCancel;

    private final Context mContext;
    private final Activity mActivity;
    private final Appointment appointment;

    public ChildView(Context mContext, Activity mActivity, Appointment appointment) {
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.appointment = appointment;
    }

    @Resolve
    private void onResolve(){
        txtCustomerName.setText("Name: " + appointment.getBooked_by().getFirstName() + " " + appointment.getBooked_by().getSurname());
        txtPhone.setText(appointment.getBooked_by().getPhone());

        if(appointment.getStatus().equals("Pending")) {
            btnStatus.setText("Accept");
            btnStatus.setOnClickListener(v -> saveStatus("Accepted"));
            btnCancel.setText("Reject");
            btnCancel.setOnClickListener(v -> saveStatus("Declined"));
        } else if (appointment.getStatus().equals("Accepted")) {
            btnStatus.setText("Completed");
            btnStatus.setOnClickListener(v -> saveStatus("Completed"));
            btnCancel.setText("Cancel");
            btnCancel.setOnClickListener(v -> saveStatus("Canceled"));
        } else if (appointment.getStatus().equals("Completed") ||
                appointment.getStatus().equals("Canceled") ||
                appointment.getStatus().equals("Declined")) {
            btnStatus.setVisibility(android.view.View.GONE);
            btnCancel.setVisibility(android.view.View.GONE);
        }
    }

    private void saveStatus(String status) {
        progressDialog = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE);
        progressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        progressDialog.setTitleText("Saving appointment status...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        RetrofitApiClient.getInstance(mContext).
                getApi()
                .saveStatus(appointment.getId(), status)
                .enqueue(new Callback<Appointment>() {
                    @Override
                    public void onResponse(@NotNull Call<Appointment> call, @NotNull Response<Appointment> response) {
                        mActivity.runOnUiThread(progressDialog::dismissWithAnimation);
                        if (response.isSuccessful()) {
                            Appointment appointment = response.body();
                            BusinessAppointmentFragment businessAppointmentFragment = new BusinessAppointmentFragment();
                            businessAppointmentFragment.refreshFragment();
                        } else {
                            mActivity.runOnUiThread(() -> new SweetAlertDialog(mContext, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText(mContext.getResources().getString(R.string.error_title))
                                    .setContentText("Please try again later")
                                    .setConfirmText(mContext.getResources().getString(R.string.okay))
                                    .show()
                            );
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<Appointment> call, @NotNull Throwable t) {
                        Log.e(TAG, "onFailure: ", t);
                        mActivity.runOnUiThread(progressDialog::dismissWithAnimation);
                        mActivity.runOnUiThread(() -> new SweetAlertDialog(mContext, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText(mContext.getResources().getString(R.string.error_title))
                                .setContentText(mContext.getResources().getString(R.string.internet_error))
                                .setConfirmText(mContext.getResources().getString(R.string.okay))
                                .show()
                        );
                    }
                });
    }
}
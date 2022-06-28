package com.jemimah.glamorous_you.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.jemimah.glamorous_you.R;
import com.jemimah.glamorous_you.activity.CategoryActivity;
import com.jemimah.glamorous_you.adapter.HistoryRVAdapter;
import com.jemimah.glamorous_you.model.Appointment;
import com.jemimah.glamorous_you.model.User;
import com.jemimah.glamorous_you.retrofit.RetrofitApiClient;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentFragment extends Fragment {
    private static final String TAG = "AppointmentFragment";

    private Toolbar toolbar;
    private RecyclerView rvHistory;
    private ShimmerFrameLayout mShimmerViewContainer;

    private User user;
    private List<Appointment> appointments;

    public AppointmentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_appointment, container, false);

        initView(view);

        getActivity().runOnUiThread(this::getAppointments);

        toolbar.setTitle("My Appointments");

        return view;
    }

    private void initView(View view) {
        rvHistory = view.findViewById(R.id.rvHistory);
        toolbar = view.findViewById(R.id.toolbar);
        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);

        SharedPreferences pref = getActivity().getSharedPreferences("User Data", MODE_PRIVATE);
        String json = pref.getString("user", "");
        Gson gson = new Gson();
        user = gson.fromJson(json, User.class);
    }

    private void getAppointments() {
        RetrofitApiClient.getInstance(getActivity()).getApi()
                .getAppointments(user.getId())
                .enqueue(new Callback<List<Appointment>>() {
                    @Override
                    public void onResponse(@NotNull Call<List<Appointment>> call, @NotNull Response<List<Appointment>> response) {
                        if (response.code() == 200) {
                            appointments = new ArrayList<>();
                            appointments = response.body();

                            rvHistory.setLayoutManager(new LinearLayoutManager(getActivity()));
                            rvHistory.setHasFixedSize(true);

                            HistoryRVAdapter historyRVAdapter = new HistoryRVAdapter(getContext());
                            rvHistory.setAdapter(historyRVAdapter);
                            historyRVAdapter.setAppointmentsList(appointments);

                            // stop animating Shimmer and hide the layout
                            mShimmerViewContainer.stopShimmerAnimation();
                            mShimmerViewContainer.setVisibility(View.GONE);
                        } else {
                            getActivity().runOnUiThread(() -> new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText(getResources().getString(R.string.error_title))
                                    .setContentText(getResources().getString(R.string.sweetalert_error_message))
                                    .setConfirmText(getResources().getString(R.string.okay))
                                    .show());
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<List<Appointment>> call, @NotNull Throwable t) {
                        Log.e(TAG, "onFailure: Could not retrieve data. ", t);
                        getActivity().runOnUiThread(() -> new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText(getResources().getString(R.string.error_title))
                                .setContentText(getResources().getString(R.string.sweetalert_error_message))
                                .setConfirmText(getResources().getString(R.string.okay))
                                .show());
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        mShimmerViewContainer.startShimmerAnimation();
    }

    @Override
    public void onPause() {
        mShimmerViewContainer.stopShimmerAnimation();
        super.onPause();
    }
}
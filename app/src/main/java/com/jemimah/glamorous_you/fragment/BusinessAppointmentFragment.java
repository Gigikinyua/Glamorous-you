package com.jemimah.glamorous_you.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.jemimah.glamorous_you.R;
import com.jemimah.glamorous_you.activity.ServiceProviderActivity;
import com.jemimah.glamorous_you.model.Appointment;
import com.jemimah.glamorous_you.model.Business;
import com.jemimah.glamorous_you.retrofit.RetrofitApiClient;
import com.jemimah.glamorous_you.ui.ChildView;
import com.jemimah.glamorous_you.ui.HeaderView;
import com.mindorks.placeholderview.ExpandablePlaceHolderView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusinessAppointmentFragment extends Fragment {
    private static final String TAG = "BusinessAppointmentFragment";

//    private RecyclerView rvHistory;
    private ShimmerFrameLayout mShimmerViewContainer;
    private ExpandablePlaceHolderView expandablePlaceHolder;
    private Business business;
    private Toolbar toolbar;
    
    private List<Appointment> appointments;

    public BusinessAppointmentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_business_appointment, container, false);

        initView(view);

        toolbar.setTitle("All Appointments");

        getActivity().runOnUiThread(() -> getAppointments(business.getId()));

        return view;
    }

    public void setBusiness(Business business)
    {
        this.business = business;
    }

    private void initView(View view) {
        expandablePlaceHolder = view.findViewById(R.id.expandablePlaceHolder);
        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
        toolbar = view.findViewById(R.id.toolbar);
    }

    public void getAppointments(int businessId) {
        RetrofitApiClient.getInstance(getActivity()).getApi()
                .getBusinessHistory(businessId)
                .enqueue(new Callback<List<Appointment>>() {
                    @Override
                    public void onResponse(@NotNull Call<List<Appointment>> call, @NotNull Response<List<Appointment>> response) {
                        if (response.code() == 200) {
                            appointments = new ArrayList<>();
                            appointments = response.body();
                            
                            getHeaderAndChild(appointments);

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

    private void getHeaderAndChild(List<Appointment> appointments) {
        for (Appointment appointment : appointments){
            expandablePlaceHolder.addView(new HeaderView(getContext(), appointment));
            expandablePlaceHolder.addView(new ChildView(getContext(), getActivity(), appointment));
        }
    }

    public void refreshFragment(){
        Fragment fragment = new DashboardFragment();
        ((ServiceProviderActivity) requireActivity()).loadFragment(fragment);
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
package com.jemimah.glamorous_you.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.jemimah.glamorous_you.R;
import com.jemimah.glamorous_you.adapter.CategoriesRVAdapter;
import com.jemimah.glamorous_you.model.Service;
import com.jemimah.glamorous_you.retrofit.RetrofitApiClient;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private List<Service> services;

    private RecyclerView rvCategories;

    private ShimmerFrameLayout catShimmerViewContainer;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initView(view);

        getActivity().runOnUiThread(this::getServices);

        return view;
    }

    private void initView(View view) {
        rvCategories = view.findViewById(R.id.rvCategories);
        catShimmerViewContainer = view.findViewById(R.id.cat_shimmer_view_container);
    }

    private void getServices() {
        RetrofitApiClient.getInstance(getActivity()).getApi()
                .getServices()
                .enqueue(new Callback<List<Service>>() {
                    @Override
                    public void onResponse(@NotNull Call<List<Service>> call, @NotNull Response<List<Service>> response) {
                        if (response.code() == 200) {
                            services = new ArrayList<>();
                            services = response.body();

                            rvCategories.setHasFixedSize(true);

                            CategoriesRVAdapter categoriesRVAdapter = new CategoriesRVAdapter(getContext());
                            rvCategories.setAdapter(categoriesRVAdapter);
                            categoriesRVAdapter.setServicesList(services);

                            // stop animating Shimmer and hide the layout
                            catShimmerViewContainer.stopShimmerAnimation();
                            catShimmerViewContainer.setVisibility(View.GONE);
                        } else {
                            getActivity().runOnUiThread(() -> new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText(getResources().getString(R.string.error_title))
                                    .setContentText(getResources().getString(R.string.sweetalert_error_message))
                                    .setConfirmText(getResources().getString(R.string.okay))
                                    .show());
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<List<Service>> call, @NotNull Throwable t) {
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
        catShimmerViewContainer.startShimmerAnimation();
    }

    @Override
    public void onPause() {
        catShimmerViewContainer.stopShimmerAnimation();
        super.onPause();
    }
}
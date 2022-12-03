package com.jemimah.glamorous_you.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.jemimah.glamorous_you.R;
import com.jemimah.glamorous_you.activity.LoginActivity;
import com.jemimah.glamorous_you.activity.MainActivity;
import com.jemimah.glamorous_you.activity.RegisterActivity;
import com.jemimah.glamorous_you.activity.RegisterBusinessActivity;
import com.jemimah.glamorous_you.adapter.MyBusinessesAdapter;
import com.jemimah.glamorous_you.model.Business;
import com.jemimah.glamorous_you.model.User;
import com.jemimah.glamorous_you.retrofit.RetrofitApiClient;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyAccountFragment extends Fragment {
    private static final String TAG = "MyAccountFragment";

    private TextView txtLogout, toolbarTitle, txtServiceProvider;
    private User user;
    private Business business;

    private SweetAlertDialog progressDialog;

    private List<Business> businesses;

    public MyAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);

        initViews(view);

        if (user == null) {
            toolbarTitle.setText("Glamorous you");
            txtLogout.setText("Login");

            txtLogout.setOnClickListener(v -> getActivity().runOnUiThread(() -> {
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }));

            txtServiceProvider.setVisibility(View.GONE);
        } else {
            toolbarTitle.setText(user.getFirstName() + " " + user.getSurname());
            txtLogout.setText("Logout");

            txtLogout.setOnClickListener(v -> getActivity().runOnUiThread(() -> {
                SharedPreferences preferences = getActivity().getSharedPreferences("User Data", MODE_PRIVATE);
                preferences.edit().remove("user").commit();
                Fragment fragment = new HomeFragment();
                ((MainActivity) requireActivity()).loadFragment(fragment);
                ((MainActivity) requireActivity()).setSelectedItem(R.id.navigation_home);
            }));

            if (user.getIsServiceProvider().equals("yes")) {
                txtServiceProvider.setText("Login to your Service Provider Account");
                txtServiceProvider.setOnClickListener(v -> {
                    progressDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
                    progressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                    progressDialog.setTitleText("Fetching your businesses");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    getActivity().runOnUiThread(this::fetchBusinesses);

                });
            } else {
                txtServiceProvider.setText("Open a Service Provider Account");
                txtServiceProvider.setOnClickListener(v ->
                        startActivity(new Intent(getActivity(), RegisterActivity.class)));
            }
        }

        return view;
    }

    private void fetchBusinesses() {
        RetrofitApiClient.getInstance(getActivity()).getApi()
                .getMyBusinesses(user.getId())
                .enqueue(new Callback<List<Business>>() {
                    @Override
                    public void onResponse(@NotNull Call<List<Business>> call, @NotNull Response<List<Business>> response) {
                        getActivity().runOnUiThread(progressDialog::dismissWithAnimation);
                        if (response.code() == 200) {
                            businesses = new ArrayList<>();
                            businesses = response.body();

                            Dialog d = new Dialog(getActivity());
                            d.setContentView(R.layout.service_provicer_dialog);

                            TextView txtNewBusiness = d.findViewById(R.id.txtNewBusiness);
                            txtNewBusiness.setOnClickListener(v1 ->
                                    startActivity(new Intent(getActivity(), RegisterBusinessActivity.class)));

                            RecyclerView rvBusinesses = d.findViewById(R.id.rvBusinesses);
                            rvBusinesses.setLayoutManager(new LinearLayoutManager(getActivity()));
                            rvBusinesses.setHasFixedSize(true);

                            MyBusinessesAdapter myBusinessesAdapter = new MyBusinessesAdapter(getContext());
                            rvBusinesses.setAdapter(myBusinessesAdapter);
                            myBusinessesAdapter.setBusinessesList(businesses);

                            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                            lp.copyFrom(d.getWindow().getAttributes());
                            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                            d.show();
                            d.getWindow().setAttributes(lp);

                        } else {
                            getActivity().runOnUiThread(() -> new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText(getResources().getString(R.string.error_title))
                                    .setContentText(getResources().getString(R.string.sweetalert_error_message))
                                    .setConfirmText(getResources().getString(R.string.okay))
                                    .show());
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<List<Business>> call, @NotNull Throwable t) {
                        getActivity().runOnUiThread(progressDialog::dismissWithAnimation);
                        Log.e(TAG, "onFailure: Could not retrieve data. ", t);
                        getActivity().runOnUiThread(() -> new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText(getResources().getString(R.string.error_title))
                                .setContentText(getResources().getString(R.string.sweetalert_error_message))
                                .setConfirmText(getResources().getString(R.string.okay))
                                .show());
                    }
                });
    }

    private void initViews(View view) {
        txtLogout = view.findViewById(R.id.txtLogout);
        toolbarTitle = view.findViewById(R.id.toolbar_title);
        txtServiceProvider = view.findViewById(R.id.txtServiceProvider);

        SharedPreferences pref = getActivity().getSharedPreferences("User Data", MODE_PRIVATE);
        String json = pref.getString("user", "");
        Gson gson = new Gson();
        user = gson.fromJson(json, User.class);
    }
}
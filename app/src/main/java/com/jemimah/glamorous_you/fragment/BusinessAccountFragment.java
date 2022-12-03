package com.jemimah.glamorous_you.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jemimah.glamorous_you.R;
import com.jemimah.glamorous_you.activity.MainActivity;
import com.jemimah.glamorous_you.model.Business;
import com.jemimah.glamorous_you.model.User;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class BusinessAccountFragment extends Fragment {
    private static final String TAG = "BusinessAccountFragment";

    private TextView txtLogout, toolbarTitle, txtServiceProvider;
    private User user;

    private SweetAlertDialog progressDialog;

    private List<Business> businesses;

    public BusinessAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_my_account, container, false);

        initViews(view);

        txtServiceProvider.setText("Back to customer profile");
        txtServiceProvider.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), MainActivity.class));
            getActivity().finish();
        });

        txtLogout.setOnClickListener(v -> {
            getActivity().runOnUiThread(() -> {
                SharedPreferences preferences = getActivity().getSharedPreferences("User Data", MODE_PRIVATE);
                preferences.edit().remove("user").commit();
                startActivity(new Intent(getContext(), MainActivity.class));
                getActivity().finish();
            });
        });

        return view;
    }

    private void initViews(View view) {
        txtLogout = view.findViewById(R.id.txtLogout);
        toolbarTitle = view.findViewById(R.id.toolbar_title);
        txtServiceProvider = view.findViewById(R.id.txtServiceProvider);

        SharedPreferences pref = getActivity().getSharedPreferences("User Data", MODE_PRIVATE);
        String json = pref.getString("user", "");
        Gson gson=new Gson();
        user =gson.fromJson(json, User.class);
    }
}
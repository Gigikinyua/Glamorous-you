package com.jemimah.glamorous_you.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.gson.Gson;
import com.jemimah.glamorous_you.R;
import com.jemimah.glamorous_you.fragment.BusinessAccountFragment;
import com.jemimah.glamorous_you.fragment.BusinessAppointmentFragment;
import com.jemimah.glamorous_you.fragment.DashboardFragment;
import com.jemimah.glamorous_you.model.Business;
import com.jemimah.glamorous_you.model.User;

public class ServiceProviderActivity extends AppCompatActivity {
    private static final String TAG = "ServiceProviderActivity";

    public BottomNavigationView navigation;
    private User user;
    private Business business;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider);

        initViews();

        navigation.setOnItemSelectedListener(mOnNavigationItemSelectedListener);

        DashboardFragment dashboardFragment = new DashboardFragment();
        dashboardFragment.setBusiness(business);
        loadFragment(dashboardFragment);
    }

    private void initViews() {
        navigation = findViewById(R.id.navigation);

        //get extras
        business = (Business) getIntent().getExtras().getSerializable("business");

        SharedPreferences pref = ServiceProviderActivity.this.getSharedPreferences("User Data", MODE_PRIVATE);
        String json = pref.getString("user", "");
        Gson gson = new Gson();
        user = gson.fromJson(json, User.class);
    }

    private final NavigationBarView.OnItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        Fragment fragment;
        if (item.getItemId() == R.id.navigation_dashboard) {
            DashboardFragment dashboardFragment = new DashboardFragment();
            dashboardFragment.setBusiness(business);
            loadFragment(dashboardFragment);
            return true;
        } else if (item.getItemId() == R.id.navigation_appointment) {
            BusinessAppointmentFragment businessAppointmentFragment = new BusinessAppointmentFragment();
            businessAppointmentFragment.setBusiness(business);
            loadFragment(businessAppointmentFragment);
            return true;
        } else if (item.getItemId() == R.id.navigation_account) {
            fragment = new BusinessAccountFragment();
            loadFragment(fragment);
            return true;
        } else {
            return false;
        }
    };


    public void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ServiceProviderActivity.this, MainActivity.class));
        finish();
    }
}
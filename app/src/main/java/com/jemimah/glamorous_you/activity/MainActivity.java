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
import com.jemimah.glamorous_you.fragment.AppointmentFragment;
import com.jemimah.glamorous_you.fragment.HomeFragment;
import com.jemimah.glamorous_you.fragment.MyAccountFragment;
import com.jemimah.glamorous_you.model.User;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    public BottomNavigationView navigation;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        navigation.setOnItemSelectedListener(mOnNavigationItemSelectedListener);

        loadFragment(new HomeFragment());
        setSelectedItem(R.id.navigation_home);
    }

    private void initViews() {
        navigation = findViewById(R.id.navigation);

        SharedPreferences pref = MainActivity.this.getSharedPreferences("User Data", MODE_PRIVATE);
        String json = pref.getString("user", "");
        Gson gson=new Gson();
        user =gson.fromJson(json, User.class);
    }

    private final NavigationBarView.OnItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        Fragment fragment;
        if (item.getItemId() == R.id.navigation_home) {
            fragment = new HomeFragment();
            loadFragment(fragment);
            return true;
        } else if (item.getItemId() == R.id.navigation_appointment) {
            if (user == null) {
                runOnUiThread(() -> new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Warning")
                        .setContentText("Unfortunately, you need to login to access this page.")
                        .setConfirmButton("Login", dialog -> {
                            dialog.dismissWithAnimation();
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            finish();
                        })
                        .show()
                );
            } else {
                fragment = new AppointmentFragment();
                loadFragment(fragment);
            }
            return true;
        } else if (item.getItemId() == R.id.navigation_account) {
            fragment = new MyAccountFragment();
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

    public void setSelectedItem (int id) {
        navigation.setSelectedItemId(id);
    }
}
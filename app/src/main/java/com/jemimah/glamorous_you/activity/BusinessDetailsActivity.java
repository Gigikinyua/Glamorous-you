package com.jemimah.glamorous_you.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.jemimah.glamorous_you.R;
import com.jemimah.glamorous_you.adapter.ServicesGridAdapter;
import com.jemimah.glamorous_you.model.Business;
import com.jemimah.glamorous_you.model.BusinessService;
import com.jemimah.glamorous_you.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class BusinessDetailsActivity extends AppCompatActivity {

    private TextView txtLocation;
    private Business activeBusiness;
    private Toolbar toolbar;
    private Button btnBookAppointment;
    private RecyclerView rvServicesOffered;

    private User user;

    List<BusinessService> businessServices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_details);

        initViews();

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle(activeBusiness.getName());

        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_profile);
        toolbar.setOverflowIcon(drawable);

        txtLocation.setText("Location: " + activeBusiness.getCounty().getName());

        businessServices = activeBusiness.getBusinessServices();

        // set a GridLayoutManager with default vertical orientation and 2 number of columns
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        rvServicesOffered.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView
        //  call the constructor of CustomAdapter to send the reference and data to Adapter
        ServicesGridAdapter servicesGridAdapter = new ServicesGridAdapter(BusinessDetailsActivity.this, businessServices);
        rvServicesOffered.setAdapter(servicesGridAdapter); // set the Adapter to RecyclerView


        btnBookAppointment.setOnClickListener(v -> {
            if (user == null) {
                runOnUiThread(() -> new SweetAlertDialog(BusinessDetailsActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Warning")
                        .setContentText("Unfortunately, you need to login to access this page.")
                        .setConfirmButton("Login", dialog -> {
                            dialog.dismissWithAnimation();
                            startActivity(new Intent(BusinessDetailsActivity.this, LoginActivity.class));
                        })
                        .show()
                );
            } else {
                Intent intent = new Intent(this, BookServiceActivity.class);
                intent.putExtra("business", activeBusiness);
                startActivity(intent);
            }
        });
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        txtLocation = findViewById(R.id.txtLocation);
        btnBookAppointment = findViewById(R.id.btn_book_appointment);
        rvServicesOffered = findViewById(R.id.rvServicesOffered);

        //get extras
        activeBusiness = (Business) getIntent().getExtras().getSerializable("business");

        SharedPreferences pref = BusinessDetailsActivity.this.getSharedPreferences("User Data", MODE_PRIVATE);
        String json = pref.getString("user", "");
        Gson gson = new Gson();
        user = gson.fromJson(json, User.class);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.account_menu, menu);
        if (menu instanceof MenuBuilder) {  //To display icon on overflow menu

            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_changepassword) {
            Intent intent=new Intent(getApplicationContext(),ChangePassword.class);
            startActivity(intent);
            return true;
        }else if(id==R.id.action_logout){
            finish();
        }
        return true;
    }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
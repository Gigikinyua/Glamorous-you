package com.jemimah.glamorous_you.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import com.jemimah.glamorous_you.R;
import com.jemimah.glamorous_you.model.Business;
import com.jemimah.glamorous_you.model.BusinessService;
import com.jemimah.glamorous_you.model.User;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {
    private static final String TAG = "DashboardFragment";

    private TextView txtUsername, txtBusinessName, txtLocation;
    private ChipGroup servicesChipGroup;

    private Business business;
    private User user;

    List<BusinessService> businessServices = new ArrayList<>();

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        initViews(view);

        txtUsername.setText("Hi! " + user.getFirstName());
        txtBusinessName.setText(business.getName());
        txtLocation.setText(business.getCounty().getName());

        businessServices = business.getBusinessServices();

        for (int i = 0; i < businessServices.size(); i++) {
            BusinessService service = businessServices.get(i);
            Chip chip = (Chip) inflater.inflate(R.layout.layout_chip_choice, servicesChipGroup, false);
            chip.setText(service.getName() + " (Ksh. " + service.getPrice() + ")");
            chip.setCloseIconVisible(false);
            servicesChipGroup.addView(chip);

            Log.d(TAG, "onCreateView: " + chip.getId());
        }
        return view;
    }

    private void initViews(View view) {
        servicesChipGroup = view.findViewById(R.id.servicesChipGroup);
        txtUsername = view.findViewById(R.id.txtUsername);
        txtBusinessName = view.findViewById(R.id.txtBusinessName);
        txtLocation = view.findViewById(R.id.txtLocation);

        SharedPreferences pref = getActivity().getSharedPreferences("User Data", MODE_PRIVATE);
        String json = pref.getString("user", "");
        Gson gson=new Gson();
        user =gson.fromJson(json, User.class);
    }

    public void setBusiness(Business business)
    {
        this.business = business;
    }
}
package com.jemimah.glamorous_you.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jemimah.glamorous_you.R;
import com.jemimah.glamorous_you.activity.BusinessDetailsActivity;
import com.jemimah.glamorous_you.activity.ServiceProviderActivity;
import com.jemimah.glamorous_you.model.Business;

import java.util.ArrayList;
import java.util.List;

public class MyBusinessesAdapter  extends RecyclerView.Adapter<MyBusinessesAdapter.ViewHolder> {
    private static final String TAG = "MyBusinessesAdapter";
    private List<Business> businesses = new ArrayList<>();
    private final Context context;

    public MyBusinessesAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MyBusinessesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_my_businesses, parent, false);
        return new MyBusinessesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyBusinessesAdapter.ViewHolder holder, int position) {
        Business business = businesses.get(position);
        holder.txtName.setText(business.getName());
        holder.businessLayout.setOnClickListener(v -> {
            Intent intent = new Intent(context, ServiceProviderActivity.class);
            intent.putExtra("business", business);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return businesses.size();
    }

    public void setBusinessesList(List<Business> businesses) {
        this.businesses = businesses;
        Log.d(TAG, "setBusinessesList: " + businesses);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtName;
        private final RelativeLayout businessLayout;

        private ViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            businessLayout = itemView.findViewById(R.id.businessLayout);
        }
    }
}

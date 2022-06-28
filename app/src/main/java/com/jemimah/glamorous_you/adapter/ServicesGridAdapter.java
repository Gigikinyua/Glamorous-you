package com.jemimah.glamorous_you.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jemimah.glamorous_you.R;
import com.jemimah.glamorous_you.activity.BusinessDetailsActivity;
import com.jemimah.glamorous_you.model.Business;
import com.jemimah.glamorous_you.model.BusinessService;

import java.util.ArrayList;
import java.util.List;

public class ServicesGridAdapter extends RecyclerView.Adapter<ServicesGridAdapter.ViewHolder> {
    List<BusinessService> businessServices = new ArrayList<>();
    private final Context context;

    public ServicesGridAdapter(Context context, List<BusinessService> businessServices) {
        this.context = context;
        this.businessServices = businessServices;
    }

    @Override
    public ServicesGridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // infalte the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_services, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v); // pass the view to View Holder
    }

    @Override
    public void onBindViewHolder(@NonNull ServicesGridAdapter.ViewHolder holder, int position) {
        BusinessService service = businessServices.get(position);
        holder.txtServiceName.setText(service.getName());
        holder.txtPrice.setText("Ksh. " + service.getPrice());
    }

    @Override
    public int getItemCount() {
        return businessServices.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtServiceName, txtPrice;
        private ViewHolder(View itemView) {
            super(itemView);
            txtServiceName = itemView.findViewById(R.id.txtServiceName);
            txtPrice = itemView.findViewById(R.id.txtPrice);
        }
    }
}

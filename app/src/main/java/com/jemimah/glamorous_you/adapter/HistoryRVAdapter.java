package com.jemimah.glamorous_you.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jemimah.glamorous_you.R;
import com.jemimah.glamorous_you.activity.BookServiceActivity;
import com.jemimah.glamorous_you.activity.CategoryActivity;
import com.jemimah.glamorous_you.model.Appointment;
import com.jemimah.glamorous_you.model.BusinessService;
import com.jemimah.glamorous_you.model.Service;

import java.util.ArrayList;
import java.util.List;

public class HistoryRVAdapter extends RecyclerView.Adapter<HistoryRVAdapter.ViewHolder> {
    private static final String TAG = "HistoryRVAdapter";
    private List<Appointment> appointments = new ArrayList<>();
    private final Context context;

    public HistoryRVAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public HistoryRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_appointments, parent, false);
        return new HistoryRVAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryRVAdapter.ViewHolder holder, int position) {
        Appointment appointment = appointments.get(position);
        List<BusinessService> services = appointment.getServices();
        List<String> serviceNames = new ArrayList<>();
        for (BusinessService businessService: services) {
            serviceNames.add(businessService.getName());
        }
        String selectedServiceNames = TextUtils.join(", ", serviceNames);
        holder.txtName.setText(appointment.getBusiness().getName() + " - " + selectedServiceNames);
        String status = appointment.getStatus();
        holder.status.setText(status);
        holder.txtClients.setText((appointment.getNo_of_adults() + appointment.getNo_of_children()) + " Clients");
        holder.txtDate.setText("on " + appointment.getAppointment_date() + " " + appointment.getAppointment_time());

        if (status.equals("Completed")) {
            holder.btnReschedule.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);
        } else if (status.equals("Accepted")) {
            holder.btnReschedule.setVisibility(View.GONE);
        }
        holder.btnReschedule.setOnClickListener(v -> {
            Intent intent = new Intent(context, BookServiceActivity.class);
            intent.putExtra("appointment", appointment);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public void setAppointmentsList(List<Appointment> appointments) {
        this.appointments = appointments;
        Log.d(TAG, "setAppointmentsList: " + appointments);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtName, status, txtDate, txtClients;
        private final Button btnReschedule, btnCancel;

        private ViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            status = itemView.findViewById(R.id.status);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtClients = itemView.findViewById(R.id.txtClients);
            btnReschedule = itemView.findViewById(R.id.btnReschedule);
            btnCancel = itemView.findViewById(R.id.btnCancel);
        }
    }
}

package com.jemimah.glamorous_you.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jemimah.glamorous_you.R;
import com.jemimah.glamorous_you.activity.CategoryActivity;
import com.jemimah.glamorous_you.model.Service;

import java.util.ArrayList;
import java.util.List;

public class CategoriesRVAdapter  extends RecyclerView.Adapter<CategoriesRVAdapter.ViewHolder> {
    private static final String TAG = "CategoriesRVAdapter";
    private List<Service> services = new ArrayList<>();
    private final Context context;

    public CategoriesRVAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public CategoriesRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_categories, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesRVAdapter.ViewHolder holder, int position) {
        Service service = services.get(position);
        int id = context.getResources().getIdentifier(service.getImage(), "drawable", context.getPackageName());
        holder.icon.setImageResource(id);
        holder.txtName.setText(service.getName());
        holder.category_layout.setOnClickListener(v -> {
            Intent intent = new Intent(context, CategoryActivity.class);
            intent.putExtra("category", service);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    public void setServicesList(List<Service> services) {
        this.services = services;
        Log.d(TAG, "setServicesList: " + services);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout category_layout;
        private final TextView txtName;
        private final ImageView icon;

        private ViewHolder(View itemView){
            super(itemView);
            category_layout = itemView.findViewById(R.id.category_layout);
            txtName = itemView.findViewById(R.id.txtName);
            icon = itemView.findViewById(R.id.icon);
        }
    }
}

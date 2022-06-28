package com.jemimah.glamorous_you.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jemimah.glamorous_you.R;
import com.jemimah.glamorous_you.activity.BusinessDetailsActivity;
import com.jemimah.glamorous_you.model.Business;

import java.util.ArrayList;
import java.util.List;

public class BusinessesRVAdapter extends RecyclerView.Adapter<BusinessesRVAdapter.ViewHolder> {
    private static final String TAG = "BusinessesRVAdapter";
    private List<Business> businesses = new ArrayList<>();
    private List<Business> allBusinesses;
    private final Context context;

    public BusinessesRVAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public BusinessesRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_businesses, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusinessesRVAdapter.ViewHolder holder, int position) {
        Business business = businesses.get(position);
        holder.txtName.setText(business.getName());
        holder.owner_name.setText("By " + business.getOwner().getFirstName() + " " + business.getOwner().getSurname());
        holder.txtLocation.setText("Location: " + business.getCounty().getName());
        holder.businessLayout.setOnClickListener(v -> {
            Intent intent = new Intent(context, BusinessDetailsActivity.class);
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
        allBusinesses = new ArrayList<>(businesses);
        Log.d(TAG, "setBusinessesList: " + businesses);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtName, owner_name, txtLocation;
        private final LinearLayout businessLayout;

        private ViewHolder(View itemView) {
            super(itemView);
            owner_name = itemView.findViewById(R.id.owner_name);
            txtName = itemView.findViewById(R.id.txtName);
            txtLocation = itemView.findViewById(R.id.txtLocation);
            businessLayout = itemView.findViewById(R.id.businessLayout);
        }
    }

    public Filter getFilter() {
        return BusinessFilter;
    }

    private Filter BusinessFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Business> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(allBusinesses);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Business item : allBusinesses) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            businesses.clear();
            businesses.addAll((ArrayList) results.values);
            notifyDataSetChanged();

        }
    };
}

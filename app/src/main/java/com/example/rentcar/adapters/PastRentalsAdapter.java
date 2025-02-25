package com.example.rentcar.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rentcar.R;
import com.example.rentcar.db.models.Rental;
import com.example.rentcar.db.models.Vehicle;

import java.util.List;

public class PastRentalsAdapter extends RecyclerView.Adapter<PastRentalsAdapter.PastRentalViewHolder> {

    private Context context;
    private List<Rental> pastRentals;

    public PastRentalsAdapter(Context context, List<Rental> pastRentals) {
        this.context = context;
        this.pastRentals = pastRentals;
    }

    @NonNull
    @Override
    public PastRentalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_past_rental, parent, false);
        return new PastRentalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PastRentalViewHolder holder, int position) {
        Rental rental = pastRentals.get(position);

        // Bind data to views
        holder.textViewModel.setText(String.format("Model: %s", rental.getVehicleModel() != null ? rental.getVehicleModel() : "N/A"));
        holder.textViewRentalDates.setText(String.format("Dates: %s to %s", rental.getStartDate(), rental.getEndDate()));
        holder.textViewTotalCost.setText(String.format("Total Cost: $%.2f", rental.getTotalCost()));
    }

    @Override
    public int getItemCount() {
        return pastRentals.size();
    }

    public static class PastRentalViewHolder extends RecyclerView.ViewHolder {
        TextView textViewModel, textViewRentalDates, textViewTotalCost;
        Button buttonReview;

        public PastRentalViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewModel = itemView.findViewById(R.id.textViewModel);
            textViewRentalDates = itemView.findViewById(R.id.textViewRentalDates);
            textViewTotalCost = itemView.findViewById(R.id.textViewTotalCost);
            buttonReview = itemView.findViewById(R.id.buttonReview);
        }
    }
}
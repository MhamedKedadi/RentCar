package com.example.rentcar.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rentcar.R;
import com.example.rentcar.activities.RentalDetailActivity;
import com.example.rentcar.db.models.Vehicle;

import java.io.Serializable;
import java.util.List;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder> {

    private Context context;
    private List<Vehicle> vehicleList;
    private int userId;

    public VehicleAdapter(Context context, List<Vehicle> vehicleList, int userId) {
        this.context = context;
        this.vehicleList = vehicleList;
        this.userId = userId;
    }

    @NonNull
    @Override
    public VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_vehicle, parent, false);
        return new VehicleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleViewHolder holder, int position) {
        Vehicle vehicle = vehicleList.get(position);

        // Bind data to views
        holder.textViewModel.setText(String.format("Model: %s", vehicle.getModel()));
        holder.textViewBrand.setText(String.format("Brand: %s", vehicle.getBrand()));
        holder.textViewDailyRate.setText(String.format("Daily Rate: $%s", vehicle.getDailyRate()));

        // Handle Rent button click
        holder.buttonRent.setOnClickListener(v -> {
            Intent intent = new Intent(context, RentalDetailActivity.class);
            intent.putExtra("vehicle", vehicle);
            intent.putExtra("userId", userId);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return vehicleList.size();
    }

    public static class VehicleViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewVehicle;
        TextView textViewModel, textViewBrand, textViewDailyRate;
        Button buttonRent;

        public VehicleViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewVehicle = itemView.findViewById(R.id.imageViewVehicle);
            textViewModel = itemView.findViewById(R.id.textViewModel);
            textViewBrand = itemView.findViewById(R.id.textViewBrand);
            textViewDailyRate = itemView.findViewById(R.id.textViewDailyRate);
            buttonRent = itemView.findViewById(R.id.buttonRent);
        }
    }
}
package com.example.rentcar.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rentcar.R;
import com.example.rentcar.activities.AdminActivity;
import com.example.rentcar.db.DbHelper;
import com.example.rentcar.db.models.Vehicle;

import java.util.List;

public class AdminVehicleAdapter extends RecyclerView.Adapter<AdminVehicleAdapter.VehicleViewHolder> {

    private Context context;
    private List<Vehicle> vehicles;
    private DbHelper dbHelper;

    public AdminVehicleAdapter(Context context, List<Vehicle> vehicles, DbHelper dbHelper) {
        this.context = context;
        this.vehicles = vehicles;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_vehicle, parent, false);
        return new VehicleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleViewHolder holder, int position) {
        Vehicle vehicle = vehicles.get(position);

        // Bind data to views
        holder.textViewModel.setText(String.format("Model: %s", vehicle.getModel()));
        holder.textViewBrand.setText(String.format("Brand: %s", vehicle.getBrand()));

        // Edit Button Click Listener
        holder.buttonEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, AdminActivity.class);
            intent.putExtra("isEditing", true);
            intent.putExtra("vehicle", vehicle);
            context.startActivity(intent);
        });

        // Delete Button Click Listener
        holder.buttonDelete.setOnClickListener(v -> {
            // Delete the vehicle from the database
            dbHelper.deleteVehicle(vehicle.getId());
            vehicles.remove(position);
            notifyItemRemoved(position);
            Toast.makeText(context, "Vehicle deleted", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return vehicles.size();
    }

    public static class VehicleViewHolder extends RecyclerView.ViewHolder {
        TextView textViewModel, textViewBrand;
        Button buttonEdit, buttonDelete;

        public VehicleViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewModel = itemView.findViewById(R.id.textViewModel);
            textViewBrand = itemView.findViewById(R.id.textViewBrand);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}
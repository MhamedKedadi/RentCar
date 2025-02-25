package com.example.rentcar.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rentcar.R;
import com.example.rentcar.adapters.PastRentalsAdapter;
import com.example.rentcar.db.DbHelper;
import com.example.rentcar.db.models.Rental;
import com.example.rentcar.db.models.Vehicle;

import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PastRentalsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewPastRentals;
    private PastRentalsAdapter pastRentalsAdapter;
    private DbHelper dbHelper;
    private int userId; // ID of the logged-in user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_rentals);

        // Initialize views
        recyclerViewPastRentals = findViewById(R.id.recyclerViewPastRentals);

        // Get logged-in user ID (you can pass this from the previous activity)
        userId = getIntent().getIntExtra("userId", -1);
        if (userId == -1) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize database helper
        dbHelper = new DbHelper(this);

        // Fetch past rentals for the user
        List<Rental> pastRentals = dbHelper.getUserRentals(userId);

        // Set the vehicle model for each rental
        for (Rental rental : pastRentals) {
            Vehicle vehicle = dbHelper.getVehicle(rental.getVehicleId());
            if (vehicle != null) {
                rental.setVehicleModel(vehicle.getModel());
            } else {
                rental.setVehicleModel("N/A"); // Handle cases where the vehicle is not found
            }
        }

        // Set up RecyclerView
        pastRentalsAdapter = new PastRentalsAdapter(this, pastRentals);
        recyclerViewPastRentals.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPastRentals.setAdapter(pastRentalsAdapter);
    }
}
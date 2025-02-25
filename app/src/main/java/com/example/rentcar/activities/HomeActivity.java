package com.example.rentcar.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rentcar.R;
import com.example.rentcar.adapters.VehicleAdapter;
import com.example.rentcar.db.DbHelper;
import com.example.rentcar.db.models.User;
import com.example.rentcar.db.models.Vehicle;

import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private RecyclerView recyclerViewVehicles;
    private Button buttonPreviousRentals, buttonAdminDashboard;
    private DbHelper dbHelper;
    private int userId;
    private VehicleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recyclerViewVehicles = findViewById(R.id.recyclerViewVehicles);
        buttonPreviousRentals = findViewById(R.id.buttonPreviousRentals);
        buttonAdminDashboard = findViewById(R.id.buttonAdminDashboard);

        dbHelper = new DbHelper(this);
        userId = getIntent().getIntExtra("userId", -1);

        if (userId == -1) {
            // User not logged in
            finish();
            return;
        }

        User user = dbHelper.getUserById(userId);

        // Check if the user is an admin
        if (user.isAdmin()) {
            buttonAdminDashboard.setVisibility(View.VISIBLE);
        }

        // Load available vehicles
        loadAvailableVehicles(user.getId());

        // Button click listeners
        buttonPreviousRentals.setOnClickListener(v -> {
            Intent intent = new Intent(this, PastRentalsActivity.class);
            intent.putExtra("userId", user.getId());
            startActivity(intent);
        });

        buttonAdminDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list of available vehicles when the activity resumes
        loadAvailableVehicles(userId);
    }

    private void loadAvailableVehicles(int userId) {
        List<Vehicle> vehicles = dbHelper.getAvailableVehicles();
        adapter = new VehicleAdapter(this, vehicles, userId);
        recyclerViewVehicles.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewVehicles.setAdapter(adapter);
    }
}
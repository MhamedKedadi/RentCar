package com.example.rentcar.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rentcar.R;
import com.example.rentcar.adapters.AdminVehicleAdapter;
import com.example.rentcar.db.DbHelper;
import com.example.rentcar.db.models.Vehicle;

import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private EditText editTextType, editTextModel, editTextBrand, editTextYear, editTextLicensePlate, editTextDailyRate, editTextImageUrl;
    private Button buttonSave;
    private RecyclerView recyclerViewVehicles;
    private DbHelper dbHelper;
    private AdminVehicleAdapter adapter;
    private boolean isEditing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Initialize views
        editTextType = findViewById(R.id.editTextType);
        editTextModel = findViewById(R.id.editTextModel);
        editTextBrand = findViewById(R.id.editTextBrand);
        editTextYear = findViewById(R.id.editTextYear);
        editTextLicensePlate = findViewById(R.id.editTextLicensePlate);
        editTextDailyRate = findViewById(R.id.editTextDailyRate);
        editTextImageUrl = findViewById(R.id.editTextImageUrl);
        buttonSave = findViewById(R.id.buttonSave);
        recyclerViewVehicles = findViewById(R.id.recyclerViewVehicles);

        isEditing = getIntent().getBooleanExtra("isEditing", false);

        if (isEditing) {
            buttonSave.setText("Update");
            Vehicle vehicle = (Vehicle) getIntent().getSerializableExtra("vehicle");
            editTextType.setText(vehicle.getType());
            editTextModel.setText(vehicle.getModel());
            editTextBrand.setText(vehicle.getBrand());
            editTextYear.setText(String.valueOf(vehicle.getYear()));
            editTextLicensePlate.setText(vehicle.getLicensePlate());
            editTextDailyRate.setText(String.valueOf(vehicle.getDailyRate()));
            editTextImageUrl.setText(vehicle.getImageUrl());
        }

        // Initialize database helper
        dbHelper = new DbHelper(this);

        // Load existing vehicles
        loadVehicles();

        // Save Button Click Listener
        buttonSave.setOnClickListener(v -> {
            if (isEditing) {
                updateVehicle();
            } else {
                saveVehicle();
            }
        });
    }

    private void saveVehicle() {
        String type = editTextType.getText().toString().trim();
        String model = editTextModel.getText().toString().trim();
        String brand = editTextBrand.getText().toString().trim();
        int year = Integer.parseInt(editTextYear.getText().toString().trim());
        String licensePlate = editTextLicensePlate.getText().toString().trim();
        double dailyRate = Double.parseDouble(editTextDailyRate.getText().toString().trim());
        String imageUrl = editTextImageUrl.getText().toString().trim();

        // Create a new vehicle
        Vehicle vehicle = new Vehicle(0, type, model, brand, year, licensePlate, dailyRate, true, imageUrl);

        // Save the vehicle to the database
        long id = dbHelper.addVehicle(vehicle);
        if (id != -1) {
            Toast.makeText(this, "Vehicle saved successfully!", Toast.LENGTH_SHORT).show();
            clearForm();
            loadVehicles(); // Refresh the list
        } else {
            Toast.makeText(this, "Failed to save vehicle", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateVehicle() {
        String type = editTextType.getText().toString().trim();
        String model = editTextModel.getText().toString().trim();
        String brand = editTextBrand.getText().toString().trim();
        int year = Integer.parseInt(editTextYear.getText().toString().trim());
        String licensePlate = editTextLicensePlate.getText().toString().trim();
        double dailyRate = Double.parseDouble(editTextDailyRate.getText().toString().trim());
        String imageUrl = editTextImageUrl.getText().toString().trim();

        Vehicle vehicle = (Vehicle) getIntent().getSerializableExtra("vehicle");
        vehicle.setType(type);
        vehicle.setModel(model);
        vehicle.setBrand(brand);
        vehicle.setYear(year);
        vehicle.setLicensePlate(licensePlate);
        vehicle.setDailyRate(dailyRate);
        vehicle.setImageUrl(imageUrl);

        // Update the vehicle in the database
        int rowsAffected = dbHelper.updateVehicle(vehicle);
        if (rowsAffected > 0) {
            Toast.makeText(this, "Vehicle updated successfully!", Toast.LENGTH_SHORT).show();
            clearForm();
            loadVehicles(); // Refresh the list
        } else {
            Toast.makeText(this, "Failed to update vehicle", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadVehicles() {
        List<Vehicle> vehicles = dbHelper.getAllVehicles();
        adapter = new AdminVehicleAdapter(this, vehicles, dbHelper);
        recyclerViewVehicles.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewVehicles.setAdapter(adapter);
    }

    private void clearForm() {
        editTextType.setText("");
        editTextModel.setText("");
        editTextBrand.setText("");
        editTextYear.setText("");
        editTextLicensePlate.setText("");
        editTextDailyRate.setText("");
        editTextImageUrl.setText("");
    }
}
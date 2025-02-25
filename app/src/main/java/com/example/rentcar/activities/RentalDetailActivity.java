package com.example.rentcar.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rentcar.R;
import com.example.rentcar.db.DbHelper;
import com.example.rentcar.db.models.Rental;
import com.example.rentcar.db.models.Vehicle;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RentalDetailActivity extends AppCompatActivity {

    private ImageView imageViewVehicle;
    private TextView textViewModel, textViewBrand, textViewDailyRate, textViewTotalCost;
    private Button buttonStartDate, buttonEndDate, buttonRent;

    private double dailyRate;
    private Date startDate, endDate;
    private DbHelper dbHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rental_detail);
        dbHelper = new DbHelper(this);
        userId = getIntent().getIntExtra("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initialize views
        imageViewVehicle = findViewById(R.id.imageViewVehicle);
        textViewModel = findViewById(R.id.textViewModel);
        textViewBrand = findViewById(R.id.textViewBrand);
        textViewDailyRate = findViewById(R.id.textViewDailyRate);
        textViewTotalCost = findViewById(R.id.textViewTotalCost);
        buttonStartDate = findViewById(R.id.buttonStartDate);
        buttonEndDate = findViewById(R.id.buttonEndDate);
        buttonRent = findViewById(R.id.buttonRent);

        // Get vehicle data from intent
        Vehicle vehicle = (Vehicle) getIntent().getSerializableExtra("vehicle");
        if (vehicle != null) {
            textViewModel.setText(String.format("Model: %s", vehicle.getModel()));
            textViewBrand.setText(String.format("Brand: %s", vehicle.getBrand()));
            textViewDailyRate.setText(String.format("Daily Rate: $%.2f", vehicle.getDailyRate()));
            dailyRate = vehicle.getDailyRate();
        }

        // Start Date Picker
        buttonStartDate.setOnClickListener(v -> showDatePicker(true));

        // End Date Picker
        buttonEndDate.setOnClickListener(v -> showDatePicker(false));

        // Rent Button
        buttonRent.setOnClickListener(v -> {
            if (startDate == null || endDate == null) {
                Toast.makeText(this, "Please select start and end dates", Toast.LENGTH_SHORT).show();
            } else if (endDate.before(startDate)) {
                Toast.makeText(this, "End date must be after start date", Toast.LENGTH_SHORT).show();
            } else {
                // Format the dates to "yyyy-MM-dd" (date only, no time)
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String formattedStartDate = sdf.format(startDate);
                String formattedEndDate = sdf.format(endDate);

                // Calculate total cost
                long diff = endDate.getTime() - startDate.getTime();
                long days = diff / (24 * 60 * 60 * 1000); // Convert milliseconds to days
                double totalCost = days * dailyRate;

                // Create and save the rental
                Rental rental = new Rental(userId, vehicle.getId(), formattedStartDate, formattedEndDate, totalCost);
                dbHelper.createRental(rental);

                // Update vehicle availability
                dbHelper.updateVehicleAvailability(vehicle.getId(), false);

                Toast.makeText(this, "Rental confirmed!", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity after rental is confirmed
            }
        });
    }

    private void showDatePicker(boolean isStartDate) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String dateString = String.format(Locale.getDefault(), "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        Date selectedDate = sdf.parse(dateString);
                        Calendar selectedCalendar = Calendar.getInstance();
                        selectedCalendar.set(selectedYear, selectedMonth, selectedDay);

                        Calendar currentCalendar = Calendar.getInstance();
                        currentCalendar.set(Calendar.HOUR_OF_DAY, 0);
                        currentCalendar.set(Calendar.MINUTE, 0);
                        currentCalendar.set(Calendar.SECOND, 0);
                        currentCalendar.set(Calendar.MILLISECOND, 0);


                        if (selectedCalendar.before(currentCalendar)) {
                            Toast.makeText(this, "Please select a future date", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (isStartDate) {
                            startDate = selectedDate;
                            buttonStartDate.setText(dateString);
                        } else {
                            endDate = selectedDate;
                            buttonEndDate.setText(dateString);
                        }

                        // Update total cost
                        updateTotalCost();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    private void updateTotalCost() {
        if (startDate != null && endDate != null) {
            long diff = endDate.getTime() - startDate.getTime();
            long days = diff / (24 * 60 * 60 * 1000); // Convert milliseconds to days
            double totalCost = days * dailyRate;
            textViewTotalCost.setText(String.format(Locale.getDefault(), "Total Cost: $%.2f", totalCost));
        }
    }
}
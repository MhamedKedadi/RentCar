package com.example.rentcar.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.rentcar.db.models.Rental;
import com.example.rentcar.db.models.User;
import com.example.rentcar.db.models.Vehicle;

import java.util.ArrayList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {

    // Database name and version
    private static final String DATABASE_NAME = "rent.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_VEHICLES = "vehicles";
    private static final String TABLE_RENTALS = "rentals";

    // Common column names
    private static final String KEY_ID = "id";

    // Users table columns
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_IS_ADMIN = "isAdmin";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";

    // Vehicles table columns
    private static final String KEY_TYPE = "type";
    private static final String KEY_MODEL = "model";
    private static final String KEY_BRAND = "brand";
    private static final String KEY_YEAR = "year";
    private static final String KEY_LICENSE_PLATE = "licensePlate";
    private static final String KEY_DAILY_RATE = "dailyRate";
    private static final String KEY_IS_AVAILABLE = "isAvailable";
    private static final String KEY_IMAGE_URL = "imageUrl";

    // Rentals table columns
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_VEHICLE_ID = "vehicleId";
    private static final String KEY_START_DATE = "startDate";
    private static final String KEY_END_DATE = "endDate";
    private static final String KEY_TOTAL_COST = "totalCost";
    private static final String KEY_STATUS = "status";

    // Constructor
    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table
        String CREATE_USERS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_USERS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_USERNAME + " TEXT NOT NULL,"
                + KEY_PASSWORD + " TEXT NOT NULL,"
                + KEY_IS_ADMIN + " INTEGER DEFAULT 0,"
                + KEY_EMAIL + " TEXT,"
                + KEY_PHONE + " TEXT"
                + ")";
        db.execSQL(CREATE_USERS_TABLE);

        // Create vehicles table
        String CREATE_VEHICLES_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_VEHICLES + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_TYPE + " TEXT NOT NULL,"
                + KEY_MODEL + " TEXT NOT NULL,"
                + KEY_BRAND + " TEXT NOT NULL,"
                + KEY_YEAR + " INTEGER,"
                + KEY_LICENSE_PLATE + " TEXT UNIQUE,"
                + KEY_DAILY_RATE + " REAL NOT NULL,"
                + KEY_IS_AVAILABLE + " INTEGER DEFAULT 1,"
                + KEY_IMAGE_URL + " TEXT"
                + ")";
        db.execSQL(CREATE_VEHICLES_TABLE);

        // Create rentals table
        String CREATE_RENTALS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_RENTALS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_USER_ID + " INTEGER,"
                + KEY_VEHICLE_ID + " INTEGER,"
                + KEY_START_DATE + " TEXT NOT NULL,"
                + KEY_END_DATE + " TEXT NOT NULL,"
                + KEY_TOTAL_COST + " REAL NOT NULL,"
                + KEY_STATUS + " TEXT DEFAULT 'pending',"
                + "FOREIGN KEY(" + KEY_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + KEY_ID + "),"
                + "FOREIGN KEY(" + KEY_VEHICLE_ID + ") REFERENCES " + TABLE_VEHICLES + "(" + KEY_ID + ")"
                + ")";
        db.execSQL(CREATE_RENTALS_TABLE);

        // Insert default admin user
        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, "admin");
        values.put(KEY_PASSWORD, "admin");
        values.put(KEY_IS_ADMIN, 1);
        db.insert(TABLE_USERS, null, values);

        // Insert default vehicles
        values.clear();
        values.put(KEY_TYPE, "Sedan");
        values.put(KEY_MODEL, "Corolla");
        values.put(KEY_BRAND, "Toyota");
        values.put(KEY_YEAR, 2021);
        values.put(KEY_LICENSE_PLATE, "ABC123");
        values.put(KEY_DAILY_RATE, 50.0);
        values.put(KEY_IMAGE_URL, "https://example.com/image.jpg");
        db.insert(TABLE_VEHICLES, null, values);

        values.clear();
        values.put(KEY_TYPE, "SUV");
        values.put(KEY_MODEL, "CR-V");
        values.put(KEY_BRAND, "Honda");
        values.put(KEY_YEAR, 2020);
        values.put(KEY_LICENSE_PLATE, "XYZ456");
        values.put(KEY_DAILY_RATE, 60.0);
        values.put(KEY_IMAGE_URL, "https://example.com/image.jpg");
        db.insert(TABLE_VEHICLES, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if they exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VEHICLES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RENTALS);

        // Create tables again
        onCreate(db);
    }

    public long addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, user.getUsername());
        values.put(KEY_PASSWORD, user.getPassword());

        long id = db.insert(TABLE_USERS, null, values);
        db.close();
        return id;
    }

    public User getUserByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, KEY_USERNAME + " = ?", new String[]{username}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            User user = new User(
                    cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_USERNAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_PASSWORD)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_PHONE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(KEY_IS_ADMIN)) == 1
            );
            cursor.close();
            return user;
        }
        return null;
    }

    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, KEY_ID + " = ?", new String[]{String.valueOf(userId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            User user = new User(
                    cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_USERNAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_PASSWORD)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_PHONE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(KEY_IS_ADMIN)) == 1
            );
            cursor.close();
            return user;
        }
        return null;
    }

    public User loginUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, KEY_USERNAME + " = ? AND " + KEY_PASSWORD + " = ?",
                new String[]{username, password}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            User user = new User(
                    cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_USERNAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_PASSWORD)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_PHONE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(KEY_IS_ADMIN)) == 1
            );
            cursor.close();
            return user;
        }
        return null;
    }

    public long addVehicle(Vehicle vehicle) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TYPE, vehicle.getType());
        values.put(KEY_MODEL, vehicle.getModel());
        values.put(KEY_BRAND, vehicle.getBrand());
        values.put(KEY_YEAR, vehicle.getYear());
        values.put(KEY_LICENSE_PLATE, vehicle.getLicensePlate());
        values.put(KEY_DAILY_RATE, vehicle.getDailyRate());
        values.put(KEY_IMAGE_URL, vehicle.getImageUrl());

        long id = db.insert(TABLE_VEHICLES, null, values);
        db.close();
        return id;
    }

    public int updateVehicle(Vehicle vehicle) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TYPE, vehicle.getType());
        values.put(KEY_MODEL, vehicle.getModel());
        values.put(KEY_BRAND, vehicle.getBrand());
        values.put(KEY_YEAR, vehicle.getYear());
        values.put(KEY_LICENSE_PLATE, vehicle.getLicensePlate());
        values.put(KEY_DAILY_RATE, vehicle.getDailyRate());
        values.put(KEY_IMAGE_URL, vehicle.getImageUrl());

        return db.update(TABLE_VEHICLES, values, KEY_ID + " = ?", new String[]{String.valueOf(vehicle.getId())});
    }

    public int deleteVehicle(int vehicleId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_VEHICLES, KEY_ID + " = ?", new String[]{String.valueOf(vehicleId)});
    }

    public List<Vehicle> getAllVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_VEHICLES, null);

        if (cursor.moveToFirst()) {
            do {
                Vehicle vehicle = new Vehicle(
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_TYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_MODEL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_BRAND)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_YEAR)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_LICENSE_PLATE)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_DAILY_RATE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_IS_AVAILABLE)) == 1,
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_IMAGE_URL))
                );
                vehicles.add(vehicle);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return vehicles;
    }

    public List<Vehicle> getAvailableVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_VEHICLES + " WHERE " + KEY_IS_AVAILABLE + " = 1", null);

        if (cursor.moveToFirst()) {
            do {
                Vehicle vehicle = new Vehicle(
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_TYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_MODEL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_BRAND)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_YEAR)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_LICENSE_PLATE)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_DAILY_RATE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_IS_AVAILABLE)) == 1,
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_IMAGE_URL))
                );
                vehicles.add(vehicle);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return vehicles;
    }

    public Vehicle getVehicle(int vehicleId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_VEHICLES, null, KEY_ID + " = ?", new String[]{String.valueOf(vehicleId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Vehicle vehicle = new Vehicle(
                    cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_TYPE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_MODEL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_BRAND)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(KEY_YEAR)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_LICENSE_PLATE)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_DAILY_RATE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(KEY_IS_AVAILABLE)) == 1,
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_IMAGE_URL))
            );
            cursor.close();
            return vehicle;
        }
        return null;
    }

    public int updateVehicleAvailability(int vehicleId, boolean isAvailable) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_IS_AVAILABLE, isAvailable ? 1 : 0);

        return db.update(TABLE_VEHICLES, values, KEY_ID + " = ?", new String[]{String.valueOf(vehicleId)});
    }

    public long createRental(Rental rental) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, rental.getUserId());
        values.put(KEY_VEHICLE_ID, rental.getVehicleId());
        values.put(KEY_START_DATE, rental.getStartDate());
        values.put(KEY_END_DATE, rental.getEndDate());
        values.put(KEY_TOTAL_COST, rental.getTotalCost());
        values.put(KEY_STATUS, "pending");

        long id = db.insert(TABLE_RENTALS, null, values);
        db.close();
        return id;
    }

    public List<Rental> getUserRentals(int userId) {
        List<Rental> rentals = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM rentals WHERE userId = ?",
                new String[]{String.valueOf(userId)}
        );

        if (cursor.moveToFirst()) {
            do {
                Rental rental = new Rental(
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_USER_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_VEHICLE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_START_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_END_DATE)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_TOTAL_COST)), // Ensure this line is present
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_STATUS))
                );
                rentals.add(rental);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return rentals;
    }

    public int updateRentalStatus(int rentalId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_STATUS, status);

        return db.update(TABLE_RENTALS, values, KEY_ID + " = ?", new String[]{String.valueOf(rentalId)});
    }
}
package com.example.rentcar.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rentcar.R;
import com.example.rentcar.db.DbHelper;
import com.example.rentcar.db.models.User;

public class LoginActivity extends AppCompatActivity {
    private EditText username, password;
    private Button login, signUp;
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DbHelper(this);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

        login = findViewById(R.id.loginButton);
        signUp = findViewById(R.id.signupButton);

        login.setOnClickListener(v -> {
            String usernameText = username.getText().toString();
            String passwordText = password.getText().toString();

            User user = dbHelper.loginUser(usernameText, passwordText);

            if (user != null) {
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                intent.putExtra("userId", user.getId());
                startActivity(intent);
            } else {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            }
        });


        signUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }
}
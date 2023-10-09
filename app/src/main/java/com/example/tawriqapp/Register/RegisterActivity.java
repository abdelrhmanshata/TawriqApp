package com.example.tawriqapp.Register;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tawriqapp.Activity.HomeActivity;
import com.example.tawriqapp.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {

    ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnSignIN.setOnClickListener(v -> {
            {
                startActivity(new Intent(RegisterActivity.this, Sign_IN_Activity.class));
            }
        });

        binding.btnSignUP.setOnClickListener(v -> {
            {
                startActivity(new Intent(RegisterActivity.this, Sign_UP_Activity.class));
            }
        });

        binding.registerGuest.setOnClickListener(v -> {
            {
                startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
            }
        });
    }
}
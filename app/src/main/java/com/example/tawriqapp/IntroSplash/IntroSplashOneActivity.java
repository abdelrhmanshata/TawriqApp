package com.example.tawriqapp.IntroSplash;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.tawriqapp.Register.RegisterActivity;
import com.example.tawriqapp.databinding.ActivityIntroSplashOneBinding;

public class IntroSplashOneActivity extends AppCompatActivity {

    ActivityIntroSplashOneBinding splashOneBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        splashOneBinding = ActivityIntroSplashOneBinding.inflate(getLayoutInflater());
        setContentView(splashOneBinding.getRoot());

        splashOneBinding.skipButton.setOnClickListener(v -> {
            startActivity(new Intent(IntroSplashOneActivity.this, RegisterActivity.class));
            setFirstTime(false);
            ActivityCompat.finishAffinity(this);
        });

        splashOneBinding.nextButton.setOnClickListener(v ->
                {
                    startActivity(new Intent(IntroSplashOneActivity.this, IntroSplashTwoActivity.class));
                }
        );
    }

    public void setFirstTime(boolean FirstTime) {
        SharedPreferences sp = getSharedPreferences("FirstTime", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("FirstTime", FirstTime);
        editor.apply();
    }
}
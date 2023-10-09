package com.example.tawriqapp.IntroSplash;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.tawriqapp.Register.RegisterActivity;
import com.example.tawriqapp.databinding.ActivityIntroSplashThreeBinding;

public class IntroSplashThreeActivity extends AppCompatActivity {
    ActivityIntroSplashThreeBinding splashThreeBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        splashThreeBinding = ActivityIntroSplashThreeBinding.inflate(getLayoutInflater());
        setContentView(splashThreeBinding.getRoot());

        splashThreeBinding.skipButton.setOnClickListener(v -> {
            startActivity(new Intent(IntroSplashThreeActivity.this, RegisterActivity.class));
            setFirstTime(false);
            ActivityCompat.finishAffinity(this);
        });

        splashThreeBinding.nextButton.setOnClickListener(v -> {
                    startActivity(new Intent(IntroSplashThreeActivity.this, RegisterActivity.class));
                    setFirstTime(false);
                    ActivityCompat.finishAffinity(this);
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
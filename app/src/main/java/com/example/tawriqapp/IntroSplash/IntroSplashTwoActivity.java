package com.example.tawriqapp.IntroSplash;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.tawriqapp.Register.RegisterActivity;
import com.example.tawriqapp.databinding.ActivityIntroSplashTwoBinding;

public class IntroSplashTwoActivity extends AppCompatActivity {

    ActivityIntroSplashTwoBinding splashTwoBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        splashTwoBinding = ActivityIntroSplashTwoBinding.inflate(getLayoutInflater());
        setContentView(splashTwoBinding.getRoot());

        splashTwoBinding.skipButton.setOnClickListener(v -> {
            startActivity(new Intent(IntroSplashTwoActivity.this, RegisterActivity.class));
            setFirstTime(false);
            ActivityCompat.finishAffinity(this);
        });

        splashTwoBinding.nextButton.setOnClickListener(v ->
                {
                    startActivity(new Intent(IntroSplashTwoActivity.this, IntroSplashThreeActivity.class));
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
package com.example.tawriqapp.IntroSplash;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.tawriqapp.Activity.HomeActivity;
import com.example.tawriqapp.R;
import com.example.tawriqapp.Register.RegisterActivity;
import com.example.tawriqapp.databinding.ActivitySplashScreenBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {

    private final static int SPLASH_DISPLAY_LENGTH = 2000; //change time

    Animation Bottom_Top, Top_Bottom;
    ActivitySplashScreenBinding splashScreenBinding;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        splashScreenBinding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(splashScreenBinding.getRoot());

        // Initialize all variables in this page
        Initialize_Variables();

        Bottom_Top = AnimationUtils.loadAnimation(this, R.anim.bottom_to_top);
        Top_Bottom = AnimationUtils.loadAnimation(this, R.anim.top_to_bottom);

        splashScreenBinding.topLayout.setAnimation(Top_Bottom);
        splashScreenBinding.bottomLayout.setAnimation(Bottom_Top);

        new Handler().postDelayed(() -> {
            /* Create an Intent that will start the Home-Activity. */
            if (isFirstTime()) {
                startActivity(new Intent(SplashScreenActivity.this, IntroSplashOneActivity.class));
            } else {
                if (firebaseUser != null) {
                    startActivity(new Intent(SplashScreenActivity.this, HomeActivity.class));
                } else {
                    startActivity(new Intent(SplashScreenActivity.this, RegisterActivity.class));
                }
            }
            ActivityCompat.finishAffinity(this);
        }, SPLASH_DISPLAY_LENGTH);

    }

    public void Initialize_Variables() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    public boolean isFirstTime() {
        SharedPreferences sp = getSharedPreferences("FirstTime", Activity.MODE_PRIVATE);
        return sp.getBoolean("FirstTime", true);
    }
}
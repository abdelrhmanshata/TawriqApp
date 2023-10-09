package com.example.tawriqapp.Register;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.tawriqapp.Activity.HomeActivity;
import com.example.tawriqapp.Model.Student;
import com.example.tawriqapp.R;
import com.example.tawriqapp.databinding.ActivitySignInBinding;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import java.util.Objects;

public class Sign_IN_Activity extends AppCompatActivity implements Student.Login {

    ActivitySignInBinding binding;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set On Click Listener
        binding.signUP.setOnClickListener(v -> {
            startActivity(new Intent(Sign_IN_Activity.this, Sign_UP_Activity.class));
            finish();
        });

        binding.btnSignIN.setOnClickListener(v -> {
            reviewDataEntered();
        });
    }

    private void reviewDataEntered() {
        // Hide the android keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.mainLayout.getWindowToken(), 0);

        binding.progressCircle.setVisibility(View.VISIBLE);

        String inputEmail = Objects.requireNonNull(binding.inputEmail.getText()).toString().trim();
        if (inputEmail.isEmpty()) {
            binding.inputEmail.setError(getString(R.string.emailIsRequired));
            binding.inputEmail.requestFocus();
            binding.progressCircle.setVisibility(View.INVISIBLE);
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail).matches()) {
            binding.inputEmail.setError(getString(R.string.please_enter_valid_email));
            binding.inputEmail.requestFocus();
            binding.progressCircle.setVisibility(View.INVISIBLE);
            return;
        }

        String inputPassword = Objects.requireNonNull(binding.inputPassword.getText()).toString().trim();
        if (inputPassword.isEmpty()) {
            binding.inputPassword.setError(getString(R.string.passwordIsRequired));
            binding.inputPassword.requestFocus();
            binding.progressCircle.setVisibility(View.INVISIBLE);
            return;
        }
        if (inputPassword.length() < 8) {
            binding.inputPassword.setError(getString(R.string.minimumLength));
            binding.inputPassword.requestFocus();
            binding.progressCircle.setVisibility(View.INVISIBLE);
            return;
        }

        if (inputPassword.contains(" ")) {
            binding.inputPassword.setError(getString(R.string.passwordNotSpace));
            binding.inputPassword.requestFocus();
            binding.progressCircle.setVisibility(View.INVISIBLE);
            return;
        }

        Login(inputEmail, inputPassword);
    }

    @Override
    public void Login(String email, String password) {
        firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        showDialogSuccessfullyLayout(getString(R.string.Sign_in_SuccessfullyText));


                    } else if (task.getException() instanceof FirebaseNetworkException) {
                        showDialogFailedLayout(getString(R.string.noConnection));
                    } else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                        showDialogFailedLayout(getString(R.string.userNotFound));
                    } else if ((task.getException() instanceof FirebaseAuthInvalidCredentialsException)) {
                        showDialogFailedLayout(getString(R.string.passwordIncorrect));
                    } else if (task.getException() instanceof FirebaseTooManyRequestsException) {
                        showDialogFailedLayout(getString(R.string.Enter_password_many_times));
                    } else {
                        showDialogFailedLayout("Error+->" + task.getException());
                    }
                    binding.progressCircle.setVisibility(View.INVISIBLE);
                });
    }

    @SuppressLint("CheckResult")
    public void showDialogSuccessfullyLayout(String msg) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.sign_in_successfully_layout, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        //
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        alertDialog.getWindow().setWindowAnimations(R.style.AnimationForDialog);
        //

        TextView textMsg = dialogView.findViewById(R.id.textMsg);
        textMsg.setText(msg.trim());

        Button skipButton = dialogView.findViewById(R.id.skipButton);
        skipButton.setOnClickListener(v -> {
            startActivity(new Intent(Sign_IN_Activity.this, HomeActivity.class));
            ActivityCompat.finishAffinity(this);
            alertDialog.dismiss();
        });
    }

    @SuppressLint("CheckResult")
    public void showDialogFailedLayout(String msg) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.sign_in_failed_layout, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        alertDialog.getWindow().setWindowAnimations(R.style.AnimationForDialog);

        TextView textMsg = dialogView.findViewById(R.id.textMsg);
        textMsg.setText(msg.trim());

        Button skipButton = dialogView.findViewById(R.id.skipButton);
        skipButton.setOnClickListener(v -> {
            alertDialog.dismiss();
        });
    }

}
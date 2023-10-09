package com.example.tawriqapp.Register;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tawriqapp.Model.Student;
import com.example.tawriqapp.R;
import com.example.tawriqapp.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class Sign_UP_Activity extends AppCompatActivity implements Student.Register {

    ActivitySignUpBinding binding;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceAllStudent = database.getReference("AllStudent");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set On Click Listener

        binding.signIN.setOnClickListener(v -> {
            startActivity(new Intent(Sign_UP_Activity.this, Sign_IN_Activity.class));
            finish();
        });

        binding.btnSignUP.setOnClickListener(v -> {
            reviewDataEntered();
        });
    }

    public void reviewDataEntered() {
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

        String confirmPassword = Objects.requireNonNull(binding.inputConfirmPassword.getText()).toString().trim();
        if (!confirmPassword.equals(inputPassword)) {
            binding.inputConfirmPassword.setError(getString(R.string.passwordDontMatch));
            binding.inputConfirmPassword.requestFocus();
            binding.progressCircle.setVisibility(View.INVISIBLE);
            return;
        }

        String inputFirstName = Objects.requireNonNull(binding.inputFirstName.getText()).toString().trim();
        if (inputFirstName.isEmpty()) {
            binding.inputFirstName.setError(getString(R.string.nameIsRequired));
            binding.inputFirstName.requestFocus();
            binding.progressCircle.setVisibility(View.INVISIBLE);
            return;
        }

        String inputLastName = Objects.requireNonNull(binding.inputLastName.getText()).toString().trim();
        if (inputLastName.isEmpty()) {
            binding.inputLastName.setError(getString(R.string.nameIsRequired));
            binding.inputLastName.requestFocus();
            binding.progressCircle.setVisibility(View.INVISIBLE);
            return;
        }

        String inputPhone = binding.inputPhone.getText().toString();
        if (TextUtils.isEmpty(inputPhone)) {
            binding.inputPhone.setError(getString(R.string.phone_number_is_required));
            binding.inputPhone.requestFocus();
            binding.progressCircle.setVisibility(View.INVISIBLE);
            return;
        }

        // Create Object From Student Class

        Student student = new Student();
        student.setID("");
        student.setEmail(inputEmail);
        student.setPassword(inputPassword);
        student.setFirstName(inputFirstName);
        student.setLastName(inputLastName);
        student.setPhone(inputPhone);
        student.setImageUri("");
        student.setStatus("");
        student.setTypingTo("");
        student.setAboutMe("");

        // Recording Student data into the database
        Register(student);
    }

    @Override
    public void Register(Student student) {
        firebaseAuth
                .createUserWithEmailAndPassword(student.getEmail(), student.getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser CurrentUser = firebaseAuth.getCurrentUser();
                        student.setID(CurrentUser.getUid());
                        student.setImageUri("https://firebasestorage.googleapis.com/v0/b/tawriq-project.appspot.com/o/user_m.png?alt=media&token=77c6adf3-b0be-4638-9e7d-871715018c2b");

                        // Upload Student In Database
                        referenceAllStudent
                                .child(student.getID())
                                .setValue(student)
                                .addOnSuccessListener(unused -> {
                                    binding.progressCircle.setVisibility(View.INVISIBLE);
                                    showDialogSuccessfullyLayout(getString(R.string.Sign_up_SuccessfullyText));
                                });


                    }
                }).addOnFailureListener(e -> {
                    binding.progressCircle.setVisibility(View.INVISIBLE);
                    if (e instanceof FirebaseAuthUserCollisionException) {
                        showDialogFailedLayout(getString(R.string.AlreadyRegistered));
                    } else if (e instanceof FirebaseNetworkException) {
                        showDialogFailedLayout(getString(R.string.noConnection));
                    } else {
                        showDialogFailedLayout("Exception -> " + e.getMessage());
                    }
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

            startActivity(new Intent(Sign_UP_Activity.this, Sign_IN_Activity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();

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
            Toast.makeText(this, "Click Skip", Toast.LENGTH_SHORT).show();
            alertDialog.dismiss();
        });
    }


}
package com.example.tawriqapp.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.tawriqapp.Activity.ImagePreviewActivity;
import com.example.tawriqapp.Model.Student;
import com.example.tawriqapp.R;
import com.example.tawriqapp.Register.RegisterActivity;
import com.example.tawriqapp.databinding.FragmentUserBinding;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class UserFragment extends Fragment implements Student.UpdateAccount {

    FragmentUserBinding userBinding;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference("AllStudent");

    boolean isEdit = false;
    Student currentStudent = null;

    String ImageUri = "";

    public UserFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        userBinding = FragmentUserBinding.inflate(inflater, container, false);

        if (firebaseUser != null) {

            loadingData();

            userBinding.setting.setOnClickListener(v -> {
                isEdit = !isEdit;
                userBinding.setting.setImageDrawable(isEdit ? getContext().getDrawable(R.drawable.ic_round_close_24) : getContext().getDrawable(R.drawable.ic_round_settings_24));

                userBinding.editImageLayout.setVisibility(isEdit ? View.VISIBLE : View.GONE);

                userBinding.studentEmail.setCompoundDrawablesWithIntrinsicBounds(isEdit ? R.drawable.editing : 0, 0, 0, 0);
                userBinding.studentPassword.setCompoundDrawablesWithIntrinsicBounds(isEdit ? R.drawable.editing : 0, 0, 0, 0);
                userBinding.studentFirstName.setCompoundDrawablesWithIntrinsicBounds(isEdit ? R.drawable.editing : 0, 0, 0, 0);
                userBinding.studentLastName.setCompoundDrawablesWithIntrinsicBounds(isEdit ? R.drawable.editing : 0, 0, 0, 0);
                userBinding.studentPhone.setCompoundDrawablesWithIntrinsicBounds(isEdit ? R.drawable.editing : 0, 0, 0, 0);
                userBinding.studentAboutMe.setCompoundDrawablesWithIntrinsicBounds(isEdit ? R.drawable.editing : 0, 0, 0, 0);

                userBinding.studentFirstName.setEnabled(isEdit);
                userBinding.studentLastName.setEnabled(isEdit);
                userBinding.studentPhone.setEnabled(isEdit);
                userBinding.studentAboutMe.setEnabled(isEdit);

                userBinding.btnUpdate.setVisibility(isEdit ? View.VISIBLE : View.INVISIBLE);

            });
            userBinding.logout.setOnClickListener(v -> {
                showDialogLogoutLayout();
            });
            userBinding.userImage.setOnClickListener(v -> {
                startActivity(new Intent(getContext(), ImagePreviewActivity.class)
                        .putExtra("ImageUri", ImageUri)
                        .putExtra("EditImage", false));
            });
            userBinding.editImageLayout.setOnClickListener(v -> {
                startActivity(new Intent(getContext(), ImagePreviewActivity.class)
                        .putExtra("ImageUri", ImageUri)
                        .putExtra("EditImage", true));
            });

            userBinding.studentEmail.setOnClickListener(v -> {
                if (isEdit) {
                    if (currentStudent != null)
                        showDialogUpdateEmailLayout(currentStudent);
                }
            });

            userBinding.studentPassword.setOnClickListener(v -> {
                if (isEdit) {
                    if (currentStudent != null)
                        showDialogUpdatePasswordLayout(currentStudent);
                }
            });

            userBinding.btnUpdate.setOnClickListener(v -> {
                inputDataReview();
            });
        } else {
            userBinding.getRoot().setOnClickListener(v -> {
                showDialogPleaseLoginLayout();
            });
        }
        return userBinding.getRoot();
    }

    void loadingData() {
        reference
                .child(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Student student = snapshot.getValue(Student.class);
                        if (student != null) {
                            currentStudent = student;
                            try {
                                Picasso
                                        .get()
                                        .load(student.getImageUri())
                                        .fit()
                                        .into(userBinding.userImage);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            ImageUri = student.getImageUri();

                            userBinding.studentEmail.setText(student.getEmail());
                            userBinding.studentPassword.setText(student.getPassword());
                            userBinding.studentFirstName.setText(student.getFirstName());
                            userBinding.studentLastName.setText(student.getLastName());
                            userBinding.studentPhone.setText(student.getPhone());
                            userBinding.studentAboutMe.setText(student.getAboutMe());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void inputDataReview() {

        // Hide the android keyboard
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(userBinding.btnUpdate.getWindowToken(), 0);

        userBinding.progressCircle.setVisibility(View.VISIBLE);

        String studentFirstName = Objects.requireNonNull(userBinding.studentFirstName.getText()).toString().trim();
        if (studentFirstName.isEmpty()) {
            userBinding.studentFirstName.setError(getString(R.string.userNameIsRequired));
            userBinding.studentFirstName.setFocusable(true);
            userBinding.studentFirstName.requestFocus();
            userBinding.progressCircle.setVisibility(View.GONE);
            return;
        }

        String studentLastName = Objects.requireNonNull(userBinding.studentLastName.getText()).toString().trim();
        if (studentLastName.isEmpty()) {
            userBinding.studentLastName.setError(getString(R.string.userNameIsRequired));
            userBinding.studentLastName.setFocusable(true);
            userBinding.studentLastName.requestFocus();
            userBinding.progressCircle.setVisibility(View.GONE);
            return;
        }

        String studentPhone = userBinding.studentPhone.getText().toString();
        if (TextUtils.isEmpty(studentPhone)) {
            userBinding.studentPhone.setError(getString(R.string.phone_number_is_required));
            userBinding.studentPhone.setFocusable(true);
            userBinding.studentPhone.requestFocus();
            userBinding.progressCircle.setVisibility(View.GONE);
            return;
        }

        String studentAboutMe = "" + Objects.requireNonNull(userBinding.studentAboutMe.getText()).toString().trim();

        currentStudent.setFirstName(studentFirstName);
        currentStudent.setLastName(studentLastName);
        currentStudent.setPhone(studentPhone);
        currentStudent.setAboutMe(studentAboutMe);

        // Update Student Account
        UpdateAccount(currentStudent);

    }

    @Override
    public void UpdateAccount(Student student) {
        reference.child(student.getID())
                .setValue(student)
                .addOnSuccessListener(unused -> {
                    userBinding.progressCircle.setVisibility(View.GONE);
                    showDialogSuccessfullyLayout(getString(R.string.updateDateSuccessfully));
                });
    }

    @SuppressLint("CheckResult")
    public void showDialogUpdateEmailLayout(Student studentDate) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.update_email_layout, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        //
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        alertDialog.getWindow().setWindowAnimations(R.style.AnimationForDialog);
        //

        LinearLayout authenticateLayout = dialogView.findViewById(R.id.authenticateLayout);
        ProgressBar progressCircle1 = dialogView.findViewById(R.id.progressCircle1);
        TextView CurrentEmail = dialogView.findViewById(R.id.inputCurrentEmail);
        EditText Password = dialogView.findViewById(R.id.inputPassword);
        Button btnAuthenticate = dialogView.findViewById(R.id.btnAuthenticate);

        LinearLayout updateEmailLayout = dialogView.findViewById(R.id.updateEmailLayout);
        ProgressBar progressCircle2 = dialogView.findViewById(R.id.progressCircle2);
        EditText NewEmail = dialogView.findViewById(R.id.NewEmail);
        Button btnUpdate = dialogView.findViewById(R.id.btnUpdate);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        CurrentEmail.setText(studentDate.getEmail());
        btnAuthenticate.setOnClickListener(v -> {
            if (Password.getText().toString().equals(studentDate.getPassword())) {
                progressCircle1.setVisibility(View.VISIBLE);
                AuthCredential credential = EmailAuthProvider.getCredential(studentDate.getEmail(), studentDate.getPassword());

                firebaseUser
                        .reauthenticate(credential)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                progressCircle1.setVisibility(View.GONE);
                                authenticateLayout.setVisibility(View.GONE);
                                updateEmailLayout.setVisibility(View.VISIBLE);
                            } else {
                                try {
                                    throw task.getException();
                                } catch (Exception e) {
                                    Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                showDialogMessageLayout(getString(R.string.passwordDontMatch));
            }
        });

        btnUpdate.setOnClickListener(v -> {
            progressCircle2.setVisibility(View.VISIBLE);
            String newEmail = Objects.requireNonNull(NewEmail.getText()).toString().trim();
            if (newEmail.isEmpty()) {
                NewEmail.setError(getString(R.string.emailIsRequired));
                NewEmail.requestFocus();
                progressCircle2.setVisibility(View.INVISIBLE);
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                NewEmail.setError(getString(R.string.please_enter_valid_email));
                NewEmail.requestFocus();
                progressCircle2.setVisibility(View.INVISIBLE);
                return;
            }

            firebaseUser
                    .updateEmail(newEmail)
                    .addOnCompleteListener(task -> {
                        if (task.isComplete()) {
                            currentStudent.setEmail(newEmail);
                            UpdateAccount(currentStudent);
                            alertDialog.dismiss();

                        } else {
                            try {
                                throw task.getException();
                            } catch (Exception e) {
                                Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });
        btnCancel.setOnClickListener(v -> {
            alertDialog.dismiss();
        });
    }

    @SuppressLint("CheckResult")
    public void showDialogUpdatePasswordLayout(Student studentDate) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.update_password_layout, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        //
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        alertDialog.getWindow().setWindowAnimations(R.style.AnimationForDialog);
        //

        ProgressBar progressCircle = dialogView.findViewById(R.id.progressCircle);
        EditText CurrentPassword = dialogView.findViewById(R.id.inputCurrentPassword);
        EditText NewPassword = dialogView.findViewById(R.id.inputNewPassword);
        EditText ConfirmNewPassword = dialogView.findViewById(R.id.inputConfirmNewPassword);
        Button btnUpdate = dialogView.findViewById(R.id.btnUpdate);

        btnUpdate.setOnClickListener(v -> {
            if (CurrentPassword.getText().toString().equals(studentDate.getPassword())) {
                progressCircle.setVisibility(View.VISIBLE);

                String newPassword = Objects.requireNonNull(NewPassword.getText()).toString().trim();

                if (newPassword.isEmpty()) {
                    NewPassword.setError(getString(R.string.passwordIsRequired));
                    NewPassword.requestFocus();
                    progressCircle.setVisibility(View.INVISIBLE);
                    return;
                }
                if (newPassword.length() < 8) {
                    NewPassword.setError(getString(R.string.minimumLength));
                    NewPassword.requestFocus();
                    progressCircle.setVisibility(View.INVISIBLE);
                    return;
                }
                if (newPassword.contains(" ")) {
                    NewPassword.setError(getString(R.string.passwordNotSpace));
                    NewPassword.setFocusable(true);
                    NewPassword.requestFocus();
                    progressCircle.setVisibility(View.INVISIBLE);
                    return;
                }

                String confirmPassword = Objects.requireNonNull(ConfirmNewPassword.getText()).toString().trim();
                if (!confirmPassword.equals(newPassword)) {
                    ConfirmNewPassword.setError(getString(R.string.passwordDontMatch));
                    ConfirmNewPassword.setFocusable(true);
                    ConfirmNewPassword.requestFocus();
                    progressCircle.setVisibility(View.INVISIBLE);
                    return;
                }

                AuthCredential credential = EmailAuthProvider.getCredential(studentDate.getEmail(), studentDate.getPassword());
                firebaseUser
                        .reauthenticate(credential)
                        .addOnCompleteListener(task -> {
                            firebaseUser
                                    .updatePassword(newPassword)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {

                                            currentStudent.setPassword(newPassword);
                                            UpdateAccount(currentStudent);
                                            alertDialog.dismiss();

                                        } else {
                                            try {
                                                throw task1.getException();
                                            } catch (Exception e) {
                                                Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        });

            } else {
                showDialogMessageLayout(getString(R.string.wrongPassword));
            }
        });
    }

    @SuppressLint("CheckResult")
    public void showDialogSuccessfullyLayout(String msg) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
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
            alertDialog.dismiss();
        });
    }

    @SuppressLint("CheckResult")
    public void showDialogMessageLayout(String msg) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.msg_layout, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        alertDialog.getWindow().setWindowAnimations(R.style.AnimationForDialog);

        TextView textMsg = dialogView.findViewById(R.id.textMsg);
        textMsg.setText(msg.trim());
    }

    @SuppressLint("CheckResult")
    public void showDialogLogoutLayout() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.logout_layout, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        alertDialog.getWindow().setWindowAnimations(R.style.AnimationForDialog);

        Button logoutButton = dialogView.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
            setUserStatus(getDateTime());
            firebaseAuth.signOut();
            startActivity(new Intent(getContext(), RegisterActivity.class));
            ActivityCompat.finishAffinity(getActivity());

        });
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(v -> {
            alertDialog.dismiss();
        });
    }

    @SuppressLint("CheckResult")
    public void showDialogPleaseLoginLayout() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.please_login_layout, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        alertDialog.getWindow().setWindowAnimations(R.style.AnimationForDialog);

        Button registerButton = dialogView.findViewById(R.id.registerButton);
        registerButton.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), RegisterActivity.class));
            alertDialog.dismiss();
            ActivityCompat.finishAffinity(getActivity());
        });
    }

    private void setUserStatus(String status) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        if (firebaseUser != null)
            reference
                    .child(firebaseUser.getUid())
                    .updateChildren(hashMap);
    }

    String getDateTime() {
        Date date = new Date();
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }


}
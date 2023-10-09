package com.example.tawriqapp.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.tawriqapp.Activity.AddItemsActivity;
import com.example.tawriqapp.Activity.DiplomaActivity;
import com.example.tawriqapp.R;
import com.example.tawriqapp.Register.RegisterActivity;
import com.example.tawriqapp.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class    HomeFragment extends Fragment {

    FragmentHomeBinding homeBinding;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false);

        homeBinding.ProgrammingLayout.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), DiplomaActivity.class).putExtra("TYPE", getString(R.string.Programming)));
        });
        homeBinding.MultimediaLayout.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), DiplomaActivity.class).putExtra("TYPE", getString(R.string.Multimedia)));
        });
        homeBinding.addNewItem.setOnClickListener(v -> {
            if (user != null) {
                showDialogAddPaperLayout();
            } else {
                showDialogPleaseLoginLayout();
            }
        });

        return homeBinding.getRoot();
    }

    @SuppressLint({"CheckResult", "NonConstantResourceId"})
    public void showDialogAddPaperLayout() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_layout, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        alertDialog.getWindow().setWindowAnimations(R.style.AnimationForDialog);

        RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroupType);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.radioBook:
                    startActivity(new Intent(getContext(), AddItemsActivity.class)
                            .putExtra("STYLE", "Book"));
                    break;
                case R.id.radioSlide:
                    startActivity(new Intent(getContext(), AddItemsActivity.class)
                            .putExtra("STYLE", "Slide"));
                    break;
            }
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


}
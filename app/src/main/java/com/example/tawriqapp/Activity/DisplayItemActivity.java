package com.example.tawriqapp.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.tawriqapp.Adapter.AdapterSlider;
import com.example.tawriqapp.Model.Item;
import com.example.tawriqapp.R;
import com.example.tawriqapp.Register.RegisterActivity;
import com.example.tawriqapp.databinding.ActivityDisplayItemBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class DisplayItemActivity extends AppCompatActivity {

    ActivityDisplayItemBinding binding;
    Item itemData = null;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceAllStudent = database.getReference("AllStudent");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDisplayItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        itemData = (Item) getIntent().getSerializableExtra("ItemData");

        Initialize_Variables();

        loadingData(itemData);

        binding.imageBack.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.chatOwner.setOnClickListener(v -> {
            if (user != null) {
                startActivity(new Intent(DisplayItemActivity.this, ChattingActivity.class)
                        .putExtra("ReceiverID", itemData.getStudentID()));
            } else {
                showDialogPleaseLoginLayout();
            }
        });

        binding.viewComments.setOnClickListener(v -> {
            startActivity(new Intent(DisplayItemActivity.this, ViewItemCommentsActivity.class)
                    .putExtra("ItemData", itemData));
        });

    }

    private void Initialize_Variables() {
        if (user != null) {
            if (itemData.getStudentID().equals(user.getUid())) {
                binding.chatOwner.setVisibility(View.GONE);
            } else {
                binding.chatOwner.setVisibility(View.VISIBLE);
            }
        }
    }

    void loadingData(Item itemData) {
        if (itemData != null) {

            List<String> coverList = new ArrayList<>();

            coverList.add(itemData.getCoverPhotoUrl_1());
            coverList.add(itemData.getCoverPhotoUrl_2());
            coverList.add(itemData.getCoverPhotoUrl_3());

            AdapterSlider adapterSlider = new AdapterSlider(coverList);
            binding.imageSlider.setSliderAdapter(adapterSlider);
            binding.imageSlider.setAutoCycle(true);
            binding.imageSlider.setSliderTransformAnimation(SliderAnimations.ZOOMOUTTRANSFORMATION);
            binding.imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM);

            binding.ItemTitleTV.setText(itemData.getItemStyle().equals("Book") ? getString(R.string.bookTitle) : getString(R.string.slideTitle));
            binding.ownerNameTV.setText(itemData.getItemStyle().equals("Book") ? getString(R.string.bookName) : getString(R.string.slideName));
            binding.ItemStatusTV.setText(itemData.getItemStyle().equals("Book") ? getString(R.string.bookStatus) : getString(R.string.slideStatus));

            binding.ItemTitleData.setText(itemData.getItemTitle());
            binding.ownerNameData.setText(itemData.getOwnerName());
            binding.ItemTypeData.setText(itemData.getProgram().getProgramName());
            binding.courseNameData.setText(itemData.getCourse().getCourseName());
            binding.courseCodeData.setText(itemData.getCourse().getCourseCode());
            binding.bookStatusData.setText(itemData.getQuality());
            binding.academicYearData.setText(itemData.getAcademicYear());
            binding.semesterData.setText(itemData.getSemester());
            binding.ItemDateData.setText(itemData.getUploadDate());
        }
    }

    @SuppressLint("CheckResult")
    public void showDialogPleaseLoginLayout() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.please_login_layout, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        alertDialog.getWindow().setWindowAnimations(R.style.AnimationForDialog);

        Button registerButton = dialogView.findViewById(R.id.registerButton);
        registerButton.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            alertDialog.dismiss();
            ActivityCompat.finishAffinity(this);
        });
    }

    private void setUserStatus(String status) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        if (user != null)
            referenceAllStudent
                    .child(user.getUid())
                    .updateChildren(hashMap);
    }

    String getDateTime() {
        Date date = new Date();
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUserStatus("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        setUserStatus(getDateTime());
    }

}
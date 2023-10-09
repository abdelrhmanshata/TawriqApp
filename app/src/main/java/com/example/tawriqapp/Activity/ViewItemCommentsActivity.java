package com.example.tawriqapp.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.tawriqapp.Adapter.AdapterComments;
import com.example.tawriqapp.Model.Comment;
import com.example.tawriqapp.Model.Item;
import com.example.tawriqapp.Model.Student;
import com.example.tawriqapp.R;
import com.example.tawriqapp.Register.RegisterActivity;
import com.example.tawriqapp.databinding.ActivityViewItemCommentsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ViewItemCommentsActivity extends AppCompatActivity implements Student.WriteComment {

    ActivityViewItemCommentsBinding binding;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceAllStudent = database.getReference("AllStudent");
    DatabaseReference refComments = database.getReference("Comments");

    ArrayList<Comment> commentArrayList;
    AdapterComments adapterComments;

    Item itemData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewItemCommentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        itemData = (Item) getIntent().getSerializableExtra("ItemData");

        Initialize_Variables();
        loadingData(itemData);
        ViewComments(itemData.getItemID());

        binding.imageBack.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.sendButton.setOnClickListener(v -> {
            if (user != null) {
                if (!binding.commentText.getText().toString().isEmpty()) {
                    // Hide the android keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(binding.sendButton.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);

                    Comment comment = new Comment();
                    comment.setCommentID(refComments.push().getKey());
                    comment.setWriter(user.getUid());
                    comment.setItemID(itemData.getItemID());
                    comment.setCommentText(binding.commentText.getText().toString());
                    comment.setCommentTime(getCurrentTime());
                    comment.setCommentDate(getCurrentData());

                    // Write Comment Into Item In Database
                    WriteComment(comment);
                    binding.commentText.setText("");

                } else {
                    Toast.makeText(this, "لا يمكنك ارسال تعليق فارغ", Toast.LENGTH_SHORT).show();
                }
            } else {
                showDialogPleaseLoginLayout();
            }
        });

    }

    void Initialize_Variables() {
        commentArrayList = new ArrayList<>();
        adapterComments = new AdapterComments(this, commentArrayList);

        binding.commentsRecyclerView.setHasFixedSize(true);
        binding.commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.commentsRecyclerView.setAdapter(adapterComments);
    }

    private void loadingData(Item itemData) {
        try {
            Picasso
                    .get()
                    .load(itemData.getCoverPhotoUrl_1())
                    .fit()
                    .placeholder(R.drawable.loading)
                    .into(binding.coverImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        binding.itemTitle.setText(itemData.getItemTitle());
        binding.ownerName.setText(itemData.getOwnerName());
    }

    @Override
    public void ViewComments(String ItemID) {
        refComments
                .child(ItemID)
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        commentArrayList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Comment comment = snapshot.getValue(Comment.class);
                            if (comment != null) {
                                commentArrayList.add(comment);
                            }
                        }
                        adapterComments.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    @Override
    public void WriteComment(Comment comment) {
        refComments
                .child(itemData.getItemID())
                .child(comment.getCommentID())
                .setValue(comment)
                .addOnSuccessListener(unused -> {
                    showDialogSuccessfullyLayout(getString(R.string.successfullyAddedComment));
                });
    }

    public String getCurrentData() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy/M/dd", Locale.ENGLISH);
        return format.format(calendar.getTime());
    }

    public String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
        return format.format(calendar.getTime());
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
            alertDialog.dismiss();
        });
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
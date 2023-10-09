package com.example.tawriqapp.Activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tawriqapp.Fragment.ChatFragment;
import com.example.tawriqapp.Fragment.HomeFragment;
import com.example.tawriqapp.Fragment.MyBooksFragment;
import com.example.tawriqapp.Fragment.SearchFragment;
import com.example.tawriqapp.Fragment.UserFragment;
import com.example.tawriqapp.Model.Message;
import com.example.tawriqapp.R;
import com.example.tawriqapp.databinding.ActivityHomeBinding;
import com.google.android.material.badge.BadgeDrawable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;

    HomeFragment homeFragment = new HomeFragment();
    SearchFragment searchFragment = new SearchFragment();
    MyBooksFragment myBooksFragment = new MyBooksFragment();
    ChatFragment chatFragment = new ChatFragment();
    UserFragment userFragment = new UserFragment();

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceAllStudent = database.getReference("AllStudent");
    DatabaseReference messageReference = database.getReference("Messages");

    public static ArrayList<String> listSenderID = new ArrayList();

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.background));

        Home();
        if (user != null)
            getCountUnreadMessage();

        binding
                .bottomNavigation
                .setOnItemSelectedListener(item -> {

                    switch (item.getItemId()) {
                        case R.id.home:
                            Home();
                            return true;
                        case R.id.search:
                            Search();
                            return true;
                        case R.id.myBooks:
                            MyBooks();
                            return true;
                        case R.id.chat:
                            Chat();
                            return true;
                        case R.id.user:
                            User();
                            return true;
                    }
                    return false;
                });
    }

    void Home() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, homeFragment)
                .commit();
    }

    void Search() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, searchFragment)
                .commit();
    }

    void MyBooks() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, myBooksFragment)
                .commit();
    }

    void Chat() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, chatFragment)
                .commit();
    }

    void User() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, userFragment)
                .commit();
    }

    void getCountUnreadMessage() {
        messageReference
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int unreadMsg = 0;
                        listSenderID.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Message message = snapshot.getValue(Message.class);
                            if (message != null) {
                                if (message.getReceiver().equals(user.getUid())) {
                                    if (!message.getWasRead()) {
                                        unreadMsg++;
                                        if (!listSenderID.contains(message.getSender())) {
                                            listSenderID.add(message.getSender());
                                        }
                                    }
                                }
                            }
                        }
                        BadgeDrawable badgeDrawable = binding.bottomNavigation.getOrCreateBadge(R.id.chat);
                        if (unreadMsg != 0) {
                            badgeDrawable.setVisible(true);
                            badgeDrawable.setNumber(unreadMsg);
                        } else {
                            badgeDrawable.setVisible(false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
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
package com.example.tawriqapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tawriqapp.Adapter.AdapterItemsFilter;
import com.example.tawriqapp.Model.Book;
import com.example.tawriqapp.Model.Item;
import com.example.tawriqapp.Model.Slide;
import com.example.tawriqapp.databinding.ActivityDiplomaBinding;
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
import java.util.List;
import java.util.Locale;

public class DiplomaActivity extends AppCompatActivity {

    ActivityDiplomaBinding binding;

    String Type = "";

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();

    ValueEventListener mDBListener;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceAllStudent = database.getReference("AllStudent");
    DatabaseReference mDatabaseRef = database.getReference("Item");

    AdapterItemsFilter adapterItemsFilter;

    List<Item> itemDataList;
    List<Item> bookDataList;
    List<Item> slideDataList;

    TextWatcher searchTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(final CharSequence s, int start, int before, int count) {
            Type = getIntent().getStringExtra("TYPE");
            loadingAllBookItems(Type, s.toString().toLowerCase());
            loadingAllSlideItems(Type, s.toString().toLowerCase());
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDiplomaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Type = getIntent().getStringExtra("TYPE");

        Initialize_Variables();

        binding.imageBack.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.ItemSearch.addTextChangedListener(searchTextWatcher);

        binding.itemsGridView.setOnItemClickListener((parent, view, position, id) -> {
            Item itemData = itemDataList.get(position);

            startActivity(new Intent(this, DisplayItemActivity.class)
                    .putExtra("ItemData", itemData)

            );
        });

        loadingAllBookItems(Type, null);
        loadingAllSlideItems(Type, null);
    }

    private void Initialize_Variables() {
        itemDataList = new ArrayList<>();
        bookDataList = new ArrayList<>();
        slideDataList = new ArrayList<>();
        adapterItemsFilter = new AdapterItemsFilter(this, itemDataList);
        binding.itemsGridView.setAdapter(adapterItemsFilter);
    }

    public void loadingAllBookItems(String type, CharSequence name) {
        binding.progressCircle.setVisibility(View.VISIBLE);
        mDBListener = mDatabaseRef
                .child("Book")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        bookDataList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Book bookData = snapshot.getValue(Book.class);
                            if (bookData != null) {
                                Item ItemData = Item.convertToItemData(bookData, null);
                                if (ItemData.getProgram().getProgramName().equals(type)) {
                                    if (name == null) {
                                        bookDataList.add(ItemData);
                                    } else {
                                        if (ItemData.getItemTitle().toLowerCase().contains(name)) {
                                            bookDataList.add(ItemData);
                                        }
                                    }
                                }
                            }
                        }
                        mergeData();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    public void loadingAllSlideItems(String type, CharSequence name) {
        binding.progressCircle.setVisibility(View.VISIBLE);
        mDBListener = mDatabaseRef
                .child("Slide")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        slideDataList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Slide slideData = snapshot.getValue(Slide.class);
                            if (slideData != null) {
                                Item ItemData = Item.convertToItemData(null, slideData);
                                if (ItemData.getProgram().getProgramName().equals(type)) {
                                    if (name == null) {
                                        slideDataList.add(ItemData);
                                    } else {
                                        if (ItemData.getItemTitle().toLowerCase().contains(name)) {
                                            slideDataList.add(ItemData);
                                        }
                                    }
                                }
                            }
                        }
                        mergeData();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    void mergeData() {
        itemDataList.clear();
        itemDataList.addAll(bookDataList);
        itemDataList.addAll(slideDataList);
        adapterItemsFilter.notifyDataSetChanged();

        binding.progressCircle.setVisibility(View.GONE);

        if (itemDataList.isEmpty())
            binding.emptyImage.setVisibility(View.VISIBLE);
        else {
            binding.emptyImage.setVisibility(View.GONE);
        }
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
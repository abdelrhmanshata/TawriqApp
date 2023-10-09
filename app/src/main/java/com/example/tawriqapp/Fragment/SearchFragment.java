package com.example.tawriqapp.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.tawriqapp.Activity.DisplayItemActivity;
import com.example.tawriqapp.Adapter.AdapterItemsFilter;
import com.example.tawriqapp.Model.Book;
import com.example.tawriqapp.Model.Item;
import com.example.tawriqapp.Model.Slide;
import com.example.tawriqapp.Model.Student;
import com.example.tawriqapp.R;
import com.example.tawriqapp.databinding.FragmentSearchBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment implements Student.ViewAllItem, Student.SearchItem {

    FragmentSearchBinding searchBinding;

    ValueEventListener mDBListener;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabaseRef = database.getReference("Item");

    AdapterItemsFilter adapterItemsFilter;

    List<Item> itemDataList;
    List<Item> bookDataList;
    List<Item> slideDataList;


    public SearchFragment() {
        // Required empty public constructor
    }

    TextWatcher searchTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(final CharSequence s, int start, int before, int count) {
            searchBinding.radioFilter.setText(getString(R.string.Sort_by));
            SearchItem(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };


    @SuppressLint("NonConstantResourceId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        searchBinding = FragmentSearchBinding.inflate(inflater, container, false);

        Initialize_Variables();
        ViewAllItem();

        searchBinding.itemSearch.addTextChangedListener(searchTextWatcher);

        searchBinding.radioAll.setOnClickListener(v -> {
            ViewAllItem();
            searchBinding.radioFilter.setText(getString(R.string.Sort_by));
            searchBinding.itemSearch.setText("");
        });

        searchBinding.radioFilter.setOnClickListener(v -> {
            searchBinding.itemSearch.setText("");
            showDialogFilterLayout();
        });

        searchBinding.itemsGridView.setOnItemClickListener((parent, view, position, id) -> {
            Item ItemData = itemDataList.get(position);
            startActivity(new Intent(getContext(), DisplayItemActivity.class).putExtra("ItemData", ItemData));
        });

        return searchBinding.getRoot();
    }

    private void Initialize_Variables() {

        itemDataList = new ArrayList<>();
        bookDataList = new ArrayList<>();
        slideDataList = new ArrayList<>();

        adapterItemsFilter = new AdapterItemsFilter(getContext(), itemDataList);
        searchBinding.itemsGridView.setAdapter(adapterItemsFilter);

    }

    public void loadingAllBookItems(String type, CharSequence name) {
        searchBinding.progressCircle.setVisibility(View.VISIBLE);
        mDBListener = mDatabaseRef
                .child("Book")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        bookDataList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Book bookData = snapshot.getValue(Book.class);
                            if (bookData != null) {
                                Item itemData = Item.convertToItemData(bookData, null);
                                if (type.isEmpty()) {
                                    if (name == null) {
                                        bookDataList.add(itemData);
                                    } else {
                                        if (itemData.getItemTitle().toLowerCase().contains(name)) {
                                            bookDataList.add(itemData);
                                        }
                                    }
                                } else {
                                    if (itemData.getProgram().getProgramName().equals(type)) {
                                        bookDataList.add(itemData);
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
        searchBinding.progressCircle.setVisibility(View.VISIBLE);
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
                                if (type.isEmpty()) {
                                    if (name == null) {
                                        slideDataList.add(ItemData);
                                    } else {
                                        if (ItemData.getItemTitle().toLowerCase().contains(name)) {
                                            slideDataList.add(ItemData);
                                        }
                                    }
                                } else {
                                    if (ItemData.getProgram().getProgramName().equals(type))
                                        slideDataList.add(ItemData);
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

        searchBinding.progressCircle.setVisibility(View.GONE);

        if (itemDataList.isEmpty())
            searchBinding.emptyImage.setVisibility(View.VISIBLE);
        else {
            searchBinding.emptyImage.setVisibility(View.GONE);
        }
    }

    @SuppressLint({"CheckResult", "NonConstantResourceId"})
    public void showDialogFilterLayout() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.filter_type_layout, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        //
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        alertDialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        //

        ImageView checkFilter = dialogView.findViewById(R.id.checkFilter);
        checkFilter.setOnClickListener(v -> {

            String Style = "";
            RadioGroup radioGroupStyle = dialogView.findViewById(R.id.radioGroupStyle);
            RadioButton selectedStyle = dialogView.findViewById(radioGroupStyle.getCheckedRadioButtonId());
            switch (selectedStyle.getId()) {
                case R.id.radioBook:
                    Style = getString(R.string.Book);
                    break;
                case R.id.radioSlide:
                    Style = getString(R.string.Slide);
                    break;
            }
            String Type = "";
            RadioGroup radioGroupType = dialogView.findViewById(R.id.radioGroupType);
            RadioButton selectedType = dialogView.findViewById(radioGroupType.getCheckedRadioButtonId());
            switch (selectedType.getId()) {
                case R.id.radioProgramming:
                    Type = getString(R.string.Programming);
                    break;
                case R.id.radioMedia:
                    Type = getString(R.string.Multimedia);
                    break;
            }

            searchBinding.radioFilter.setText(Type.equals(getString(R.string.Programming)) ? getString(R.string.Sort_by_Programming) : getString(R.string.Sort_by_Media));

            itemDataList.clear();
            bookDataList.clear();
            slideDataList.clear();

            if (Style.equals(getString(R.string.Book))) {
                loadingAllBookItems(Type, null);
            } else {
                loadingAllSlideItems(Type, null);
            }

            alertDialog.dismiss();
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }

    @Override
    public void SearchItem(String ItemTitle) {
        loadingAllBookItems("", ItemTitle.toLowerCase());
        loadingAllSlideItems("", ItemTitle.toLowerCase());
    }

    @Override
    public void ViewAllItem() {
        loadingAllBookItems("", null);
        loadingAllSlideItems("", null);
    }
}
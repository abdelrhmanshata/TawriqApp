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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.tawriqapp.Activity.AddItemsActivity;
import com.example.tawriqapp.Activity.DisplayItemActivity;
import com.example.tawriqapp.Adapter.AdapterItems;
import com.example.tawriqapp.Model.Book;
import com.example.tawriqapp.Model.Item;
import com.example.tawriqapp.Model.Slide;
import com.example.tawriqapp.R;
import com.example.tawriqapp.Register.RegisterActivity;
import com.example.tawriqapp.databinding.FragmentMyBooksBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyBooksFragment extends Fragment {

    FragmentMyBooksBinding myBooksBinding;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabaseRef = database.getReference("Item");

    AdapterItems adapterItems;

    List<Item> ItemDataList;
    List<Item> bookDataList;
    List<Item> slideDataList;

    boolean isEdit = false;

    public MyBooksFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myBooksBinding = FragmentMyBooksBinding.inflate(inflater, container, false);

        Initialize_Variables();

        myBooksBinding.addItem.setOnClickListener(v -> {
            if (firebaseUser != null) {
                showDialogAddItemLayout();
            } else {
                showDialogPleaseLoginLayout();
            }
        });
        myBooksBinding.editItem.setOnClickListener(v -> {
            if (firebaseUser != null) {
                if (bookDataList.isEmpty() && slideDataList.isEmpty()) {
                    isEdit = false;
                    showDialogMessageLayout(getString(R.string.noBookFound));
                    myBooksBinding.editItem.setText(isEdit ? getString(R.string.saveButton) : getString(R.string.editButton));
                } else {
                    isEdit = !isEdit;
                    myBooksBinding.editItem.setText(isEdit ? getString(R.string.saveButton) : getString(R.string.editButton));
                    adapterItems = new AdapterItems(getContext(), ItemDataList, isEdit);
                    myBooksBinding.itemGridView.setAdapter(adapterItems);
                }
            } else {
                showDialogPleaseLoginLayout();
            }
        });

        myBooksBinding.itemGridView.setOnItemClickListener((parent, view, position, id) -> {
            Item ItemData = ItemDataList.get(position);
            if (isEdit) {
                startActivity(new Intent(getContext(), AddItemsActivity.class).putExtra("ItemData", ItemData));
            } else {
                startActivity(new Intent(getContext(), DisplayItemActivity.class).putExtra("ItemData", ItemData));
            }
        });

        if (firebaseUser != null) {
            loadingAllBookItems();
            loadingAllSlideItems();
        } else {
            myBooksBinding.progressCircle.setVisibility(View.GONE);
            myBooksBinding.emptyImage.setVisibility(View.VISIBLE);
        }
        return myBooksBinding.getRoot();
    }

    private void Initialize_Variables() {

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        ItemDataList = new ArrayList<>();
        bookDataList = new ArrayList<>();
        slideDataList = new ArrayList<>();

        adapterItems = new AdapterItems(getContext(), ItemDataList, false);
        myBooksBinding.itemGridView.setAdapter(adapterItems);

    }

    @SuppressLint({"CheckResult", "NonConstantResourceId"})
    public void showDialogAddItemLayout() {
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
                    startActivity(new Intent(getContext(), AddItemsActivity.class).putExtra("STYLE", "Book"));
                    break;
                case R.id.radioSlide:
                    startActivity(new Intent(getContext(), AddItemsActivity.class).putExtra("STYLE", "Slide"));
                    break;
            }
            alertDialog.dismiss();
        });
    }

    public void loadingAllBookItems() {
        myBooksBinding.progressCircle.setVisibility(View.VISIBLE);
        mDatabaseRef
                .child("Book")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        bookDataList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Book bookData = snapshot.getValue(Book.class);
                            if (bookData != null && bookData.getStudentID().equals(firebaseUser.getUid())) {
                                Item ItemData = Item.convertToItemData(bookData, null);
                                bookDataList.add(ItemData);
                            }
                        }
                        mergeData();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    public void loadingAllSlideItems() {
        myBooksBinding.progressCircle.setVisibility(View.VISIBLE);
        mDatabaseRef
                .child("Slide")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        slideDataList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Slide slideData = snapshot.getValue(Slide.class);
                            if (slideData != null && slideData.getStudentID().equals(firebaseUser.getUid())) {
                                Item ItemData = Item.convertToItemData(null, slideData);
                                slideDataList.add(ItemData);
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
        ItemDataList.clear();
        ItemDataList.addAll(bookDataList);
        ItemDataList.addAll(slideDataList);
        adapterItems.notifyDataSetChanged();

        myBooksBinding.progressCircle.setVisibility(View.GONE);

        if (ItemDataList.isEmpty())
            myBooksBinding.emptyImage.setVisibility(View.VISIBLE);
        else {
            myBooksBinding.emptyImage.setVisibility(View.GONE);
        }
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
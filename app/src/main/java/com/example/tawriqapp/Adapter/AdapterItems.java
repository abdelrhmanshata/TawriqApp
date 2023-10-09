package com.example.tawriqapp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.tawriqapp.Model.Item;
import com.example.tawriqapp.Model.Student;
import com.example.tawriqapp.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterItems extends ArrayAdapter<Item> implements Student.DeleteItem {
    Context context;
    boolean isEdit;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference("Item");
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference;

    public AdapterItems(@NonNull Context context, @NonNull List<Item> objects, boolean isEdit) {
        super(context, 0, objects);
        this.context = context;
        this.isEdit = isEdit;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        Item itemData = getItem(position);

        ShapeableImageView PaperImage = listItemView.findViewById(R.id.paperImage);
        ImageView deleteButton = listItemView.findViewById(R.id.deleteButton);
        TextView courseName = listItemView.findViewById(R.id.courseName);
        deleteButton.setVisibility(isEdit ? View.VISIBLE : View.INVISIBLE);

        /*View view = listItemView.findViewById(R.id.deleteButton);
        int cx = view.getWidth() / 2;
        int cy = view.getHeight() / 2;
        float radius = (float) Math.hypot(cx, cy);
        if (isEdit) {
            Animator animator = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, radius);
            view.setVisibility(View.VISIBLE);
            animator.start();
        } else {
            Animator animator = ViewAnimationUtils.createCircularReveal(view, cx, cy, radius, 0);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    view.setVisibility(View.INVISIBLE);
                }
            });
            animator.start();
        }*/

        try {
            Picasso
                    .get()
                    .load(itemData.getCoverPhotoUrl_1().trim() + "")
                    .fit()
                    .placeholder(R.drawable.loading)
                    .into(PaperImage);
        } catch (Exception e) {
            Log.d("" + context, e.getMessage());
        }

        deleteButton.setOnClickListener(v -> {
            DeleteItem(itemData);
        });

        courseName.setText(itemData.getItemTitle());
        return listItemView;
    }

    @SuppressLint("CheckResult")
    public void showDialogMessageLayout(Item paperData, String msg, String successMsg) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(
                R.layout.are_you_sure_delete_layout, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        alertDialog.getWindow().setWindowAnimations(R.style.AnimationForDialog);

        TextView textMsg = dialogView.findViewById(R.id.textMsg);
        textMsg.setText(msg);

        Button deleteButton = dialogView.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(v -> {
            try {
                if (!paperData.getCoverPhotoUrl_1().isEmpty()) {
                    storageReference = storage.getReferenceFromUrl(paperData.getCoverPhotoUrl_1());
                    storageReference.delete();
                }
                if (!paperData.getCoverPhotoUrl_2().isEmpty()) {
                    storageReference = storage.getReferenceFromUrl(paperData.getCoverPhotoUrl_2());
                    storageReference.delete();
                }
                if (!paperData.getCoverPhotoUrl_3().isEmpty()) {
                    storageReference = storage.getReferenceFromUrl(paperData.getCoverPhotoUrl_3());
                    storageReference.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            databaseReference
                    .child(paperData.getItemStyle())
                    .child(paperData.getItemID())
                    .removeValue();

            alertDialog.dismiss();
            showDialogSuccessfullyLayout(successMsg);
        });

        Button cancelButton = dialogView.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(v -> {
            alertDialog.dismiss();
        });
    }

    @SuppressLint("CheckResult")
    public void showDialogSuccessfullyLayout(String msg) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(
                R.layout.sign_in_successfully_layout, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        //
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

    @Override
    public void DeleteItem(Item itemData) {
        String msg = itemData.getItemStyle().equals("Book") ? context.getString(R.string.areYouSureDeleteBook) : context.getString(R.string.areYouSureDeleteSlide);
        String successMsg = itemData.getItemStyle().equals("Book") ? context.getString(R.string.successfullyDeletedBook) : context.getString(R.string.successfullyDeletedSlide);
        showDialogMessageLayout(itemData, msg, successMsg);
    }
}

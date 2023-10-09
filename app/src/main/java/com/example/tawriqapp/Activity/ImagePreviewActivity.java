package com.example.tawriqapp.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tawriqapp.R;
import com.example.tawriqapp.databinding.ActivityImagePreviewBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class ImagePreviewActivity extends AppCompatActivity
        implements EasyPermissions.PermissionCallbacks {

    ActivityImagePreviewBinding binding;

    private static final int GALLERY_REQUEST = 1;
    private static final int IMAGE_CAPTURE = 1001;
    Uri uriImage = null;
    Bitmap bitmapImage = null;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceAllStudent = database.getReference("AllStudent");

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference("StudentImage");

    boolean EditImage = false;
    boolean Camera = false, Gallery = false;

    String ImageUri = "";

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImagePreviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ImageUri = getIntent().getStringExtra("ImageUri");
        EditImage = getIntent().getBooleanExtra("EditImage", false);

        binding.selectImage.setVisibility(EditImage ? View.VISIBLE : View.GONE);
        binding.saveImage.setVisibility(EditImage ? View.VISIBLE : View.GONE);

        loadingImage(ImageUri);

        binding.imageBack.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.selectImage.setOnClickListener(v -> {
            //  Define camera storage permissions
            String[] strings = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
            //  Check condition
            if (EasyPermissions.hasPermissions(this, strings)) {
                //SelectImage();
                showDialogTakePhotoLayout();
            } else {
                EasyPermissions.requestPermissions(
                        this,
                        "App needs access to your camera & storage",
                        100,
                        strings
                );
            }

        });

        binding.saveImage.setOnClickListener(v -> {
            if (Camera) {
                if (bitmapImage != null)
                    uploadImageBitmap(bitmapImage);
            }
            if (Gallery) {
                if (uriImage != null)
                    uploadImageUri(uriImage);
            }
        });

    }

    private void loadingImage(String imageUrl) {
        try {
            Picasso
                    .get()
                    .load(imageUrl)
                    .fit()
                    .placeholder(R.drawable.loading)
                    .into(binding.photoView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("CheckResult")
    public void showDialogTakePhotoLayout() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.take_picture_layout, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        alertDialog.getWindow().setWindowAnimations(R.style.AnimationForDialog);

        LinearLayout cameraLayout = dialogView.findViewById(R.id.cameraLayout);
        LinearLayout galleryLayout = dialogView.findViewById(R.id.galleryLayout);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);

        cameraLayout.setOnClickListener(v -> {
            openCamera();
            alertDialog.dismiss();
        });

        galleryLayout.setOnClickListener(v -> {
            openGallery();
            alertDialog.dismiss();
        });

        cancelButton.setOnClickListener(v -> {
            alertDialog.dismiss();
        });
    }

    private void openCamera() {
        Camera = true;
        Gallery = false;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, IMAGE_CAPTURE);
    }

    public void openGallery() {
        Camera = false;
        Gallery = true;
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_CAPTURE) {
            try {
                bitmapImage = (Bitmap) data.getExtras().get("data");
                binding.photoView.setImageBitmap(bitmapImage);
            } catch (Exception e) {
                finish();
            }
        }
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                Uri imageURI = data.getData();
                CropImage.activity(imageURI)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);
            } catch (Exception e) {
                finish();
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                uriImage = result.getUri();
                binding.photoView.setImageURI(uriImage);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                assert result != null;
                Exception error = result.getError();
                showDialogMessageLayout(error.getMessage());
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void uploadImageUri(Uri filePath) {
        if (filePath != null) {
            binding.progressCircle.setVisibility(View.VISIBLE);
            StorageReference ref = storageReference
                    .child(user.getUid() + "/StudentImage");
            ref.putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> {
                        ref.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    if (!referenceAllStudent.onDisconnect().cancel().isSuccessful()) {
                                        binding.progressCircle.setVisibility(View.GONE);
                                        showDialogSuccessfullyLayout(getString(R.string.updatedSuccessfully));
                                        referenceAllStudent
                                                .child(user.getUid())
                                                .child("imageUri")
                                                .setValue(uri.toString());
                                    }
                                });
                    }).addOnFailureListener(e -> {
                        binding.progressCircle.setVisibility(View.GONE);
                    });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void uploadImageBitmap(Bitmap bitmapImage) {
        if (bitmapImage != null) {
            binding.progressCircle.setVisibility(View.VISIBLE);
            StorageReference ref = storageReference
                    .child(user.getUid() + "/StudentImage");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            ref.putBytes(data)
                    .addOnSuccessListener(taskSnapshot -> {
                        ref.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    if (!referenceAllStudent.onDisconnect().cancel().isSuccessful()) {
                                        binding.progressCircle.setVisibility(View.GONE);
                                        showDialogSuccessfullyLayout(getString(R.string.updatedSuccessfully));
                                        referenceAllStudent
                                                .child(user.getUid())
                                                .child("imageUri")
                                                .setValue(uri.toString());
                                    }
                                });
                    }).addOnFailureListener(e -> {
                        binding.progressCircle.setVisibility(View.GONE);
                    });
        }
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
    public void showDialogMessageLayout(String msg) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (requestCode == 100 && perms.size() == 2) {
            showDialogTakePhotoLayout();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
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
package com.example.tawriqapp.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.tawriqapp.Adapter.AdapterImageCover;
import com.example.tawriqapp.Model.Book;
import com.example.tawriqapp.Model.Item;
import com.example.tawriqapp.Model.Slide;
import com.example.tawriqapp.Model.Student;
import com.example.tawriqapp.R;
import com.example.tawriqapp.databinding.ActivityAddItemBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class AddItemsActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener, EasyPermissions.PermissionCallbacks, Student.AddItem, Student.UpdateItem {

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser CurrentUser = auth.getCurrentUser();

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceAllStudent = database.getReference("AllStudent");
    DatabaseReference databaseReference = database.getReference("Item");
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference("Item");

    ActivityAddItemBinding binding;
    ArrayList<String>
            listCourseName,
            listCourseCode,
            listBookQuality,
            listAcademicYear,
            listSemester;

    ArrayList<Uri> listImage;
    ArrayList<String> listImageUrl;

    AdapterImageCover adapterImageCover;

    String styleItem = "";

    Item item = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        styleItem = getIntent().getStringExtra("STYLE");
        item = (Item) getIntent().getSerializableExtra("ItemData");

        // Initialize all variables in this page
        Initialize_Variables();
        loadingDataFromObject(item);

        binding.coverImage.setOnClickListener(v -> {
            //  Define camera storage permissions
            String[] strings = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
            //  Check condition
            if (EasyPermissions.hasPermissions(this, strings)) {
                ImagePicker();
            } else {
                EasyPermissions.requestPermissions(
                        this,
                        "App needs access to your camera & storage",
                        100,
                        strings
                );
            }
        });

        binding.courseNameLayout.setOnClickListener(v -> {
            showDialogListLayout(getString(R.string.courseName), listCourseName, binding.inputCourseName);
        });

        binding.courseCodeLayout.setOnClickListener(v -> {
            showDialogListLayout(getString(R.string.courseCode), listCourseCode, binding.inputCourseCode);
        });

        binding.bookStatusLayout.setOnClickListener(v -> {
            showDialogListLayout(getString(R.string.Status), listBookQuality, binding.inputStatus);
        });

        binding.academicYearLayout.setOnClickListener(v -> {
            showDialogListLayout(getString(R.string.academicYear), listAcademicYear, binding.inputAcademicYear);
        });

        binding.semesterLayout.setOnClickListener(v -> {
            showDialogListLayout(getString(R.string.semester), listSemester, binding.inputSemester);
        });

        binding.uploadDateBtn.setOnClickListener(v -> {
            showDatePickerDialog();
        });

        binding.addButton.setOnClickListener(v -> {
            itemDataReview();
        });

        binding.cancelButton.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void Initialize_Variables() {

        if (item != null) {
            styleItem = item.getItemStyle();
        }

        if (styleItem.equals("Book")) {
            binding.itemTitle.setText(getString(R.string.BookTitle));
            binding.OwnerName.setText(getString(R.string.BookName));
            binding.itemStatus.setText(getString(R.string.BookStatus));
        } else {
            binding.itemTitle.setText(getString(R.string.SlideTitle));
            binding.OwnerName.setText(getString(R.string.SlideName));
            binding.itemStatus.setText(getString(R.string.SlideStatus));
        }

        listCourseName = new ArrayList<>();
        listCourseName.add("لغة انجليزيه مكثفه 1");
        listCourseName.add("الرياضيات");
        listCourseName.add("برمجة الحاسب 1");
        listCourseName.add("اساسيات الحاسب و تطبيقاته");
        listCourseName.add("لغة انجليزيه مكثفه 2");
        listCourseName.add("مهارات الكتابه باللغة الانجليزيه");
        listCourseName.add("برمجة الحاسب 2");
        listCourseName.add("الشبكات و الاتصالات (1212 تقن)");
        listCourseName.add("تطوير تطبيقات الهاتف الذكي");
        listCourseName.add("اساسيات قواعد البيانات");

        listCourseCode = new ArrayList<>();
        listCourseCode.add("1140 نجم");
        listCourseCode.add("1111 ريض");
        listCourseCode.add("1111 تقن");
        listCourseCode.add("1112 تقن");
        listCourseCode.add("1141 نجم");
        listCourseCode.add("1142 نجم");
        listCourseCode.add("1211 تقن");
        listCourseCode.add("1212 تقن");
        listCourseCode.add("1351 تقن");
        listCourseCode.add("1213 تقن");

        listBookQuality = new ArrayList<>();
        listBookQuality.add("جديد");
        listBookQuality.add("مستعمل بحالة ممتازة");
        listBookQuality.add("مستعمل بحالة جيدة");

        listAcademicYear = new ArrayList<>();
        listAcademicYear.add("2022");
        listAcademicYear.add("2021");
        listAcademicYear.add("2020");
        listAcademicYear.add("2019");
        listAcademicYear.add("2018");

        listSemester = new ArrayList<>();
        listSemester.add("الفصل الدراسي الأول");
        listSemester.add("الفصل الدراسي الثاني");

        listImage = new ArrayList<>();
        listImageUrl = new ArrayList<>();

        binding.recyclerviewCover.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapterImageCover = new AdapterImageCover(listImage, listImageUrl);
        binding.recyclerviewCover.setAdapter(adapterImageCover);

    }

    @SuppressLint("NotifyDataSetChanged")
    void loadingDataFromObject(Item itemData) {
        if (itemData != null) {

            listImageUrl.add(itemData.getCoverPhotoUrl_1());
            listImageUrl.add(itemData.getCoverPhotoUrl_2());
            listImageUrl.add(itemData.getCoverPhotoUrl_3());

            binding.inputItemTitle.setText(itemData.getItemTitle());
            binding.inputOwnerName.setText(itemData.getOwnerName());
            binding.radioGroupType.check(itemData.getProgram().getProgramName().equals(getString(R.string.Programming)) ? R.id.radioProgramming : R.id.radioMedia);
            binding.inputCourseName.setText(itemData.getCourse().getCourseName());
            binding.inputCourseCode.setText(itemData.getCourse().getCourseCode());
            binding.inputStatus.setText(itemData.getQuality());
            binding.inputAcademicYear.setText(itemData.getAcademicYear());
            binding.inputSemester.setText(itemData.getSemester());
            binding.inputDate.setText(itemData.getUploadDate());

            binding.addButton.setText(getString(R.string.edit));

            binding.recyclerviewCover.setAdapter(new AdapterImageCover(listImage, listImageUrl));
        }
    }


    @SuppressLint("NonConstantResourceId")
    public void itemDataReview() {
        binding.progressCircle.setVisibility(View.VISIBLE);

        if (listImage.isEmpty() && item == null) {
            showDialogFailedLayout(getString(R.string.coverImageIsRequired));
            binding.progressCircle.setVisibility(View.INVISIBLE);
            return;
        }
        String inputItemTitle = Objects.requireNonNull(binding.inputItemTitle.getText()).toString().trim();
        if (inputItemTitle.isEmpty()) {
            binding.inputItemTitle.setError(getString(R.string.addressIsRequired));
            binding.inputItemTitle.requestFocus();
            binding.progressCircle.setVisibility(View.INVISIBLE);
            return;
        }

        String inputOwnerName = Objects.requireNonNull(binding.inputOwnerName.getText()).toString().trim();
        if (inputOwnerName.isEmpty()) {
            binding.inputOwnerName.setError(getString(R.string.nameIsRequired));
            binding.inputOwnerName.requestFocus();
            binding.progressCircle.setVisibility(View.INVISIBLE);
            return;
        }

        String inputType = "";
        if (binding.radioGroupType.getCheckedRadioButtonId() == -1) {
            showDialogFailedLayout(getString(R.string.typeIsRequired));
            binding.progressCircle.setVisibility(View.INVISIBLE);
            return;
        } else {
            RadioButton selectedType = findViewById(binding.radioGroupType.getCheckedRadioButtonId());
            switch (selectedType.getId()) {
                case R.id.radioProgramming:
                    inputType = getString(R.string.Programming);
                    break;
                case R.id.radioMedia:
                    inputType = getString(R.string.Multimedia);
                    break;
            }
        }

        String inputCourseName = Objects.requireNonNull(binding.inputCourseName.getText()).toString().trim();
        if (inputCourseName.isEmpty()) {
            binding.inputCourseName.setError(getString(R.string.courseNameIsRequired));
            binding.inputCourseName.requestFocus();
            binding.progressCircle.setVisibility(View.INVISIBLE);
            return;
        } else {
            binding.inputCourseName.setError(null);
        }

        String inputCourseCode = Objects.requireNonNull(binding.inputCourseCode.getText()).toString().trim();
        if (inputCourseCode.isEmpty()) {
            binding.inputCourseCode.setError(getString(R.string.courseCodeIsRequired));
            binding.inputCourseCode.requestFocus();
            binding.progressCircle.setVisibility(View.INVISIBLE);
            return;
        } else {
            binding.inputCourseCode.setError(null);
        }

        String inputStatus = Objects.requireNonNull(binding.inputStatus.getText()).toString().trim();
        if (inputStatus.isEmpty()) {
            binding.inputStatus.setError(getString(R.string.bookStatusIsRequired));
            binding.inputStatus.requestFocus();
            binding.progressCircle.setVisibility(View.INVISIBLE);
            return;
        } else {
            binding.inputStatus.setError(null);
        }

        String inputAcademicYear = Objects.requireNonNull(binding.inputAcademicYear.getText()).toString().trim();
        if (inputAcademicYear.isEmpty()) {
            binding.inputAcademicYear.setError(getString(R.string.academicYearIsRequired));
            binding.inputAcademicYear.requestFocus();
            binding.progressCircle.setVisibility(View.INVISIBLE);
            return;
        } else {
            binding.inputAcademicYear.setError(null);
        }

        String inputSemester = Objects.requireNonNull(binding.inputSemester.getText()).toString().trim();
        if (inputSemester.isEmpty()) {
            binding.inputSemester.setError(getString(R.string.semesterIsRequired));
            binding.inputSemester.requestFocus();
            binding.progressCircle.setVisibility(View.INVISIBLE);
            return;
        } else {
            binding.inputSemester.setError(null);
        }

        String inputDate = Objects.requireNonNull(binding.inputDate.getText()).toString().trim();
        if (inputDate.isEmpty()) {
            binding.inputDate.setError(getString(R.string.dateIsRequired));
            binding.inputDate.requestFocus();
            binding.progressCircle.setVisibility(View.INVISIBLE);
            return;
        } else {
            binding.inputDate.setError(null);
        }

        /*String ID;
        if(item ==null){
            ID = databaseReference.push().getKey();
        }else{
            ID = item.getItemID();
        }*/

        String ID = item == null ? databaseReference.push().getKey() : item.getItemID();

        if (styleItem.equals("Book")) {
            Book book = new Book();
            book.setItemID(ID);
            book.setStudentID(CurrentUser.getUid());
            book.setItemStyle("Book");
            book.setCoverPhotoUrl_1(item == null ? "" : item.getCoverPhotoUrl_1());
            book.setCoverPhotoUrl_2(item == null ? "" : item.getCoverPhotoUrl_2());
            book.setCoverPhotoUrl_3(item == null ? "" : item.getCoverPhotoUrl_3());
            book.setItemTitle(inputItemTitle);
            book.setBookAuthor(inputOwnerName);
            book.getProgram().setProgramName(inputType);
            book.getCourse().setCourseName(inputCourseName);
            book.getCourse().setCourseCode(inputCourseCode);
            book.setQuality(inputStatus);
            book.setAcademicYear(inputAcademicYear);
            book.setSemester(inputSemester);
            book.setUploadDate(inputDate);

            // Upload Item In DataBase
            uploadItemData(listImage, ID, book, null);
        } else {
            Slide slide = new Slide();
            slide.setItemID(ID);
            slide.setStudentID(CurrentUser.getUid());
            slide.setItemStyle("Slide");
            slide.setCoverPhotoUrl_1(item == null ? "" : item.getCoverPhotoUrl_1());
            slide.setCoverPhotoUrl_2(item == null ? "" : item.getCoverPhotoUrl_2());
            slide.setCoverPhotoUrl_3(item == null ? "" : item.getCoverPhotoUrl_3());
            slide.setItemTitle(inputItemTitle);
            slide.setCourseTeacher(inputOwnerName);
            slide.getProgram().setProgramName(inputType);
            slide.getCourse().setCourseName(inputCourseName);
            slide.getCourse().setCourseCode(inputCourseCode);
            slide.setQuality(inputStatus);
            slide.setAcademicYear(inputAcademicYear);
            slide.setSemester(inputSemester);
            slide.setUploadDate(inputDate);

            // Upload Item In DataBase
            uploadItemData(listImage, ID, null, slide);
        }
    }

    private void uploadItemData(ArrayList<Uri> listImageUris, String ID, Book bookData, Slide slideData) {
        binding.progressCircle.setVisibility(View.VISIBLE);
        if (!listImageUris.isEmpty()) {
            for (int index = 0; index < listImageUris.size(); index++) {
                Uri imageUri = listImageUris.get(index);
                if (imageUri != null) {
                    StorageReference ref = storageReference.child(ID).child(index + ".jpg");
                    int finalIndex = index;
                    ref.putFile(imageUri)
                            .addOnSuccessListener(taskSnapshot -> {
                                ref.getDownloadUrl()
                                        .addOnSuccessListener(uri -> {
                                            switch (finalIndex) {
                                                case 0:
                                                    if (bookData != null)
                                                        bookData.setCoverPhotoUrl_1(uri.toString());
                                                    if (slideData != null)
                                                        slideData.setCoverPhotoUrl_1(uri.toString());
                                                    break;
                                                case 1:
                                                    if (bookData != null)
                                                        bookData.setCoverPhotoUrl_2(uri.toString());
                                                    if (slideData != null)
                                                        slideData.setCoverPhotoUrl_2(uri.toString());
                                                    break;
                                                case 2:
                                                    if (bookData != null)
                                                        bookData.setCoverPhotoUrl_3(uri.toString());
                                                    if (slideData != null)
                                                        slideData.setCoverPhotoUrl_3(uri.toString());
                                                    break;
                                            }
                                            if (finalIndex == listImageUris.size() - 1)
                                              uploadItem(bookData, slideData);
                                        });
                            }).addOnFailureListener(e -> {
                                binding.progressCircle.setVisibility(View.GONE);
                            });
                }
            }
        } else {
            uploadItem(bookData, slideData);
        }
    }

    void uploadItem(Book bookData, Slide slideData) {
        databaseReference
                .child(styleItem)
                .child(bookData != null ? bookData.getItemID() : slideData.getItemID())
                .setValue(bookData != null ? bookData : slideData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        binding.progressCircle.setVisibility(View.INVISIBLE);
                        String msg;
                        if (item == null) {
                            msg = styleItem.equals("Book") ? getString(R.string.successfullyAddedBook) : getString(R.string.successfullyAddedSlide);
                        } else {
                            msg = styleItem.equals("Book") ? getString(R.string.successfullyEditedBook) : getString(R.string.successfullyEditedSlide);
                        }
                        showDialogSuccessfullyLayout(msg);
                    }
                });
    }

    public void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = dayOfMonth + "/" + (month + 1) + "/" + year;
        binding.inputDate.setText(date.trim());
    }

    @SuppressLint("CheckResult")
    public void showDialogListLayout(String listName, ArrayList<String> list, TextView textViewResult) {

        // Hide the android keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.getRoot().getWindowToken(), 0);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.list_layout, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        alertDialog.getWindow().setWindowAnimations(R.style.AnimationForDialog);

        ListView listView = (ListView) dialogView.findViewById(R.id.listText);
        TextView textView = (TextView) dialogView.findViewById(R.id.listName);

        textView.setText(listName);

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((adapterView, view, position, l) -> {
            String value = adapter.getItem(position);
            textViewResult.setText(value.trim());
            alertDialog.dismiss();
        });
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
            onBackPressed();
            alertDialog.dismiss();
        });
    }

    @SuppressLint("CheckResult")
    public void showDialogFailedLayout(String msg) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.sign_in_failed_layout, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        alertDialog.getWindow().setWindowAnimations(R.style.AnimationForDialog);

        TextView textMsg = dialogView.findViewById(R.id.textMsg);
        textMsg.setText(msg.trim());

        Button skipButton = dialogView.findViewById(R.id.skipButton);
        skipButton.setOnClickListener(v -> {
            alertDialog.dismiss();
        });
    }

    void ImagePicker() {
        listImage.clear();
        FilePickerBuilder.getInstance()
                .setActivityTitle("Select Images")
                .setSpan(FilePickerConst.SPAN_TYPE.FOLDER_SPAN, 3)
                .setSpan(FilePickerConst.SPAN_TYPE.DETAIL_SPAN, 4)
                .setMaxCount(3)
                .setSelectedFiles(listImage)
                .setActivityTheme(R.style.CustomTheme)
                .pickPhoto(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == FilePickerConst.REQUEST_CODE_PHOTO) {
                listImage = data.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA);
                binding.recyclerviewCover.setAdapter(new AdapterImageCover(listImage, listImageUrl));
            }
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (requestCode == 100 && perms.size() == 2) {
            ImagePicker();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        } else {
            Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void setUserStatus(String status) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        if (CurrentUser != null)
            referenceAllStudent
                    .child(CurrentUser.getUid())
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

    @Override
    public void AddItem(Item item) {
        databaseReference
                .child(styleItem)
                .child(item.getItemID())
                .setValue(item)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        binding.progressCircle.setVisibility(View.INVISIBLE);
                        String msg = styleItem.equals("Book") ? getString(R.string.successfullyAddedBook) : getString(R.string.successfullyAddedSlide);
                        showDialogSuccessfullyLayout(msg);
                    }
                });
    }

    @Override
    public void UpdateItem(Item item) {
        databaseReference
                .child(styleItem)
                .child(item.getItemID())
                .setValue(item)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        binding.progressCircle.setVisibility(View.INVISIBLE);
                        String msg = styleItem.equals("Book") ? getString(R.string.successfullyEditedBook) : getString(R.string.successfullyEditedSlide);
                        showDialogSuccessfullyLayout(msg);
                    }
                });
    }
}
package com.madadgar.fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.madadgar.R;
import com.madadgar._interface.FirebaseFileStorageResponse;
import com.madadgar._interface.FirebaseResponse;
import com.madadgar._interface.ItemClickListener;
import com.madadgar.activity.MapActivity;
import com.madadgar.adapter.RecyclerViewAdapter;
import com.madadgar.enums.UserType;
import com.madadgar.firebase.FireBaseDb;
import com.madadgar.model.Emergency;
import com.madadgar.model.User;
import com.madadgar.util.Constants;
import com.madadgar.util.FirebaseFileStorage;
import com.madadgar.util.Permission;
import com.madadgar.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;


public class EmergencyFragment extends BaseFragment implements FirebaseResponse, View.OnClickListener, ItemClickListener<Emergency>, OnSuccessListener<Location>, OnFailureListener, FirebaseFileStorageResponse {

    private FireBaseDb fireBaseDb;
    private User user;

    private DatabaseReference databaseReference;

    private RecyclerView recyclerView;
    private TextView textView_noData;
    private FloatingActionButton fab;

    private AlertDialog customDialog;

    private Permission permission;
    /*Dialog Views*/
    private ImageView imageView_emergencyImage;
    private TextView textView_uploadImage;
    private Spinner spinner_emergencyType;
    private TextView textView_emergencyLocation;
    private EditText editText_emergencyInfo;
    private TextView textView_add;
    private TextView textView_cancel;

    private Uri imageUri;
    private Bitmap imageBitmap;
    private String currentLocation;

    private FirebaseFileStorage fileStorage;
    private FirebaseStorage firebaseStorage;

    private Emergency emergency;
    private ProgressDialog progressDialog;

    public EmergencyFragment() {
        // Required empty public constructor
    }


    public EmergencyFragment(FireBaseDb fireBaseDb, User user) {
        this.fireBaseDb = fireBaseDb;
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_emergency, container, false);

        permission = new Permission(getContext());
        databaseReference = FirebaseDatabase.getInstance().getReference("Emergency");


        fileStorage = new FirebaseFileStorage(this);
        firebaseStorage = FirebaseStorage.getInstance();

        setUpProgressDialog();
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        fab = view.findViewById(R.id.fab);
        textView_noData = view.findViewById(R.id.textView_noData);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        fab.setOnClickListener(this);

        if (user.getUserType() == UserType.User) {
            fab.show();
        }

        if (user.getUserType() == UserType.User) {
            progressDialog.show();
            fireBaseDb.view(databaseReference.orderByChild("userId").equalTo(user.getUserId()), Emergency.class, this);
        } else {
            progressDialog.show();
            fireBaseDb.view(databaseReference, Emergency.class, this);
        }
    }

    @Override
    public void onSuccess(List list) {
        progressDialog.hide();
        if (!list.isEmpty()) {
            textView_noData.setVisibility(View.GONE);
        } else {
            textView_noData.setVisibility(View.VISIBLE);
        }

        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, R.layout.item_emergency, list);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onFailure(String message) {
        progressDialog.hide();
        Util.showToast(getContext(), message);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                showCustomDialog(R.layout.dialog_report_emergency);
                break;
            case R.id.textView_add:
                if (isValidInput()) {
                    progressDialog.show();
                    submitEmergencyCase();
                    customDialog.dismiss();
                } else {
                    Util.showToast(getContext(), "Input Fields shouldn't pe empty");
                }
                break;
            case R.id.textView_cancel:
                customDialog.dismiss();
                break;
            case R.id.textView_uploadImage:
            case R.id.imageView_emergencyImage:
                showImageCaptureDialog();
                break;
            case R.id.textView_emergencyLocation:
                if (permission.isPermitted(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    getCurrentLocation();
                } else {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.LOCATION_PERMISSION_CODE);
                }
                break;
        }
    }

    private boolean isValidInput() {
        return !(TextUtils.isEmpty(textView_emergencyLocation.getText().toString()) && TextUtils.isEmpty(editText_emergencyInfo.getText().toString()) && imageUri == null);
    }


    private void submitEmergencyCase() {
        String emergencyId = databaseReference.push().getKey();
        String emergencyType = (String) spinner_emergencyType.getSelectedItem();
        String emergencyDetails = editText_emergencyInfo.getText().toString();
        String emergencyPhotoUrl = emergencyId;
        String emergencyStatus = "PENDING";
        String emergencyLocationAddress = textView_emergencyLocation.getText().toString();
        String emergencyLocation = currentLocation;
        Date emergencyReportingTime = new Date();
        emergency = new Emergency(emergencyId, emergencyType, emergencyDetails, emergencyPhotoUrl, emergencyStatus, emergencyLocation, emergencyLocationAddress, emergencyReportingTime, user.getUserId());

        StorageReference storageReference = firebaseStorage.getReference().child("images/" + emergency.getEmergencyPhotoUrl());
        fileStorage.uploadFile(storageReference, imageUri);
    }

    private void showCustomDialog(int layoutId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = getLayoutInflater().inflate(layoutId, null);

        /*init views of dialog*/
        initDialogViews(view);


        builder.setView(view).setCancelable(true);
        customDialog = builder.create();
        customDialog.show();
    }

    private void showImageCaptureDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Image Resource from...");
        builder.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (permission.isPermitted(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    openFileChooser();
                } else {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.READ_STORAGE_PERMISSION_CODE);
                }
            }
        });
        builder.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (permission.isPermitted(Manifest.permission.CAMERA)) {
                    openCamera();
                } else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, Constants.CAMERA_PERMISSION_CODE);
                }
            }
        });
        builder.create().show();
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, Constants.PICK_CAMERA_IMAGE_REQUEST);
    }

    private void initDialogViews(View view) {
        imageView_emergencyImage = view.findViewById(R.id.imageView_emergencyImage);
        textView_uploadImage = view.findViewById(R.id.textView_uploadImage);
        spinner_emergencyType = view.findViewById(R.id.spinner_emergencyType);
        textView_emergencyLocation = view.findViewById(R.id.textView_emergencyLocation);
        editText_emergencyInfo = view.findViewById(R.id.editText_emergencyDetails);
        textView_add = view.findViewById(R.id.textView_add);
        textView_cancel = view.findViewById(R.id.textView_cancel);

        textView_emergencyLocation.setOnClickListener(this);

        textView_uploadImage.setOnClickListener(this);
        imageView_emergencyImage.setOnClickListener(this);

        textView_add.setOnClickListener(this);
        textView_cancel.setOnClickListener(this);
    }

    @Override
    public void onItemClicked(Emergency em) {

        startActivity(new Intent(getContext(), MapActivity.class).putExtra("emergency", em));
    }

    @Override
    public void onItemLongClicked(View view, final Emergency em) {
        if (user.getUserType() != UserType.User) {
            PopupMenu popup = new PopupMenu(getContext(), view);
            popup.getMenuInflater().inflate(R.menu.option_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.item_updateEmergencyStatus:
                            updateEmergencyStatusDialog(em);
                            break;
                    }
                    return true;
                }
            });
            popup.show();

        }

    }

    private void updateEmergencyStatusDialog(final Emergency em) {
        final CharSequence[] statusValues = {"PENDING", "RESOLVED"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Update Emergency Status");
        builder.setSingleChoiceItems(statusValues, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Util.showToast(getContext(), statusValues[which] + "");
                em.setEmergencyStatus(statusValues[which] + "");
                fireBaseDb.update(databaseReference, em.getEmergencyId(), em);
                dialog.dismiss();
            }
        });

        builder.setCancelable(true);
        builder.create().show();
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), Constants.PICK_GALLERY_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.PICK_GALLERY_IMAGE_REQUEST:
                if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                    imageUri = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
                        imageView_emergencyImage.setImageBitmap(bitmap);
                        textView_uploadImage.setVisibility(View.GONE);

                    } catch (IOException e) {
                        Util.showToast(getContext(), e.getMessage());
                    }
                }
                break;
            case Constants.PICK_CAMERA_IMAGE_REQUEST:
                if (resultCode == RESULT_OK) {
                    imageBitmap = (Bitmap) data.getExtras().get("data");
                    textView_uploadImage.setVisibility(View.GONE);

                    if (permission.isPermitted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        imageUri = saveImageToGallery(imageBitmap);
                        imageView_emergencyImage.setImageURI(imageUri);
                    } else {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.WRITE_STORAGE_PERMISSION_CODE);
                    }
                }

                break;
        }

    }

    private Uri saveImageToGallery(Bitmap imageBitmap) {
        String savedImageURL = MediaStore.Images.Media.insertImage(
                getContext().getContentResolver(),
                imageBitmap,
                "Bird",
                "Image of bird"
        );
        return Uri.parse(savedImageURL);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.READ_STORAGE_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openFileChooser();
                } else if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Util.showToast(getContext(), "Some Features might not work");

                    openAppSettings();
                }
            }
            break;
            case Constants.WRITE_STORAGE_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    imageUri = saveImageToGallery(imageBitmap);
                    imageView_emergencyImage.setImageURI(imageUri);
                } else if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Util.showToast(getContext(), "Some Features might not work");

                    openAppSettings();
                }
            }
            break;
            case Constants.LOCATION_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();
                } else if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Util.showToast(getContext(), "Some Features might not work");
                    openAppSettings();
                }
            }
            break;
            case Constants.CAMERA_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
                    Util.showToast(getContext(), "Some Features might not work");
                    openAppSettings();
                }
            }
            break;
        }
    }


    @Override
    public void onSuccess(Location location) {
        if (location != null) {
            currentLocation = location.getLatitude() + ":" + location.getLongitude();
            String currentLocationAddress = getAddressFromLatLng(location.getLatitude(), location.getLongitude());
            textView_emergencyLocation.setText(currentLocationAddress);
        } else {
            Util.showToast(getContext(), "Can't get Location, Turn on your GPS");
        }
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onFileUploadSuccess(Uri uri) {
        progressDialog.hide();
        emergency.setEmergencyPhotoUrl(uri.toString());
        fireBaseDb.insert(databaseReference, emergency.getEmergencyId(), emergency);

    }

    @Override
    public void onFileUploadFailure(String failureCause) {
        progressDialog.hide();
        Util.showToast(getContext(), failureCause);
    }

    @Override
    public void onFileUploadProgress(double progress) {
        progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
    }

    private void setUpProgressDialog() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading....");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
    }

}

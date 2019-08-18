package com.madadgar.fragment;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.madadgar.R;
import com.madadgar._interface.FirebaseResponse;
import com.madadgar._interface.ItemClickListener;
import com.madadgar.adapter.RecyclerViewAdapter;
import com.madadgar.decoration.MyDividerItemDecoration;
import com.madadgar.enums.UserType;
import com.madadgar.firebase.FireBaseDb;
import com.madadgar.model.Blood;
import com.madadgar.model.User;
import com.madadgar.util.Constants;
import com.madadgar.util.Permission;
import com.madadgar.util.Util;

import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class BloodFragment extends BaseFragment implements View.OnClickListener, FirebaseResponse, ItemClickListener<Blood>, MaterialSpinner.OnItemSelectedListener<String> {


    private FireBaseDb fireBaseDb;
    private User user;
    private DatabaseReference databaseReference;
    private FloatingActionButton fab;
    private TextView textView_noData;
    private RecyclerView recyclerView;
    private TextView textView_bloodQuantity;
    private ProgressDialog progressDialog;
    private MaterialSpinner spinner;
    private String currentLocation;
    /*Dialog Views*/
    private TextView textView_bloodGroup;
    private Spinner spinner_requestType;
    private TextView textView_bloodRequestLocation;
    private EditText editText_bloodRequestDetails;
    private TextView textView_add;
    private TextView textView_cancel;
    private EditText editText_bloodQuantity;
    private String[] bloodGroupArray;
    private AlertDialog customDialog;

    private Permission permission;
    private int totalBloodCount = 0;

    public BloodFragment() {
        // Required empty public constructor
    }


    public BloodFragment(FireBaseDb fireBaseDb, User user) {
        this.fireBaseDb = fireBaseDb;
        this.user = user;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_blood, container, false);

        permission = new Permission(getContext());
        databaseReference = FirebaseDatabase.getInstance().getReference("Blood");

        bloodGroupArray = getResources().getStringArray(R.array.bloodGroupTypesArray);

        setUpProgressDialog();
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        textView_bloodQuantity = view.findViewById(R.id.textView_bloodQuantity);
        spinner = view.findViewById(R.id.spinner_bloodGroupType);
        fab = view.findViewById(R.id.fab);
        textView_noData = view.findViewById(R.id.textView_noData);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL, 16));


        progressDialog.show();
        fireBaseDb.view(databaseReference.child("A+"), Blood.class, this);

        spinner.setItems(bloodGroupArray);
        spinner.setOnItemSelectedListener(this);
        fab.setOnClickListener(this);

        if (user.getUserType() == UserType.User) {
            fab.show();
        }

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                showCustomDialog(R.layout.dialog_request_blood);
                break;
            case R.id.textView_add:
                if (isValidInput()) {
                    if (spinner_requestType.getSelectedItemPosition() == 0 && totalBloodCount - Integer.parseInt(editText_bloodQuantity.getText().toString()) < 0) {
                        Util.showToast(getContext(), "Current quantity is not according to your need");
                    } else {
                        uploadBloodRequest();
                        customDialog.dismiss();
                    }
                } else {
                    Util.showToast(getContext(), "Input Fields shouldn't be empty");
                }
                break;
            case R.id.textView_cancel:
                customDialog.dismiss();
                break;
            case R.id.textView_bloodRequestLocation:
                if (permission.isPermitted(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    getCurrentLocation();
                } else {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.LOCATION_PERMISSION_CODE);
                }
                break;
        }

    }

    private boolean isValidInput() {
        return !(TextUtils.isEmpty(editText_bloodQuantity.getText().toString()) && TextUtils.isEmpty(textView_bloodRequestLocation.getText().toString()) && TextUtils.isEmpty(editText_bloodRequestDetails.getText().toString()));
    }

    private void uploadBloodRequest() {
        String bloodGroup = textView_bloodGroup.getText().toString();
        String bloodId = databaseReference.push().getKey();
        String bloodRequestType = (String) spinner_requestType.getSelectedItem();
        String bloodRequestLocation = textView_bloodRequestLocation.getText().toString();
        Date bloodRequestDate = new Date();
        int bloodQuantity = Integer.parseInt(editText_bloodQuantity.getText().toString());
        String userName = user.getUserName();

        Blood blood = new Blood(bloodId, bloodRequestType, bloodRequestLocation, bloodRequestDate, bloodQuantity, userName);
        fireBaseDb.insert(databaseReference.child(bloodGroup), bloodId, blood);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.LOCATION_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();
                } else if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Util.showToast(getContext(), "Some Features might not work");
                    openAppSettings();
                }
            }
            break;
        }
    }

    @Override
    public void onSuccess(List list) {

        totalBloodCount = 0;
        for (int i = 0; i < list.size(); i++) {
            Blood b = (Blood) list.get(i);
            if (b.getBloodRequestType().equals("Donate Blood")) {
                totalBloodCount += b.getQuantity();
            }
        }
        textView_bloodQuantity.setText(totalBloodCount + " Bottle(s) Available");
        progressDialog.hide();
        if (!list.isEmpty()) {
            textView_noData.setVisibility(View.GONE);
        } else {
            textView_noData.setVisibility(View.VISIBLE);
        }

        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, R.layout.item_blood, list);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onFailure(String message) {
        progressDialog.hide();
    }

    @Override
    public void onItemClicked(Blood blood) {

    }

    @Override
    public void onItemLongClicked(View view, Blood blood) {

    }

    @Override
    public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
        progressDialog.show();
        fireBaseDb.view(databaseReference.child(item), Blood.class, this);
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

    private void initDialogViews(View view) {
        textView_bloodGroup = view.findViewById(R.id.textView_bloodGroup);
        spinner_requestType = view.findViewById(R.id.spinner_requestType);
        textView_bloodRequestLocation = view.findViewById(R.id.textView_bloodRequestLocation);
        editText_bloodRequestDetails = view.findViewById(R.id.editText_bloodRequestDetails);
        editText_bloodQuantity = view.findViewById(R.id.editText_bloodQuantity);
        textView_add = view.findViewById(R.id.textView_add);
        textView_cancel = view.findViewById(R.id.textView_cancel);


        textView_bloodGroup.setText(bloodGroupArray[spinner.getSelectedIndex()]);
        textView_bloodRequestLocation.setOnClickListener(this);


        textView_add.setOnClickListener(this);
        textView_cancel.setOnClickListener(this);
    }


    @Override
    public void onFailure(@NonNull Exception e) {

    }

    @Override
    public void onSuccess(Location location) {
        currentLocation = location.getLatitude() + ":" + location.getLongitude();
        String currentLocationAddress = getAddressFromLatLng(location.getLatitude(), location.getLongitude());
        textView_bloodRequestLocation.setText(currentLocationAddress);

    }

    private void setUpProgressDialog() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading....");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
    }

}

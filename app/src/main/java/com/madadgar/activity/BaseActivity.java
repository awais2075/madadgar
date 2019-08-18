package com.madadgar.activity;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.madadgar._interface.FirebaseResponse;
import com.madadgar.util.FirebaseEmailAuth;
import com.madadgar.firebase.FireBaseDb;
import com.madadgar.model.User;

/*abstract class
 * all activities extend this class inorder to get similar methods or instances*/
public abstract class BaseActivity extends AppCompatActivity implements FirebaseResponse {

    protected FirebaseEmailAuth auth;
    protected FireBaseDb fireBaseDb;
    protected ProgressDialog progressDialog;

    protected static User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getView());

        /*Initializing Email FirebaseEmailAuth*/
        auth = new FirebaseEmailAuth();
        auth.init();

        /*Initializing FireBase*/
        fireBaseDb = new FireBaseDb(this);

        /*Setting up Progress Dialog*/
        setUpProgressDialog();

        /*Initializing Activity Views*/
    }


    /*abstract method to getView of activity*/
    protected abstract int getView();

    /*abstract method to initialize views in activity*/
    protected abstract void initViews();


    @Override
    protected void onStart() {
        super.onStart();
    }


    /*method to show progress dialog whenever loading time is required*/
    public void setUpProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading....");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
    }
}


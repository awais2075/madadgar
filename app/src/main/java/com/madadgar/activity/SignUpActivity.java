package com.madadgar.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.madadgar.R;
import com.madadgar._interface.AuthResponse;
import com.madadgar.enums.UserType;
import com.madadgar.model.User;
import com.madadgar.util.Util;

import java.util.List;

public class SignUpActivity extends BaseActivity implements View.OnClickListener, TextWatcher, AuthResponse {

    private EditText editText_name;
    private EditText editText_email;
    private EditText editText_password;

    private Button button_signUp;

    private Spinner spinner_bloodGroup;

    private DatabaseReference databaseReference;

    private boolean isUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isUser = getIntent().getBooleanExtra("userType", false);
        databaseReference = FirebaseDatabase.getInstance().getReference("User");
        initViews();
    }

    @Override
    protected int getView() {
        return R.layout.activity_sign_up;
    }

    @Override
    protected void initViews() {
        editText_name = findViewById(R.id.editText_name);
        editText_email = findViewById(R.id.editText_email);
        editText_password = findViewById(R.id.editText_password);
        spinner_bloodGroup = findViewById(R.id.spinner_bloodGroupType);

        button_signUp = findViewById(R.id.button_signUp);

        editText_name.addTextChangedListener(this);
        editText_email.addTextChangedListener(this);
        editText_password.addTextChangedListener(this);

        button_signUp.setOnClickListener(this);
        findViewById(R.id.textView_signIn).setOnClickListener(this);
    }

    @Override
    public void onSuccess(List list) {

    }

    @Override
    public void onSuccess(User user) {
        fireBaseDb.insert(databaseReference, user.getUserId(), user);
        progressDialog.hide();
        Util.showToast(this, "Registration Successful...");
        if (isUser) {
            //user = (User) list.get(0);
            setResult(Activity.RESULT_OK, new Intent().putExtra("user", user));
        }
        finish();
    }

    @Override
    public void onFailure(String message) {
        Util.showToast(this, message);
        progressDialog.hide();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_signUp:
                String userName = editText_name.getText().toString();
                String userEmail = editText_email.getText().toString();
                String userPassword = editText_password.getText().toString();
                String userBloodGroup = (String) spinner_bloodGroup.getSelectedItem();
                UserType userType = UserType.User;
                progressDialog.show();
                if (!isUser) {
                    userType = UserType.Staff;
                }
                User user = new User("", userName, userEmail, userPassword, userBloodGroup, userType);
                auth.signUp(this, this, user);
                break;
            case R.id.textView_signIn:
                finish();
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!TextUtils.isEmpty(editText_email.getText().toString()) && !TextUtils.isEmpty(editText_email.getText().toString()) && editText_password.getText().toString().length() >= 6) {
            button_signUp.setEnabled(true);
            button_signUp.setBackgroundColor(getColor(R.color.colorPrimary));
        } else {
            button_signUp.setEnabled(false);
            button_signUp.setBackgroundColor(getColor(R.color.grey));
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}

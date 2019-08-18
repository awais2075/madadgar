package com.madadgar.activity;

import androidx.annotation.Nullable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.madadgar.R;
import com.madadgar._interface.AuthResponse;
import com.madadgar.model.User;
import com.madadgar.util.Util;

import java.util.List;

public class SignInActivity extends BaseActivity implements View.OnClickListener, AuthResponse, TextWatcher {


    private EditText editText_email;
    private EditText editText_password;
    private Button button_signIn;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViews();

        databaseReference = FirebaseDatabase.getInstance().getReference("User");
        progressDialog.show();
        onSuccess(auth.getCurrentUser());
    }

    @Override
    protected int getView() {
        return R.layout.activity_sign_in;
    }

    @Override
    protected void initViews() {
        editText_email = findViewById(R.id.editText_email);
        editText_password = findViewById(R.id.editText_password);
        button_signIn = findViewById(R.id.button_signIn);

        editText_email.addTextChangedListener(this);
        editText_password.addTextChangedListener(this);

        button_signIn.setOnClickListener(this);
        findViewById(R.id.textView_register).setOnClickListener(this);
    }

    @Override
    public void onSuccess(List list) {
        progressDialog.hide();
        user = (User) list.get(0);
        startActivity(new Intent(this, MainActivity.class).putExtra("user", user));
        finish();
    }

    @Override
    public void onSuccess(User user) {
        if (user != null) {
            fireBaseDb.view(databaseReference.orderByChild("userId").equalTo(user.getUserId()), User.class);
        } else {
            progressDialog.hide();
        }
    }

    private void clearTextFields() {
        editText_email.setText("");
        editText_password.setText("");
    }

    @Override
    public void onFailure(String message) {
        clearTextFields();
        progressDialog.hide();
        Util.showToast(this, message);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_signIn:
                progressDialog.show();
                auth.signIn(this, this, editText_email.getText().toString(), editText_password.getText().toString());
                break;
            case R.id.textView_register:
                startActivityForResult(new Intent(this, SignUpActivity.class).putExtra("userType", true), Util.REQUEST_CODE);
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!TextUtils.isEmpty(editText_email.getText().toString()) && editText_password.getText().toString().length() >= 6) {
            button_signIn.setEnabled(true);
            button_signIn.setBackgroundColor(getColor(R.color.colorPrimary));
        } else {
            button_signIn.setEnabled(false);
            button_signIn.setBackgroundColor(getColor(R.color.grey));

        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Util.REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    user = (User) data.getSerializableExtra("user");

                    editText_email.setText(user.getUserEmail());
                    editText_password.setText(user.getUserPassword());
                }
                break;
        }
    }
}

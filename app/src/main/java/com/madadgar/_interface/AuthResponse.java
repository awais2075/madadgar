package com.madadgar._interface;


import com.madadgar.model.User;

public interface AuthResponse {

    void onSuccess(User user);

    void onFailure(String message);
}

package com.madadgar.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;


/**
 * Runtime permissions specifically required for
 * Android Devices having Version Marshmallow & Above
 */
public class Permission {
    private Context context;

    public Permission(Context context) {
        this.context = context;
    }

    public boolean isPermitted(String permission) {
        int result = ActivityCompat.checkSelfPermission(context, permission);
        switch (result) {
            case PackageManager.PERMISSION_GRANTED:
                return true;
            case PackageManager.PERMISSION_DENIED:
                return false;
            default:
                return false;
        }
    }

    public void requestPermission(String permission, int permissionCode) {
        ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, permissionCode);

    }

}

package com.madadgar.util;

import android.content.Context;
import android.widget.Toast;

public class Util {

    public static final int REQUEST_CODE = 100;

    public static void showToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context, String text, int toastLength) {
        Toast.makeText(context, text, toastLength).show();
    }
}

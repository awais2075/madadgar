package com.madadgar._interface;

import android.net.Uri;

public interface FirebaseFileStorageResponse {

    void onFileUploadSuccess(Uri uri);

    void onFileUploadFailure(String failureCause);

    void onFileUploadProgress(double progress);
}

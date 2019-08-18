package com.madadgar.util;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.madadgar._interface.FirebaseFileStorageResponse;

public class FirebaseFileStorage {

    private FirebaseFileStorageResponse fileStorageResponse;

    public FirebaseFileStorage(FirebaseFileStorageResponse fileStorageResponse) {
        this.fileStorageResponse = fileStorageResponse;
    }

    public void uploadFile(final StorageReference storageReference, Uri imageUri) {
        storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        fileStorageResponse.onFileUploadSuccess(uri);
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                fileStorageResponse.onFileUploadFailure(e.getMessage());
                            }
                        });
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        fileStorageResponse.onFileUploadFailure(e.getMessage());
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        fileStorageResponse.onFileUploadProgress(progress);
                    }
                });
    }


}

package com.example.healthcoach.viewmodels;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;


public class SettingsViewModel extends ViewModel {
    private final MutableLiveData<Boolean> isUploadSuccessful = new MutableLiveData<>();
    private final StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("profile_pics");

    public LiveData<Boolean> getUploadStatus() {
        return isUploadSuccessful;
    }

    public void uploadImage(Uri filePath) {
        StorageReference ref = mStorageRef.child(UUID.randomUUID().toString());
        ref.putFile(filePath)
                .addOnSuccessListener(taskSnapshot -> {
                    ref.getDownloadUrl().addOnSuccessListener(uri -> {
                        Log.d("FirebaseUpload", "Download Uri: " + uri.toString());
                    });
                    isUploadSuccessful.setValue(true);
                })
                .addOnFailureListener(e -> {
                    isUploadSuccessful.setValue(false);
                    Log.e("FirebaseUpload", "Error uploading image: " + e.getMessage());
                });
    }



}

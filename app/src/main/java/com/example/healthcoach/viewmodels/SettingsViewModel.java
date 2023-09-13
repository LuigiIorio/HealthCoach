package com.example.healthcoach.viewmodels;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;



public class SettingsViewModel extends ViewModel {

    private final SavedStateHandle savedStateHandle;
    private final MutableLiveData<Uri> uploadedImageUri = new MutableLiveData<>();
    private final StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("profile_pics");

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ListenerRegistration profileUrlListener;

    public LiveData<Uri> getUploadedImageUri() {
        return uploadedImageUri;
    }

    public void saveProfileUrlToFirebase(String userId, String url) {
        DocumentReference userRef = db.collection("users").document(userId);
        userRef.update("profileUrl", url)
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirestoreUpdate", "Profile URL updated successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreUpdate", "Error updating profile URL: " + e.getMessage());
                });
    }

    public void fetchProfileUrlFromFirebase(String userId) {
        DocumentReference userRef = db.collection("users").document(userId);
        profileUrlListener = userRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Log.e("FirestoreListener", "Error fetching profile URL: " + e.getMessage());
                return;
            }
            if (documentSnapshot != null && documentSnapshot.exists()) {
                String url = documentSnapshot.getString("profileUrl");
                if (url != null) {
                    uploadedImageUri.setValue(Uri.parse(url));
                }
            }
        });
    }

    public void uploadImage(Uri filePath, Context context) {
        StorageReference ref = mStorageRef.child(UUID.randomUUID().toString());
        ref.putFile(filePath)
                .addOnSuccessListener(taskSnapshot -> {
                    ref.getDownloadUrl().addOnSuccessListener(uri -> {
                        uploadedImageUri.setValue(uri);
                        cacheLastUri(context, uri);
                    }).addOnFailureListener(e -> {
                        Log.e("FirebaseUpload", "Error getting download URL: " + e.getMessage());
                    });
                })
                .addOnFailureListener(e -> {
                    uploadedImageUri.setValue(null);
                    Log.e("FirebaseUpload", "Error uploading image: " + e.getMessage());
                });
    }

    public void cacheLastUri(Context context, Uri uri) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lastUri", uri.toString());
        editor.apply();
    }

    public Uri getLastCachedUri(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        String lastUri = sharedPreferences.getString("lastUri", null);
        return lastUri != null ? Uri.parse(lastUri) : null;
    }

    public SettingsViewModel(SavedStateHandle savedStateHandle, Context context) {
        this.savedStateHandle = savedStateHandle;
        Uri lastCachedUri = getLastCachedUri(context);
        if (lastCachedUri != null) {
            uploadedImageUri.setValue(lastCachedUri);
        }
    }

    public Uri getLastSavedUri() {
        String uriString = savedStateHandle.get("lastSavedUri");
        return uriString != null ? Uri.parse(uriString) : null;
    }

    public void setLastSavedUri(Uri uri) {
        savedStateHandle.set("lastSavedUri", uri.toString());
    }

    public boolean logoutUser() {
        try {
            FirebaseAuth.getInstance().signOut();
            return true;
        } catch (Exception e) {
            Log.e("LogoutError", "Failed to logout: " + e.getMessage());
            return false;
        }
    }
}
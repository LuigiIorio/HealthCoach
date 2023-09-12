package com.example.healthcoach.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.healthcoach.models.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.events.Event;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;


public class HomeActivityViewModel extends ViewModel {

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private final MutableLiveData<UserProfile> userProfileLiveData = new MutableLiveData<>();
    private final MutableLiveData<com.example.healthcoach.viewmodels.Event<Boolean>> logoutState = new MutableLiveData<>();

    private ListenerRegistration userProfileListener;


    public LiveData<com.example.healthcoach.viewmodels.Event<Boolean>> getLogoutState() {
        return logoutState;
    }



    public LiveData<UserProfile> getUserProfile() {
        if (auth.getCurrentUser() != null) {
            if (userProfileListener == null) {
                DocumentReference userProfileRef = firestore.collection("users")
                        .document(auth.getCurrentUser().getUid());

                userProfileListener = userProfileRef.addSnapshotListener((snapshot, error) -> {
                    if (error != null) {
                        // Gestisci l'errore
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        UserProfile userProfile = snapshot.toObject(UserProfile.class);
                        userProfileLiveData.setValue(userProfile);
                    }
                });
            }
        }

        return userProfileLiveData;
    }

    @Override
    protected void onCleared() {
        if (userProfileListener != null) {
            userProfileListener.remove();
        }
    }

    public void logoutUser() {
        try {
            FirebaseAuth.getInstance().signOut();
            logoutState.setValue(new com.example.healthcoach.viewmodels.Event<Boolean>(true));
        } catch (Exception e) {
            logoutState.setValue(new com.example.healthcoach.viewmodels.Event<Boolean>(false));
        }
    }





}

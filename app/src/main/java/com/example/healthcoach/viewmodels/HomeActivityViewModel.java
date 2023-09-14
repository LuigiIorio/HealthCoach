package com.example.healthcoach.viewmodels;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.healthcoach.activities.HomeActivity;
import com.example.healthcoach.fragments.FragmentSetting;
import com.example.healthcoach.models.UserProfile;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.HistoryClient;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.events.Event;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;


public class HomeActivityViewModel extends ViewModel {

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private final MutableLiveData<UserProfile> userProfileLiveData = new MutableLiveData<>();
    private MutableLiveData<Uri> profileImageUri = new MutableLiveData<>();
    private final MutableLiveData<com.example.healthcoach.viewmodels.Event<Boolean>> logoutState = new MutableLiveData<>();

    private MutableLiveData<Integer> stepCount = new MutableLiveData<>();

    public MutableLiveData<Integer> getStepCount() {
        return stepCount;
    }

    private ListenerRegistration userProfileListener;

    public HomeActivityViewModel() {

        fetchUserData();

    }

    private void fetchUserData() {

        DatabaseReference reference = database.getReference().child("users").child(auth.getCurrentUser().getUid());

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                userProfileLiveData.setValue(snapshot.getValue(UserProfile.class));
                getProfileImage();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public LiveData<Uri> getProfileImage() {

        StorageReference profileImageRef = firebaseStorage.getReference().child("users/" + auth.getCurrentUser().getUid() + ".jpg");

        // Recupera l'URL dell'immagine
        profileImageRef.getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    userProfileLiveData.getValue().setImage(uri.toString());
                    profileImageUri.setValue(uri);
                })
                .addOnFailureListener(exception -> {
                    // Si è verificato un errore durante il recupero dell'URL dell'immagine
                    userProfileLiveData.getValue().setImage(null); // Imposta il LiveData su null in caso di errore
                    profileImageUri.setValue(null);
                });

        return profileImageUri;

    }


    public LiveData<com.example.healthcoach.viewmodels.Event<Boolean>> getLogoutState() {
        return logoutState;
    }

    public void fetchTodaySteps(Context context) {
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(context);
        if (googleSignInAccount != null) {
            HistoryClient historyClient = Fitness.getHistoryClient(context, googleSignInAccount);

            // Set midnight of the current day
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            long startTime = cal.getTimeInMillis();

            // Set current time
            long endTime = System.currentTimeMillis();

            DataReadRequest readRequest = new DataReadRequest.Builder()
                    .read(DataType.TYPE_STEP_COUNT_DELTA)
                    .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                    .build();

            historyClient.readData(readRequest)
                    .addOnSuccessListener(dataReadResponse -> {
                        int totalSteps = 0;
                        for (DataPoint dp : dataReadResponse.getDataSet(DataType.TYPE_STEP_COUNT_DELTA).getDataPoints()) {
                            for(Field field : dp.getDataType().getFields()) {
                                int steps = dp.getValue(field).asInt();
                                totalSteps += steps;
                                Log.e("StepCount", ""+steps);
                            }
                        }
                        stepCount.postValue(totalSteps);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("HomeActivityViewModel", "Failed to read steps", e);
                    });
        } else {
            Log.e("HomeActivityViewModel", "User is not signed in");
        }
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

    public void updateProfilePic(Uri uri) {

        StorageReference imageReference = firebaseStorage.getReference("users")
                .child(userProfileLiveData.getValue().getUid() + ".jpg");

        imageReference.putFile(uri).addOnSuccessListener(taskSnapshot -> {

            profileImageUri.setValue(uri);
            userProfileLiveData.getValue().setImage(String.valueOf(uri));

        });

    }

    public void updateUser(UserProfile user) {

        DatabaseReference usersReference = database.getReference("users");

        userProfileLiveData.setValue(user);
        usersReference.child(userProfileLiveData.getValue().getUid()).setValue(userProfileLiveData.getValue());


    }

    public void updatePassword(String password) {

        auth.getCurrentUser().updatePassword(password);

    }

    public LiveData<UserProfile> getUser() {

        return userProfileLiveData;

    }

}

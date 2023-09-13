package com.example.healthcoach.viewmodels;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.events.Event;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;


public class HomeActivityViewModel extends ViewModel {

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private final MutableLiveData<UserProfile> userProfileLiveData = new MutableLiveData<>();
    private final MutableLiveData<com.example.healthcoach.viewmodels.Event<Boolean>> logoutState = new MutableLiveData<>();

    private MutableLiveData<Integer> stepCount = new MutableLiveData<>();

    public MutableLiveData<Integer> getStepCount() {
        return stepCount;
    }

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


}

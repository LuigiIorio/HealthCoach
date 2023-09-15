package com.example.healthcoach.viewmodels;

import android.app.Activity;
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
import com.example.healthcoach.activities.LoginActivity;
import com.example.healthcoach.fragments.FragmentSetting;
import com.example.healthcoach.models.UserProfile;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.HistoryClient;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.DataUpdateRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
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
import java.util.List;
import java.util.concurrent.TimeUnit;


public class HomeActivityViewModel extends ViewModel {

    private static final String TAG = "GMERGE";
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

    private MutableLiveData<Integer> steps = new MutableLiveData<>();
    private MutableLiveData<Integer> water = new MutableLiveData<>();
    private MutableLiveData<Integer> kcal = new MutableLiveData<>();

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
                            for (Field field : dp.getDataType().getFields()) {
                                int steps = dp.getValue(field).asInt();
                                totalSteps += steps;
                                Log.e("StepCount", "" + steps);
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

    public void logoutUser(Activity activity) {
        try {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(activity, LoginActivity.class);
            activity.startActivity(intent);
            activity.finish();
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

    public void updateFitValues(Context context) {

        updateFitValue(context, DataType.TYPE_STEP_COUNT_DELTA);
        updateFitValue(context, DataType.TYPE_HYDRATION);
        updateFitValue(context, DataType.TYPE_CALORIES_EXPENDED);

    }

    public void updateFitValue(Context context, DataType type) {

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(type, type)
                .bucketByTime(1, TimeUnit.DAYS) // Add this line to specify bucketing
                .setTimeRange(getStartTimeOfToday(), getEndTimeOfToday(), TimeUnit.MILLISECONDS)
                .build();

        // Execute the query
        Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context))
                .readData(readRequest)
                .addOnSuccessListener(dataReadResponse -> {
                    for (Bucket bucket : dataReadResponse.getBuckets()) {
                        DataSet dataSet = bucket.getDataSet(type);
                        if (dataSet != null) {
                            for (DataPoint dataPoint : dataSet.getDataPoints()) {
                                updateLiveData(type, dataPoint.getValue(getTypeField(type)));
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                });
    }


    public void uploadWaterIntake(Context context, int value) {

        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_HYDRATION, FitnessOptions.ACCESS_WRITE)
                .build();


        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getAccountForExtension(context, fitnessOptions);

        // Create a data source
        DataSource dataSource = new DataSource.Builder()
                .setAppPackageName(context)
                .setDataType(DataType.TYPE_HYDRATION)
                .setStreamName("$TAG - water intake")
                .setType(DataSource.TYPE_RAW)
                .build();

        Calendar calendar = Calendar.getInstance();
        long now = calendar.getTimeInMillis();

        // For each data point, specify a start time, end time, and the
        // data value -- in this case, 950 new steps.
        DataPoint dataPoint = DataPoint.builder(dataSource)
                .setField(getTypeField(DataType.TYPE_HYDRATION), value)
                .setTimestamp(now, TimeUnit.MILLISECONDS)
                .build();

        DataSet dataSet = DataSet.builder(dataSource)
                .add(dataPoint)
                .build();

        Fitness.getHistoryClient(context, googleSignInAccount)
                .insertData(dataSet)
                .addOnSuccessListener(unused -> {
                    water.setValue(water.getValue() + value);
                });

    }

    public void uploadKcalUsed(Context context, int value) {

        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_WRITE)
                .build();


        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getAccountForExtension(context, fitnessOptions);

        // Create a data source
        DataSource dataSource = new DataSource.Builder()
                .setAppPackageName(context)
                .setDataType(DataType.TYPE_CALORIES_EXPENDED)
                .setStreamName("$TAG - water intake")
                .setType(DataSource.TYPE_RAW)
                .build();

        Calendar calendar = Calendar.getInstance();
        long now = calendar.getTimeInMillis();

        // For each data point, specify a start time, end time, and the
        // data value -- in this case, 950 new steps.
        DataPoint dataPoint = DataPoint.builder(dataSource)
                .setField(getTypeField(DataType.TYPE_CALORIES_EXPENDED), value)
                .setTimestamp(now, TimeUnit.MILLISECONDS)
                .build();

        DataSet dataSet = DataSet.builder(dataSource)
                .add(dataPoint)
                .build();

        Fitness.getHistoryClient(context, googleSignInAccount)
                .insertData(dataSet)
                .addOnSuccessListener(unused -> {
                    kcal.setValue(kcal.getValue() + value);
                });

    }

    private Field getTypeField(DataType type) {

        if (type.equals(DataType.TYPE_STEP_COUNT_DELTA)) {
            return Field.FIELD_STEPS;
        } else if (type.equals(DataType.TYPE_HYDRATION)) {
            return Field.FIELD_VOLUME;
        } else if (type.equals(DataType.TYPE_CALORIES_EXPENDED)) {
            return Field.FIELD_CALORIES;
        }

        return null;

    }

    private void updateLiveData(DataType type, Value value) {

        if (type.equals(DataType.TYPE_STEP_COUNT_DELTA)) {
            steps.setValue(value.asInt());
        } else if (type.equals(DataType.TYPE_HYDRATION)) {
            water.setValue((int) value.asFloat());
        } else if (type.equals(DataType.TYPE_CALORIES_EXPENDED)) {
            kcal.setValue((int) value.asFloat());
        }

    }

    private static long getStartTimeOfToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    // Ottieni l'orario di fine di oggi (23:59:59)
    private static long getEndTimeOfToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    public void mergeGoogleAccount(Context context) {

        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(context);

        AuthCredential credential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);

        auth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = task.getResult().getUser();
                        Log.d(TAG, "Unione riuscita: " + firebaseUser.getUid());
                    } else {
                        Exception exception = task.getException();
                        Log.w(TAG, "Unione non riuscita", exception);
                    }
                });


    }

    public boolean checkGoogleMerge(Context context) {

        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            // Ottieni i provider di accesso collegati all'account Firebase
            List<? extends UserInfo> providers = currentUser.getProviderData();

            for (UserInfo userInfo : providers) {
                String providerId = userInfo.getProviderId();

                if (providerId.equals(GoogleAuthProvider.PROVIDER_ID)) {
                    Log.d(TAG, "L'account Firebase è collegato a un account Google");
                    return true;
                }
            }
        }

        return false;

    }

    public MutableLiveData<Integer> getSteps() {
        return steps;
    }

    public MutableLiveData<Integer> getWater() {
        return water;
    }

    public MutableLiveData<Integer> getKcal() {
        return kcal;
    }
}

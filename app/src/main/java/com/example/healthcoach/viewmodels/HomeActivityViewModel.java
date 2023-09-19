package com.example.healthcoach.viewmodels;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;
import com.example.healthcoach.activities.LoginActivity;
import com.example.healthcoach.models.GoogleFitDailyData;
import com.example.healthcoach.models.UserProfile;
import com.example.healthcoach.recordingapi.Hydration;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
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
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class HomeActivityViewModel extends ViewModel {

    private static final String TAG = "GMERGE";
    private static final int DAYS_TO_FETCH = 7;
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private final MutableLiveData<UserProfile> userProfileLiveData = new MutableLiveData<>();
    private MutableLiveData<Uri> profileImageUri = new MutableLiveData<>();
    private final MutableLiveData<com.example.healthcoach.viewmodels.Event<Boolean>> logoutState = new MutableLiveData<>();
    private MutableLiveData<Integer> stepCount = new MutableLiveData<>();
    private MutableLiveData<List<GoogleFitDailyData>> history = new MutableLiveData<>();

    private MutableLiveData<Float> hydrationData = new MutableLiveData<>();

    public MutableLiveData<Integer> getStepCount() {
        return stepCount;
    }

    public void setHydrationData(float hydration) {
        hydrationData.setValue(hydration);
    }

    public LiveData<Float> getHydrationData() {
        return hydrationData;
    }


    private ListenerRegistration userProfileListener;

    private MutableLiveData<Integer> steps = new MutableLiveData<>();
    private MutableLiveData<Integer> water = new MutableLiveData<>();
    private MutableLiveData<Integer> kcal = new MutableLiveData<>();

    public HomeActivityViewModel() {

        fetchUserData();

    }

    public void fetchTodayHydration(Context context) {
        long endTime = System.currentTimeMillis();
        long startTime = endTime - (24 * 60 * 60 * 1000); // Start from midnight to now

        Hydration hydration = new Hydration(context);
        hydration.readHydrationData(startTime, endTime, dataReadResponse -> {
            float totalHydration = 0;
            for (DataSet dataSet : dataReadResponse.getDataSets()) {
                for (DataPoint dataPoint : dataSet.getDataPoints()) {
                    for (Field field : dataPoint.getDataType().getFields()) {
                        float hydrationValue = dataPoint.getValue(field).asFloat();
                        totalHydration += hydrationValue;
                    }
                }
            }
            // Set the total hydration data in ViewModel
            setHydrationData(totalHydration);
        });
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

        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(context);

        if (googleSignInAccount == null) {
            Toast.makeText(context,
                    "Google Account not found!\nConsider merging one to unlock full version",
                    Toast.LENGTH_LONG).show();
            return;
        }

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
                .setField(Field.FIELD_VOLUME, (float) value)
                .setTimestamp(now, TimeUnit.MILLISECONDS)
                .build();

        DataSet dataSet = DataSet.builder(dataSource)
                .add(dataPoint)
                .build();

        Fitness.getHistoryClient(context, googleSignInAccount)
                .insertData(dataSet)
                .addOnSuccessListener(unused -> {
                    water.setValue(water.getValue() + value);
                    Log.i("Acqua Input", "Acqua caricata con successo");
                }).addOnFailureListener(e -> {
                    Log.i("Acqua", "Fallimento");
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
                .setField(Field.FIELD_CALORIES, (float) value)
                .setTimestamp(now, TimeUnit.MILLISECONDS)
                .build();

        DataSet dataSet = DataSet.builder(dataSource)
                .add(dataPoint)
                .build();

        Fitness.getHistoryClient(context, googleSignInAccount)
                .insertData(dataSet)
                .addOnSuccessListener(unused -> {
                    kcal.setValue(kcal.getValue() + value);
                    Log.i("Lavoro upload", "Cantonment con successo!");
                })
                .addOnFailureListener(e -> {

                    Log.i("Lavoro upload", "Caricamento con disprezzo!");
                    e.printStackTrace();

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

        if (googleSignInAccount != null) {
            try {
                String idToken = googleSignInAccount.getIdToken();

                AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

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

            } catch (NullPointerException e) {
                Log.e("Merge", e.toString());
            }
        } else {

            Log.e("Merge", "Not connected to Google Account");

        }





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

    public void fetchData(Context context, AnyChartView lineChart) {

        if (GoogleSignIn.getLastSignedInAccount(context) == null)
            return;

        if (history.getValue() == null) {

            List<GoogleFitDailyData> data = new ArrayList<>();
            for (int i = 0; i < 7; i++)
                data.add(new GoogleFitDailyData());

            history.setValue(data);

        }

        // Impostazione dell'intervallo di tempo
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_YEAR, -DAYS_TO_FETCH);
        long startTime = calendar.getTimeInMillis();

        // Creazione della richiesta per i dati di Google Fit
        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA)
                .aggregate(DataType.TYPE_HYDRATION)
                .aggregate(DataType.TYPE_CALORIES_EXPENDED)
                .bucketByTime(DAYS_TO_FETCH, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        HistoryClient historyClient = Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context));

        // Eseguire la richiesta
        historyClient.readData(readRequest)
                .addOnSuccessListener(dataReadResult -> {
                    List<Bucket> buckets = dataReadResult.getBuckets();
                    parseBuckets(buckets);
                    inizialiseLineChart(lineChart);
                });
    }

    private void parseBuckets(List<Bucket> buckets) {

        for (Bucket bucket : buckets) {

            List<DataSet> dataSets = bucket.getDataSets();

            for (DataSet dataSet : dataSets) {
                for (DataPoint dataPoint : dataSet.getDataPoints()) {

                    GoogleFitDailyData data = history.getValue()
                            .get((int) (DAYS_TO_FETCH - 1 - calculateDaysSince(
                                    dataPoint.getEndTime(TimeUnit.MILLISECONDS) -
                                            dataPoint.getStartTime(TimeUnit.MILLISECONDS)
                            )));

                    if(dataPoint.getDataType() == DataType.TYPE_STEP_COUNT_DELTA)
                        data.setSteps(dataPoint.getValue(Field.FIELD_STEPS).asInt());
                    else if(dataPoint.getDataType() == DataType.TYPE_HYDRATION)
                        data.setHydration(dataPoint.getValue(Field.FIELD_VOLUME).asFloat());
                    else if(dataPoint.getDataType() == DataType.TYPE_CALORIES_EXPENDED)
                        data.setCalories(dataPoint.getValue(Field.FIELD_CALORIES).asFloat());

                }
            }
        }

    }

    public static long calculateDaysSince(long timestampInMillis) {
        long currentTimeInMillis = System.currentTimeMillis();
        long timeDifference = currentTimeInMillis - timestampInMillis;
        return TimeUnit.MILLISECONDS.toDays(timeDifference);
    }

    public void inizialiseLineChart(AnyChartView lineChart) {

        Cartesian cartesian = AnyChart.line();

        cartesian.animation(true);

        cartesian.padding(10d, 20d, 5d, 20d);

        cartesian.crosshair().enabled(true);
        cartesian.crosshair()
                .yLabel(true)
                .yStroke((Stroke) null, null, null, (String) null, (String) null);

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);

        cartesian.yAxis(0).title("% Completed");
        cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);

        List<DataEntry> seriesData = new ArrayList<>();
        UserProfile user = userProfileLiveData.getValue();

        for(int i = history.getValue().size() - 1; i >= 0; i--) {

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, -i);

            GoogleFitDailyData data = history.getValue().get(i);

            seriesData.add(new CustomDataEntry(
                    formatToDDMMM(calendar),
                    (data.getSteps() / user.getDailySteps()) * 100,
                    (data.getHydration() / user.getDailyWater()) * 100,
                    (data.getCalories() / user.getDailyKcal()) * 100
            ));

        }

        Set set = Set.instantiate();
        set.data(seriesData);
        Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value' }");
        Mapping series2Mapping = set.mapAs("{ x: 'x', value: 'value2' }");
        Mapping series3Mapping = set.mapAs("{ x: 'x', value: 'value3' }");

        Line series1 = cartesian.line(series1Mapping);
        series1.name("Steps");
        series1.hovered().markers().enabled(true);
        series1.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series1.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);

        Line series2 = cartesian.line(series2Mapping);
        series2.name("Water");
        series2.hovered().markers().enabled(true);
        series2.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series2.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);

        Line series3 = cartesian.line(series3Mapping);
        series3.name("Calories");
        series3.hovered().markers().enabled(true);
        series3.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series3.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);

        cartesian.legend().enabled(true);
        cartesian.legend().fontSize(13d);
        cartesian.legend().padding(0d, 0d, 10d, 0d);

        lineChart.setChart(cartesian);

    }

    public static String formatToDDMMM(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM", Locale.getDefault());
        return sdf.format(date);
    }

    // Metodo per formattare una data nel formato "DD MMM"
    public static String formatToDDMMM(Calendar calendar) {
        return formatToDDMMM(calendar.getTime());
    }

    private class CustomDataEntry extends ValueDataEntry {

        CustomDataEntry(String x, Number value, Number value2, Number value3) {
            super(x, value);
            setValue("value2", value2);
            setValue("value3", value3);
        }

    }

}

package com.example.healthcoach.fragments;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.widget.Toast;

import com.example.healthcoach.R;
import com.example.healthcoach.recordingapi.BodyFat;
import com.example.healthcoach.recordingapi.CaloriesExpended;
import com.example.healthcoach.recordingapi.DistanceDelta;
import com.example.healthcoach.recordingapi.Hydration;
import com.example.healthcoach.recordingapi.StepCountDelta;
import com.example.healthcoach.recordingapi.Weight;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;



public class FragmentHistory extends Fragment {
    private Hydration hydration;
    private Weight weight;
    private BodyFat bodyFat;
    private TextView historyTextView;
    private Spinner dataTypeSpinner;
    private Spinner insertDataTypeSpinner;
    private EditText dataInputEditText;
    private Button fetchDataButton;
    private Button insertDataButton;
    private int selectedYear;
    private int selectedMonth;
    private int selectedDay;

    private static final int PERMISSIONS_REQUEST_CODE = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen3, container, false);

        requestPermissions();

        initializeVariables(view);
        setupCalendarView(view);
        setupSpinner(view);
        setupFetchDataButton();
        setupInsertDataButton();

        return view;
    }

    private void requestPermissions() {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION
        };

        if (!hasPermissions(requireContext(), permissions)) {
            requestPermissions(permissions, PERMISSIONS_REQUEST_CODE);
        }
    }

    private void setupInsertDataButton() {
        insertDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedType = insertDataTypeSpinner.getSelectedItem().toString();
                if (dataInputEditText.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter a value", Toast.LENGTH_SHORT).show();
                    return;
                }
                float inputValue = Float.parseFloat(dataInputEditText.getText().toString());

                if ("Hydration".equals(selectedType)) {
                    insertHydrationData(inputValue);
                } else if ("BodyFat".equals(selectedType)) {
                    insertBodyFatData(inputValue);
                } else if ("Weight".equals(selectedType)) {
                    insertWeightData(inputValue);
                }
            }
        });
    }

    private void insertHydrationData(float hydrationValue) {
        hydration.insertWaterIntake(hydrationValue);
        Toast.makeText(getActivity(), "Inserted hydration data successfully", Toast.LENGTH_SHORT).show();
    }

    private void insertBodyFatData(float bodyFatValue) {
        bodyFat.insertBodyFatData(getActivity(), bodyFatValue, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("BodyFat", "Body fat data inserted successfully");
                Toast.makeText(getActivity(), "Inserted body fat data successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void insertWeightData(float weightValue) {
        weight.insertWeightData(weightValue, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Weight", "Weight data inserted successfully");
            }
        });
    }


    private static boolean hasPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void initializeVariables(View view) {
        hydration = new Hydration(getActivity());
        weight = new Weight(getActivity());
        bodyFat = new BodyFat(getActivity());
        historyTextView = view.findViewById(R.id.historyTextView);
        dataTypeSpinner = view.findViewById(R.id.fetchDataTypeSpinner);
        fetchDataButton = view.findViewById(R.id.fetchDataButton);
        insertDataTypeSpinner = view.findViewById(R.id.insertDataTypeSpinner);
        dataInputEditText = view.findViewById(R.id.dataInputEditText);
        insertDataButton = view.findViewById(R.id.insertDataButton);

        Calendar calendar = Calendar.getInstance();
        selectedYear = calendar.get(Calendar.YEAR);
        selectedMonth = calendar.get(Calendar.MONTH);
        selectedDay = calendar.get(Calendar.DAY_OF_MONTH);
    }

    private void setupCalendarView(View view) {
        CalendarView calendarView = view.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            selectedYear = year;
            selectedMonth = month;
            selectedDay = dayOfMonth;
        });
    }

    private void setupSpinner(View view) {
        ArrayAdapter<CharSequence> fetchAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.data_types, android.R.layout.simple_spinner_item);
        fetchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataTypeSpinner.setAdapter(fetchAdapter);

        ArrayAdapter<CharSequence> insertAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.data_types2, android.R.layout.simple_spinner_item);
        insertAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        insertDataTypeSpinner.setAdapter(insertAdapter);
    }

    private void setupFetchDataButton() {
        fetchDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long startTime = getDateInMillis(selectedYear, selectedMonth, selectedDay);
                long endTime = startTime + 24 * 60 * 60 * 1000;
                String selectedType = dataTypeSpinner.getSelectedItem().toString();

                if ("Weight".equals(selectedType)) {
                    updateDataWeight(selectedType, startTime, endTime);
                } else if ("BodyFat".equals(selectedType)) {
                    updateDataBodyFat(selectedType, startTime, endTime);
                } else if ("Hydration".equals(selectedType)) {
                    updateDataHydration(selectedType, startTime, endTime);
                } else if ("Steps".equals(selectedType)) {
                    updateDataSteps(selectedType, startTime, endTime);
                } else if ("Kcal".equals(selectedType)) {
                    updateDataCaloriesExpended(selectedType, startTime, endTime);
                } else if ("Distance".equals(selectedType)) {
                    updateDataDistance(selectedType, startTime, endTime);
                }

            }
        });
    }
    private void updateDataCaloriesExpended(String type, long startTime, long endTime) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());
        if (account == null) {
            historyTextView.setText("Not signed in");
            return;
        }

        if ("Kcal".equals(type)) {
            new CaloriesExpended(getActivity(), account).readCaloriesData(getActivity(), account, startTime, endTime, new OnSuccessListener<DataReadResponse>() {
                @Override
                public void onSuccess(DataReadResponse dataReadResponse) {
                    float totalCalories = 0;
                    for (DataSet dataSet : dataReadResponse.getDataSets()) {
                        for (DataPoint point : dataSet.getDataPoints()) {
                            for (Field field : point.getDataType().getFields()) {
                                totalCalories += point.getValue(field).asFloat();
                            }
                        }
                    }
                    if (totalCalories == 0) {
                        historyTextView.setText("No data available for the selected date");
                    } else {
                        historyTextView.setText(String.format("Total Calories Expended: %.2f kcal", totalCalories));
                    }
                }
            });
        }
    }
    private void updateDataDistance(String type, long startTime, long endTime) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());
        if (account == null) {
            historyTextView.setText("Not signed in");
            return;
        }

        if ("Distance".equals(type)) {
            new DistanceDelta(getActivity(), account).readDistanceData(getActivity(), startTime, endTime, new OnSuccessListener<DataReadResponse>() {
                @Override
                public void onSuccess(DataReadResponse dataReadResponse) {
                    float totalDistance = 0;
                    for (DataSet dataSet : dataReadResponse.getDataSets()) {
                        for (DataPoint point : dataSet.getDataPoints()) {
                            for (Field field : point.getDataType().getFields()) {
                                if (field.getName().equals("distance")) {
                                    totalDistance += point.getValue(field).asFloat();
                                }
                            }
                        }
                    }
                    historyTextView.setText(String.format("Total Distance Covered: %.2f meters", totalDistance));
                }
            });
        }
    }
    private void updateDataSteps(String type, long startTime, long endTime) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());
        if (account == null) {
            historyTextView.setText("Not signed in");
            return;
        }

        if ("Steps".equals(type)) {
            StepCountDelta stepCountDelta = new StepCountDelta(getActivity(), account, getActivity());
            stepCountDelta.readStepsForRange(startTime, endTime, new OnSuccessListener<DataReadResponse>() {
                @Override
                public void onSuccess(DataReadResponse dataReadResponse) {
                    int totalSteps = 0;
                    if (dataReadResponse.getBuckets().size() > 0) {
                        for (Bucket bucket : dataReadResponse.getBuckets()) {
                            DataSet dataSet = bucket.getDataSet(DataType.AGGREGATE_STEP_COUNT_DELTA);
                            for (DataPoint point : dataSet.getDataPoints()) {
                                for (Field field : point.getDataType().getFields()) {
                                    int steps = point.getValue(field).asInt();
                                    totalSteps += steps;
                                }
                            }
                        }
                    }
                    historyTextView.setText(String.format("Total steps: %d", totalSteps));
                }
            });
        }
    }
    private void updateDataWeight(String type, long startTime, long endTime){
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());
        if (account == null) {
            historyTextView.setText("Not signed in");
            return;
        }

        if ("Weight".equals(type)) {
            weight.readWeightData(startTime, endTime, new OnSuccessListener<DataReadResponse>() {
                @Override
                public void onSuccess(DataReadResponse dataReadResponse) {
                    float latestWeight = 0;
                    long latestTime = 0;
                    for (DataSet dataSet : dataReadResponse.getDataSets()) {
                        for (DataPoint point : dataSet.getDataPoints()) {
                            for (Field field : point.getDataType().getFields()) {
                                float weightValue = point.getValue(field).asFloat();
                                long endTime = point.getEndTime(TimeUnit.MILLISECONDS);
                                if (endTime > latestTime) {
                                    latestTime = endTime;
                                    latestWeight = weightValue;
                                }
                            }
                        }
                    }
                    historyTextView.setText(String.format("Latest weight: %.2f kg", latestWeight));
                }
            });
        }

    }

    private void updateDataBodyFat(String type, long startTime, long endTime){
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());
        if (account == null) {
            historyTextView.setText("Not signed in");
            return;
        }

        if ("BodyFat".equals(type)) {
            bodyFat.readBodyFatData(getActivity(), account, startTime, endTime, new OnSuccessListener<DataReadResponse>() {
                @Override
                public void onSuccess(DataReadResponse dataReadResponse) {
                    float latestBodyFat = 0;
                    long latestTime = 0;
                    for (DataSet dataSet : dataReadResponse.getDataSets()) {
                        for (DataPoint point : dataSet.getDataPoints()) {
                            for (Field field : point.getDataType().getFields()) {
                                float bodyFatValue = point.getValue(field).asFloat();
                                long endTime = point.getEndTime(TimeUnit.MILLISECONDS);
                                if (endTime > latestTime) {
                                    latestTime = endTime;
                                    latestBodyFat = bodyFatValue;
                                }
                            }
                        }
                    }
                    historyTextView.setText(String.format("Latest body fat: %.2f%%", latestBodyFat));
                }
            });
        }

    }
    private void updateDataHydration(String type, long startTime, long endTime) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());
        if (account == null) {
            historyTextView.setText("Not signed in");
            return;
        }

        if ("Hydration".equals(type)) {
            hydration.readHydrationData(startTime, endTime, new OnSuccessListener<DataReadResponse>() {
                @Override
                public void onSuccess(DataReadResponse dataReadResponse) {
                    float totalHydration = 0;
                    for (DataSet dataSet : dataReadResponse.getDataSets()) {
                        for (DataPoint point : dataSet.getDataPoints()) {
                            for (Field field : point.getDataType().getFields()) {
                                float hydrationValue = point.getValue(field).asFloat();
                                totalHydration += hydrationValue;
                            }
                        }
                    }
                    historyTextView.setText(String.format("Total water intake: %.2f ml", totalHydration));
                }
            });
        }

    }

    private long getDateInMillis(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar.getTimeInMillis();
    }


}


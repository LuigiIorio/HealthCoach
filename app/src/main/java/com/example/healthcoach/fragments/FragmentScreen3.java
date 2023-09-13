package com.example.healthcoach.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.healthcoach.R;
import com.example.healthcoach.recordingapi.BodyFat;
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




public class FragmentScreen3 extends Fragment {

    private Hydration hydration;
    private Weight weight;
    private BodyFat bodyFat; // added this
    private TextView historyTextView;
    private Spinner dataTypeSpinner;
    private Button fetchDataButton;
    private int selectedYear;
    private int selectedMonth;
    private int selectedDay;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen3, container, false);
        initializeVariables(view);
        setupCalendarView(view);
        setupSpinner(view);
        setupFetchDataButton();
        return view;
    }

    private void initializeVariables(View view) {
        hydration = new Hydration(getActivity());
        weight = new Weight(getActivity());
        bodyFat = new BodyFat(getActivity());
        historyTextView = view.findViewById(R.id.historyTextView);
        dataTypeSpinner = view.findViewById(R.id.dataTypeSpinner);
        fetchDataButton = view.findViewById(R.id.fetchDataButton);

        // Initialize selectedYear, selectedMonth, and selectedDay
        Calendar calendar = Calendar.getInstance();
        selectedYear = calendar.get(Calendar.YEAR);
        selectedMonth = calendar.get(Calendar.MONTH);
        selectedDay = calendar.get(Calendar.DAY_OF_MONTH);
    }

    private void setupCalendarView(View view) {
        CalendarView calendarView = view.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectedYear = year;
                selectedMonth = month;
                selectedDay = dayOfMonth;
            }
        });
    }

    private void setupSpinner(View view) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.data_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataTypeSpinner.setAdapter(adapter);
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
                }

                // Add additional conditions for other data types like Steps, Distance, Kcal
            }
        });
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
                    historyTextView.setText(String.format("Total water intake: %.2f L", totalHydration));
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

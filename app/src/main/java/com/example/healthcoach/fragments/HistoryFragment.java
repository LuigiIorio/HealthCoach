package com.example.healthcoach.fragments;

import com.example.healthcoach.viewmodels.BodyFatViewModel;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.widget.Toast;

import com.example.healthcoach.R;
import com.example.healthcoach.recordingapi.BodyFat;
import com.example.healthcoach.recordingapi.Calories;
import com.example.healthcoach.recordingapi.Distance;
import com.example.healthcoach.recordingapi.Hydration;
import com.example.healthcoach.recordingapi.StepCount;
import com.example.healthcoach.recordingapi.Weight;
import com.example.healthcoach.viewmodels.BodyFatViewModel;
import com.example.healthcoach.viewmodels.CaloriesViewModel;
import com.example.healthcoach.viewmodels.DistanceViewModel;
import com.example.healthcoach.viewmodels.HydrationViewModel;
import com.example.healthcoach.viewmodels.StepViewModel;
import com.example.healthcoach.viewmodels.WeightViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;


public class HistoryFragment extends Fragment {
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
    private BodyFatViewModel bodyFatViewModel;
    private WeightViewModel weightViewModel;

    private HydrationViewModel hydrationViewModel;

    private static final int PERMISSIONS_REQUEST_CODE = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        requestPermissions();

        initializeVariables(view);
        setupCalendarView(view);
        setupSpinner(view);
        setupFetchDataButton();
        setupInsertDataButton();

        // Initialize BodyFatViewModel
        bodyFatViewModel = new ViewModelProvider(this).get(BodyFatViewModel.class);
        bodyFatViewModel.initialize(getActivity());

        // Initialize WeightViewModel
        weightViewModel = new ViewModelProvider(this).get(WeightViewModel.class);
        weightViewModel.initialize(getActivity());

        // Initialize HydrationViewModel
        hydrationViewModel = new ViewModelProvider(this).get(HydrationViewModel.class);
        hydrationViewModel.initialize(getActivity());

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
                long[] timeBounds = getDateInMillis(selectedYear, selectedMonth, selectedDay);
                long startTime = timeBounds[0];
                long endTime = timeBounds[1];

                String selectedType = insertDataTypeSpinner.getSelectedItem().toString();
                if (dataInputEditText.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter a value", Toast.LENGTH_SHORT).show();
                    return;
                }
                float inputValue = Float.parseFloat(dataInputEditText.getText().toString());

                if ("Hydration".equals(selectedType)) {
                    insertHydrationData(inputValue, startTime, endTime);
                } else if ("BodyFat".equals(selectedType)) {
                    insertBodyFatData(inputValue, startTime, endTime);
                } else if ("Weight".equals(selectedType)) {
                    insertWeightData(inputValue, startTime, endTime);
                }
            }
        });
    }


    private void insertHydrationData(float hydrationValue, long startTime, long endTime) {
        hydrationViewModel.addWater(hydrationValue, startTime, endTime);
        Toast.makeText(getActivity(), "Inserted hydration data successfully", Toast.LENGTH_SHORT).show();
    }

    private void insertBodyFatData(float bodyFatValue, long startTime, long endTime) {
        bodyFatViewModel.insertBodyFat(bodyFatValue, startTime, endTime);
        Toast.makeText(getActivity(), "Inserted body fat data successfully", Toast.LENGTH_SHORT).show();
    }



    private void insertWeightData(float weightValue, long startTime, long endTime) {
        weightViewModel.insertWeightData(weightValue, startTime, endTime);
        Toast.makeText(getActivity(), "Inserted weight data successfully", Toast.LENGTH_SHORT).show();
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
        // Initialize ViewModel and UI components
        bodyFatViewModel = new ViewModelProvider(this).get(BodyFatViewModel.class);
        bodyFatViewModel.initialize(getActivity());

        weightViewModel = new ViewModelProvider(this).get(WeightViewModel.class);
        weightViewModel.initialize(getActivity());

        hydrationViewModel = new ViewModelProvider(this).get(HydrationViewModel.class);
        hydrationViewModel.initialize(getActivity());

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
                // Get the startTime and endTime correctly
                long[] timeBounds = getDateInMillis(selectedYear, selectedMonth, selectedDay);
                long startTime = timeBounds[0];
                long endTime = timeBounds[1];

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
                    updateDataCalories(selectedType, startTime, endTime);
                } else if ("Distance".equals(selectedType)) {
                    updateDataDistance(selectedType, startTime, endTime);
                }
            }
        });
    }


    private void updateDataSteps(String type, long startTime, long endTime) {
        StepViewModel stepViewModel = new ViewModelProvider(this).get(StepViewModel.class);

        if ("Steps".equals(type)) {
            stepViewModel.readStepsForRange(startTime, endTime, getActivity());
            stepViewModel.getSteps().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                @Override
                public void onChanged(Integer totalSteps) {
                    historyTextView.setText(String.format("Total steps: %d", totalSteps));
                }
            });
        }
    }


    private void updateDataCalories(String type, long startTime, long endTime) {
        CaloriesViewModel caloriesViewModel = new ViewModelProvider(this).get(CaloriesViewModel.class);

        // Observe the LiveData first
        caloriesViewModel.getTotalCalories().observe(getViewLifecycleOwner(), new Observer<Calories>() {
            @Override
            public void onChanged(Calories calories) {
                historyTextView.setText(String.format("Total Calories Burnt: %.2f kcal", calories.getTotalCalories()));
            }
        });

        // Now trigger the data fetching
        if ("Kcal".equals(type)) {
            caloriesViewModel.fetchCaloriesData(getActivity(), GoogleSignIn.getLastSignedInAccount(getActivity()), new Date(startTime));
        }
    }



    private void updateDataDistance(String type, long startTime, long endTime) {
        DistanceViewModel distanceViewModel = new ViewModelProvider(this).get(DistanceViewModel.class);

        if ("Distance".equals(type)) {
            distanceViewModel.readDistanceForRange(startTime, endTime, getActivity());
            distanceViewModel.getDistance().observe(getViewLifecycleOwner(), new Observer<Float>() {
                @Override
                public void onChanged(Float totalDistance) {
                    historyTextView.setText(String.format("Total Distance Covered: %.2f meters", totalDistance));
                }
            });
        }
    }

    private void updateDataWeight(String type, long startTime, long endTime) {
        if ("Weight".equals(type)) {
            weightViewModel.fetchLatestWeight(startTime, endTime);
            weightViewModel.getLatestWeight().observe(getViewLifecycleOwner(), new Observer<Float>() {
                @Override
                public void onChanged(Float latestWeight) {
                    if (latestWeight != null) {
                        Log.d("HistoryFragment", "Latest weight fetched: " + latestWeight);
                        historyTextView.setText(String.format("Latest weight: %.2f kg", latestWeight));
                    }
                }
            });
        }
    }

    private void updateDataBodyFat(String type, long startTime, long endTime) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());
        if (account == null) {
            historyTextView.setText("Not signed in");
            return;
        }

        if ("BodyFat".equals(type)) {
            TimeZone tz = TimeZone.getDefault();
            int offsetFromUtc = tz.getOffset(startTime);
            startTime -= offsetFromUtc;
            endTime -= offsetFromUtc;

            bodyFatViewModel.fetchBodyFatData(new Date(startTime));
            bodyFatViewModel.getBodyFatData().observe(getViewLifecycleOwner(), new Observer<Float>() {
                @Override
                public void onChanged(Float latestBodyFat) {
                    if (latestBodyFat == 0) {
                        Log.d("updateDataBodyFat", "Latest body fat is 0. This could mean no data or an issue.");
                    }
                    historyTextView.setText(String.format("Latest body fat: %.2f%%", latestBodyFat));
                }
            });
        }
    }



    private void updateDataHydration(String type, long startTime, long endTime) {
        if ("Hydration".equals(type)) {
            TimeZone tz = TimeZone.getDefault();
            int offsetFromUtc = tz.getOffset(startTime);
            startTime -= offsetFromUtc;
            endTime -= offsetFromUtc;

            hydrationViewModel.fetchHydrationData(startTime, endTime);
            hydrationViewModel.getTotalWaterIntake().observe(getViewLifecycleOwner(), new Observer<Float>() {
                @Override
                public void onChanged(Float totalHydration) {
                    historyTextView.setText(String.format("Total water intake: %.2f ml", totalHydration));
                }
            });
        }
    }


    private long[] getDateInMillis(int year, int month, int day) {
        // Set the calendar to the start of the selected day in local time
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long startTime = calendar.getTimeInMillis();

        // Convert startTime to UTC
        TimeZone tz = TimeZone.getDefault();
        int offsetFromUtc = tz.getOffset(startTime);
        startTime -= offsetFromUtc;

        // End time is one day (in milliseconds) after start time
        long endTime = startTime + 24 * 60 * 60 * 1000 - 1;

        // Return the startTime and endTime as an array
        return new long[] {startTime, endTime};
    }

}


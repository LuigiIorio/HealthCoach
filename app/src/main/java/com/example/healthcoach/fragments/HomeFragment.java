package com.example.healthcoach.fragments;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.anychart.AnyChartView;
import com.example.healthcoach.R;
import com.example.healthcoach.models.UserProfile;
import com.example.healthcoach.recordingapi.Calories;
import com.example.healthcoach.viewmodels.CaloriesViewModel;
import com.example.healthcoach.viewmodels.HomeActivityViewModel;
import com.example.healthcoach.viewmodels.StepViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;

import java.util.Locale;



public class HomeFragment extends Fragment {

    private ProgressBar stepsProgressBar, kcalProgressBar, waterProgressBar;
    private TextView totalSteps;
    private TextView hydrationTextView;
    private AnyChartView lineChart;
    private TextView tvCalories;
    private HomeActivityViewModel homeActivityViewModel;
    private StepViewModel stepViewModel;
    private static final int REQUEST_CODE_GOOGLE_FIT_PERMISSIONS = 1;
    private static final int REQUEST_CODE_ACTIVITY_RECOGNITION = 2001;


    /**
     * Inflates the fragment's layout and initializes the UI elements.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate the fragment's layout.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState Bundle object containing the activity's previously saved state.
     * @return                   Return the View for the fragment's UI.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_home, container, false);
        inizialiseUI(view);

        return view;

    }


    /**
     * Called when the fragment is attached to its parent activity.
     * Initializes the ViewModel instances that will be shared with the parent activity.
     *
     * @param context  The context of the parent activity.
     */
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        homeActivityViewModel = new ViewModelProvider(requireActivity()).get(HomeActivityViewModel.class);
        stepViewModel = new ViewModelProvider(requireActivity()).get(StepViewModel.class);
    }

    /**
     * Requests permissions for Google Fit APIs that are required for the app.
     * Sets the read and write access types for different data types like steps, hydration, etc.
     */
    public void requestGoogleFitPermission() {
        GoogleSignInOptionsExtension fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_HYDRATION, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.TYPE_HYDRATION, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_HEIGHT, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_BODY_FAT_PERCENTAGE, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_WRITE)
                .build();

        GoogleSignIn.requestPermissions(
                this,
                REQUEST_CODE_GOOGLE_FIT_PERMISSIONS,
                GoogleSignIn.getLastSignedInAccount(this.getContext()),
                fitnessOptions);

        if (ContextCompat.checkSelfPermission(this.getContext(), "android.permission.ACTIVITY_RECOGNITION")
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{"android.permission.ACTIVITY_RECOGNITION"},
                    REQUEST_CODE_ACTIVITY_RECOGNITION);
        }
    }


    /**
     * Initializes the UI elements in the fragment.
     * Sets up LiveData observers to update UI elements based on data changes.
     *
     * @param view  The root view of the fragment.
     */

    private void inizialiseUI(View view) {

        View includedLayout = view.findViewById(R.id.include);

        stepsProgressBar = includedLayout.findViewById(R.id.circularProgressIndicator);
        totalSteps = includedLayout.findViewById(R.id.numberSteps);


        tvCalories = view.findViewById(R.id.tv_calories);
        kcalProgressBar = view.findViewById(R.id.kcalCircle);
        hydrationTextView = view.findViewById(R.id.hydrationTextView);
        waterProgressBar = view.findViewById(R.id.waterCircle);


        lineChart = view.findViewById(R.id.historyChart);

        homeActivityViewModel.getUser().observe(getViewLifecycleOwner(), userProfile -> {

            if (homeActivityViewModel.getUser().getValue() != null) {

                UserProfile profile = homeActivityViewModel.getUser().getValue();

                homeActivityViewModel.updateFitValues(this.getContext());

                requestGoogleFitPermission();

                homeActivityViewModel.getSteps().observe(getViewLifecycleOwner(), steps -> {

                    int percentage = (int) (((steps * 1.0)/profile.getDailySteps()) * 100);
                    Log.e("Steps", steps+"/"+profile.getDailySteps()+" "+percentage);
                    stepsProgressBar.setProgress(percentage);

                });

                homeActivityViewModel.getWater().observe(getViewLifecycleOwner(), water -> {

                    int percentage = (int) (((water * 1.0)/profile.getDailyWater()) * 100);
                    Log.e("Water", water+"/"+profile.getDailyWater()+" "+percentage);
                    waterProgressBar.setProgress(percentage);
                    hydrationTextView.setText(water +" ml");

                });

                homeActivityViewModel.getKcal().observe(getViewLifecycleOwner(), kcal -> {

                    int percentage = (int) ((((kcal - 1000) * 1.0)/profile.getDailyKcal()) * 100);
                    Log.e("Kcal", kcal+"/"+profile.getDailyKcal()+" "+percentage);
                    kcalProgressBar.setProgress(percentage);
                    tvCalories.setText((int)(kcal - 1000) + " kcal");

                });

                homeActivityViewModel.fetchLastSevenDaysData(this.getContext(), lineChart);

            }

        });


    }


    /**
     * Called when the fragment becomes visible to the user.
     * Checks if the user is signed into Google Fit and starts recording steps if so.
     */

    @Override
    public void onResume() {
        super.onResume();
        Log.d("HomeFragment", "onResume called");

        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(getContext());

        if (googleSignInAccount != null) {
            Log.d("HomeFragment", "User is signed in");
            homeActivityViewModel.fetchTodaySteps(getContext());
            stepViewModel.startRecordingSteps(getContext());

            homeActivityViewModel.getStepCount().observe(getViewLifecycleOwner(), steps -> {
                totalSteps.setText("Steps: " + steps);
            });
        } else {
            Log.e("HomeFragment", "User is not signed in");
        }
    }

    /**
     * Called when the fragment is no longer visible to the user.
     * Stops the recording of steps.
     */

    @Override
    public void onPause() {
        super.onPause();
        stepViewModel.stopRecordingSteps(getContext());
    }

}

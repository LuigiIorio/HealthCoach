package com.example.healthcoach.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.healthcoach.R;
import com.example.healthcoach.recordingapi.StepCountDelta;
import com.example.healthcoach.viewmodels.CaloriesExpendedViewModel;
import com.example.healthcoach.viewmodels.DistanceDeltaViewModel;
import com.example.healthcoach.viewmodels.StepViewModel;


public class FragmentScreen1 extends Fragment {

    // UI Component
    private TextView messageTextView;
    private TextView stepTextView;
    private TextView distanceTextView;
    private TextView caloriesTextView; // New TextView for calories
    private StepViewModel stepViewModel;
    private StepCountDelta stepCountDelta;
    private DistanceDeltaViewModel distanceDeltaViewModel;
    private CaloriesExpendedViewModel caloriesExpendedViewModel; // New ViewModel for calories
    private Handler handler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen1, container, false);

        // Initialize TextViews
        stepTextView = view.findViewById(R.id.stepTextView);
        distanceTextView = view.findViewById(R.id.distanceTextView);
        caloriesTextView = view.findViewById(R.id.caloriesTextView); // Initialize calories TextView

        // Initialize ViewModel for steps, distance, and calories
        stepViewModel = new ViewModelProvider(this).get(StepViewModel.class);
        distanceDeltaViewModel = new ViewModelProvider(this).get(DistanceDeltaViewModel.class);
        caloriesExpendedViewModel = new ViewModelProvider(this).get(CaloriesExpendedViewModel.class); // Initialize calories ViewModel

        // Observe LiveData from ViewModel
        stepViewModel.getSteps().observe(getViewLifecycleOwner(), steps -> stepTextView.setText("Steps: " + steps));
        distanceDeltaViewModel.getTotalDistance().observe(getViewLifecycleOwner(), distance -> distanceTextView.setText("Distance: " + distance + " meters"));
        caloriesExpendedViewModel.getTotalCalories().observe(getViewLifecycleOwner(), calories -> caloriesTextView.setText("Calories: " + calories + " kcal")); // Observe calories

        initUI(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        stepCountDelta = new StepCountDelta(getContext());  // Instantiate once here
        handler = new Handler();
        final int delay = 10000;  // 10 seconds in milliseconds

        handler.postDelayed(new Runnable() {
            public void run() {
                // Query today's distance from Google Fit
                distanceDeltaViewModel.queryTodayDistance(getContext());

                // Query today's calories from Google Fit
                caloriesExpendedViewModel.queryTodayCalories(getContext());

                // Query today's steps from Google Fit
                stepCountDelta.readTodaySteps(getContext(), getActivity());  // Use the instantiated object

                handler.postDelayed(this, delay);
            }
        }, delay);
    }



    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }

    private void initUI(View view) {
        messageTextView = view.findViewById(R.id.messageTextView);
    }
}

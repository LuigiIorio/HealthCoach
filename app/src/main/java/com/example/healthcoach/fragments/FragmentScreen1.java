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
import com.example.healthcoach.viewmodels.DistanceDeltaViewModel;
import com.example.healthcoach.viewmodels.StepViewModel;



public class FragmentScreen1 extends Fragment {

    // UI Component
    private TextView messageTextView;
    private TextView stepTextView;
    private TextView distanceTextView; // New TextView for distance
    private StepViewModel stepViewModel;
    private DistanceDeltaViewModel distanceDeltaViewModel; // New ViewModel for distance
    private Handler handler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen1, container, false);

        // Initialize TextViews
        stepTextView = view.findViewById(R.id.stepTextView);
        distanceTextView = view.findViewById(R.id.distanceTextView);  // Initialize distance TextView

        // Initialize ViewModel for steps and distance
        stepViewModel = new ViewModelProvider(this).get(StepViewModel.class);
        distanceDeltaViewModel = new ViewModelProvider(this).get(DistanceDeltaViewModel.class);  // Initialize distance ViewModel

        // Observe LiveData from ViewModel for steps
        stepViewModel.getSteps().observe(getViewLifecycleOwner(), steps -> stepTextView.setText("Steps: " + steps));

        // Observe LiveData from ViewModel for distance
        distanceDeltaViewModel.getTotalDistance().observe(getViewLifecycleOwner(), distance -> distanceTextView.setText("Distance: " + distance + " meters"));

        initUI(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Initialize Handler
        handler = new Handler();

        final int delay = 30000; // 30 seconds in milliseconds

        handler.postDelayed(new Runnable() {
            public void run() {
                // Update steps from Google Fit (existing logic)

                // Query distance from Google Fit (new logic)
                distanceDeltaViewModel.queryTodayDistance(getContext());

                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    @Override
    public void onPause() {
        super.onPause();

        // Remove callbacks to stop updates
        handler.removeCallbacksAndMessages(null);
    }

    private void initUI(View view) {
        messageTextView = view.findViewById(R.id.messageTextView);
        // If you have additional logic or UI components to initialize, you can do it here.
    }
}

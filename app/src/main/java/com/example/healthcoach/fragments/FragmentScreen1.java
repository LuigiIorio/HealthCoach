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
import com.example.healthcoach.viewmodels.MainActivityViewModel;
import com.example.healthcoach.viewmodels.StepViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;


public class FragmentScreen1 extends Fragment {

    // UI Component
    private TextView messageTextView;
    private TextView stepTextView;
    private TextView distanceTextView;
    private TextView caloriesTextView;
    private StepViewModel stepViewModel;
    private StepCountDelta stepCountDelta;
    private DistanceDeltaViewModel distanceDeltaViewModel;
    private CaloriesExpendedViewModel caloriesExpendedViewModel;
    private Handler handler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        MainActivityViewModel viewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        GoogleSignInAccount account = viewModel.getGoogleSignInAccount();

        if (account == null) {
            viewModel.checkSignInStatus(getContext());
            return view;  // Return the view early if account is null, to avoid NullPointerException
        }

        //stepTextView = view.findViewById(R.id.stepTextView);
        //distanceTextView = view.findViewById(R.id.distanceTextView);
        //caloriesTextView = view.findViewById(R.id.caloriesTextView);

        stepViewModel = new ViewModelProvider(this).get(StepViewModel.class);
        distanceDeltaViewModel = new ViewModelProvider(this).get(DistanceDeltaViewModel.class);
        caloriesExpendedViewModel = new ViewModelProvider(this).get(CaloriesExpendedViewModel.class);

        stepViewModel.getSteps().observe(getViewLifecycleOwner(), steps -> {
            stepTextView.setText("Steps: " + steps);
        });

        distanceDeltaViewModel.getTotalDistance().observe(getViewLifecycleOwner(), distance -> distanceTextView.setText("Distance: " + distance + " meters"));
        caloriesExpendedViewModel.getTotalCalories().observe(getViewLifecycleOwner(), calories -> caloriesTextView.setText("Calories: " + calories + " kcal"));

        initUI(view);

        handler = new Handler();
        final int delay = 10000;

        handler.postDelayed(new Runnable() {
            public void run() {
                distanceDeltaViewModel.queryTodayDistance(getContext());
                caloriesExpendedViewModel.queryTodayCalories(getContext());
                stepCountDelta.readTodaySteps(getContext(), getActivity());

                handler.postDelayed(this, delay);
            }
        }, delay);

        return view;
    }
    @Override
    public void onPause() {
        super.onPause();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    private void initUI(View view) {
        // messageTextView = view.findViewById(R.id.messageTextView);
    }
}

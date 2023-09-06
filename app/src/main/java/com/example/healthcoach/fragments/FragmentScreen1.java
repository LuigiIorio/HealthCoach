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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.healthcoach.R;
import com.example.healthcoach.viewmodels.StepViewModel;

public class FragmentScreen1 extends Fragment {

    // UI Component
    private TextView messageTextView;
    private TextView stepTextView;
    private StepViewModel stepViewModel;
    private Handler handler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen1, container, false);

        // Initialize TextView
        stepTextView = view.findViewById(R.id.stepTextView);

        // Initialize ViewModel
        stepViewModel = new ViewModelProvider(this).get(StepViewModel.class);

        // Observe LiveData from ViewModel
        stepViewModel.getSteps().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer steps) {
                stepTextView.setText("Steps: " + steps);
            }
        });

        initUI(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Initialize Handler
        handler = new Handler();

        final int delay = 10000; // 10 seconds in milliseconds

        handler.postDelayed(new Runnable() {
            public void run() {
                // Insert code to update steps from Google Fit here
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

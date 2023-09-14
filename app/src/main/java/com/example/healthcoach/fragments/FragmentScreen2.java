

package com.example.healthcoach.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.healthcoach.R;
import com.example.healthcoach.recordingapi.Hydration;
import com.example.healthcoach.viewmodels.BodyFatViewModel;
import com.example.healthcoach.viewmodels.Screen2ViewModel;
import com.example.healthcoach.viewmodels.WaterIntakeViewModel;
import com.example.healthcoach.viewmodels.WeightViewModel;


public class FragmentScreen2 extends Fragment {

    private TextView journalTextView;
    private Screen2ViewModel viewModel;
    private EditText waterIntakeEditText;
    private Button addWaterIntakeButton;
    private WeightViewModel weightViewModel;
    private EditText weightEditText;
    private WaterIntakeViewModel waterIntakeViewModel;

    private EditText bodyFatEditText;

    private Button submitBodyFatButton;

    private BodyFatViewModel bodyFatViewModel;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(Screen2ViewModel.class);
        weightViewModel = new ViewModelProvider(this).get(WeightViewModel.class);
        waterIntakeViewModel = new ViewModelProvider(this).get(WaterIntakeViewModel.class);
        bodyFatViewModel = new ViewModelProvider(this).get(BodyFatViewModel.class);  // New line
        waterIntakeViewModel.setRepository(new Hydration(getContext()));
    }


    /*
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        journalTextView = view.findViewById(R.id.journalTextView);
        waterIntakeEditText = view.findViewById(R.id.waterIntakeEditText);
        addWaterIntakeButton = view.findViewById(R.id.addWaterIntakeButton);
        weightEditText = view.findViewById(R.id.weightEditText2);
        bodyFatEditText = view.findViewById(R.id.bodyFatEditText);
        submitBodyFatButton = view.findViewById(R.id.submitBodyFatButton);

        observeData();
        initWaterIntakeUI();
        setupWeightSubmitButton(view);
        setupBodyFatButton();


        return view;
    } */

    private void setupBodyFatButton() {
        submitBodyFatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bodyFatText = bodyFatEditText.getText().toString();
                if (!bodyFatText.isEmpty()) {
                    float bodyFat = Float.parseFloat(bodyFatText);
                    bodyFatViewModel.insertBodyFat(getContext(), bodyFat);
                    Toast.makeText(getContext(), "Body fat data added successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Please enter body fat percentage", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void observeData() {
        viewModel.getJournalText().observe(getViewLifecycleOwner(), journalTextView::setText);
        weightViewModel.getWeightError().observe(getViewLifecycleOwner(), error -> Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show());
        weightViewModel.getWeightSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(getContext(), "Weight submitted successfully!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initWaterIntakeUI() {
        addWaterIntakeButton.setOnClickListener(v -> {
            String waterIntakeString = waterIntakeEditText.getText().toString();
            if (waterIntakeString.isEmpty()) return;

            float waterIntake;
            try {
                waterIntake = Float.parseFloat(waterIntakeString);
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Please enter a valid number", Toast.LENGTH_SHORT).show();
                return;
            }
            waterIntakeViewModel.addWater(waterIntake);
            Toast.makeText(requireContext(), "Water added successfully!", Toast.LENGTH_SHORT).show();
        });
    }


/*
    private void setupWeightSubmitButton(View view) {
        Button submitWeightButton = view.findViewById(R.id.submitWeightButton2);
        submitWeightButton.setOnClickListener(v -> {
            String weightString = weightEditText.getText().toString().trim();
            weightViewModel.validateAndSubmitWeight(getContext(), weightString);
        });
    }
    */

}



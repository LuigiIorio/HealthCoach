package com.example.healthcoach.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.healthcoach.R;
import com.example.healthcoach.viewmodels.HeightViewModel;
import com.example.healthcoach.viewmodels.UserProfileViewModel;
import com.example.healthcoach.viewmodels.WeightViewModel;

public class FragmentScreen4 extends Fragment {

    private WeightViewModel weightViewModel;
    private EditText weightEditText;
    private HeightViewModel heightViewModel;
    private EditText heightEditText;
    private UserProfileViewModel userProfileViewModel;
    private Spinner genderSpinner;
    private NumberPicker ageNumberPicker;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen4, container, false);

        weightViewModel = new ViewModelProvider(this).get(WeightViewModel.class);
        weightEditText = view.findViewById(R.id.weightEditText);
        setupWeightSubmitButton(view);
        weightViewModel.getWeightError().observe(getViewLifecycleOwner(), error -> Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show());
        weightViewModel.getWeightSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(getContext(), "Weight submitted successfully!", Toast.LENGTH_SHORT).show();
            }
        });

        heightViewModel = new ViewModelProvider(this).get(HeightViewModel.class);
        heightEditText = view.findViewById(R.id.heightEditText);
        setupHeightSubmitButton(view);
        heightViewModel.getHeightError().observe(getViewLifecycleOwner(), error -> Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show());
        heightViewModel.getHeightSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(getContext(), "Height submitted successfully!", Toast.LENGTH_SHORT).show();
            }
        });

        userProfileViewModel = new ViewModelProvider(this).get(UserProfileViewModel.class);
        genderSpinner = view.findViewById(R.id.genderSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.gender_values, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                userProfileViewModel.setGender(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ageNumberPicker = view.findViewById(R.id.ageNumberPicker);
        ageNumberPicker.setMinValue(0);
        ageNumberPicker.setMaxValue(100);
        ageNumberPicker.setOnValueChangedListener((picker, oldVal, newVal) -> userProfileViewModel.setAge(newVal));

        userProfileViewModel.getGender().observe(getViewLifecycleOwner(), gender -> {});
        userProfileViewModel.getAge().observe(getViewLifecycleOwner(), age -> ageNumberPicker.setValue(age));

        return view;
    }

    private void setupWeightSubmitButton(View view) {
        Button submitWeightButton = view.findViewById(R.id.submitWeightButton);
        submitWeightButton.setOnClickListener(v -> {
            String weightString = weightEditText.getText().toString().trim();
            weightViewModel.validateAndSubmitWeight(getContext(), weightString);
        });
    }

    private void setupHeightSubmitButton(View view) {
        Button submitHeightButton = view.findViewById(R.id.submitHeightButton);
        submitHeightButton.setOnClickListener(v -> {
            String heightString = heightEditText.getText().toString().trim();
            heightViewModel.validateAndSubmitHeight(getActivity(), heightString);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WeightViewModel.REQUEST_OAUTH_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String weightString = weightEditText.getText().toString().trim();
                if (!weightString.isEmpty()) {
                    try {
                        float weight = Float.parseFloat(weightString);
                        weightViewModel.insertWeightData(getContext(), weight);
                    } catch (NumberFormatException ignored) {}
                }
            } else {
                Toast.makeText(getContext(), "Permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == HeightViewModel.REQUEST_OAUTH_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String heightString = heightEditText.getText().toString().trim();
                if (!heightString.isEmpty()) {
                    try {
                        float height = Float.parseFloat(heightString);
                        heightViewModel.insertHeightData(getContext(), height);
                    } catch (NumberFormatException ignored) {}
                }
            } else {
                Toast.makeText(getContext(), "Permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

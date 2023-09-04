package com.example.healthcoach.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.healthcoach.R;
import com.example.healthcoach.models.UserProfile;
import com.example.healthcoach.viewmodels.HomeViewModel;

import android.app.DatePickerDialog;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.Spinner;
import java.util.Calendar;
import com.example.healthcoach.viewmodels.UserProfileViewModel;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.healthcoach.R;
import com.example.healthcoach.viewmodels.HomeViewModel;

public class FragmentScreen4 extends Fragment {

    private HomeViewModel userProfileViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen4, container, false);

        // Initialize ViewModel
        userProfileViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Set up Gender Spinner
        Spinner genderSpinner = view.findViewById(R.id.genderSpinner);
        String[] genderArray = {"Male", "Female"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, genderArray);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        // Observe and set Gender
        userProfileViewModel.getGender().observe(getViewLifecycleOwner(), gender -> {
            int position = genderAdapter.getPosition(gender);
            if (position != -1) {
                genderSpinner.setSelection(position);
            }
        });

        // Gender Spinner Selection Listener
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                userProfileViewModel.setGender(genderArray[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do Nothing
            }
        });

        // Set up Age NumberPicker
        NumberPicker ageNumberPicker = view.findViewById(R.id.ageNumberPicker);
        ageNumberPicker.setMinValue(18);
        ageNumberPicker.setMaxValue(100);

        // Observe and set Age
        userProfileViewModel.getAge().observe(getViewLifecycleOwner(), ageNumberPicker::setValue);

        ageNumberPicker.setOnValueChangedListener((picker, oldVal, newVal) -> userProfileViewModel.setAge(newVal));

        return view;
    }
}

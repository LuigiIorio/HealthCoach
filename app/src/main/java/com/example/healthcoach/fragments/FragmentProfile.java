package com.example.healthcoach.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.healthcoach.R;

public class FragmentProfile extends Fragment {

    private ImageView profilePic;
    private TextView nickname, weight, height;
    private ProgressBar progressBar;
    private Spinner workoutSpinner, timeSpinner;
    private EditText waterIntake;
    private Button submitButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profilePic = view.findViewById(R.id.cardView2).findViewById(R.id.profilePic);

        nickname = view.findViewById(R.id.nickname);
        weight = view.findViewById(R.id.weightValue);
        height = view.findViewById(R.id.heightValue);

        progressBar = view.findViewById(R.id.stepsProgressBar);

        workoutSpinner = view.findViewById(R.id.activitySpinner);
        timeSpinner = view.findViewById(R.id.timeSpinner);

        waterIntake = view.findViewById(R.id.waterIntake);

        submitButton = view.findViewById(R.id.submitButton);

        inizialiseUI();

        return view;

    }

    private void inizialiseUI() {



    }

}

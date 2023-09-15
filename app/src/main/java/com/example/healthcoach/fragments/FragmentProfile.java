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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.healthcoach.R;
import com.example.healthcoach.models.UserProfile;
import com.example.healthcoach.viewmodels.HomeActivityViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;

import java.util.Calendar;
import java.util.Date;

public class FragmentProfile extends Fragment {

    private static final int KCAL_EASY = 100;
    private static final int KCAL_MEDIUM = 250;
    private static final int KCAL_HARD = 400;
    private static final int KCAL_COMPETITIVE = 500;
    private ImageView profilePic;
    private TextView nickname, weight, height;
    private ProgressBar progressBar;
    private Spinner workoutSpinner, timeSpinner;
    private EditText waterIntake;
    private Button submitButton;
    private HomeActivityViewModel viewModel;
    private int steps, water, kcal;
    private int dailySteps, dailyWater, dailyKcal;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(HomeActivityViewModel.class);
        viewModel.updateFitValues(this.getContext());

        inizialiseUI(view);
        setupListener();

        return view;

    }

    private void inizialiseUI(View view) {

        profilePic = view.findViewById(R.id.cardView2).findViewById(R.id.profilePic);
        nickname = view.findViewById(R.id.nickname);
        weight = view.findViewById(R.id.weightValue);
        height = view.findViewById(R.id.heightValue);
        progressBar = view.findViewById(R.id.stepsProgressBar);
        workoutSpinner = view.findViewById(R.id.activitySpinner);
        timeSpinner = view.findViewById(R.id.timeSpinner);
        waterIntake = view.findViewById(R.id.waterIntake);
        submitButton = view.findViewById(R.id.submitButton);

        viewModel.getProfileImage().observe(this, uri -> {
            if (uri != null) {
                // Carica l'immagine nella ImageView quando viene ricevuto un Uri
                Glide.with(this)
                        .load(uri)
                        .into(profilePic);
            }
        });

        viewModel.getUser().observe(this, userProfile -> {
            if (userProfile != null) {

                weight.setText(userProfile.getWeight() + "Kg");
                height.setText(userProfile.getHeight() + "cm");
                nickname.setText(userProfile.getFullName() + " (" + getYears(userProfile) + ")");
                dailySteps = userProfile.getDailySteps();
                dailyWater = userProfile.getDailyWater();
                dailyKcal = userProfile.getDailyKcal();

            }
        });

        viewModel.getSteps().observe(this, steps -> {
            this.steps = steps;
            updateDailyGoal();
        });

        viewModel.getSteps().observe(this, water -> {
            this.water = water;
            updateDailyGoal();
        });

        viewModel.getSteps().observe(this, kcal -> {
            this.kcal = kcal;
            updateDailyGoal();
        });

    }

    private void setupListener() {

        submitButton.setOnClickListener(view -> {

            try {
                int water = Integer.parseInt(waterIntake.getText().toString());
                viewModel.uploadWaterIntake(this.getContext(), water);
            } catch (NumberFormatException e) {}

            String time = timeSpinner.getSelectedItem().toString();

            if(!time.equalsIgnoreCase("0 min")) {

                String training = workoutSpinner.getSelectedItem().toString();
                int multiplier = mapTimeToValue(time);

                viewModel.uploadKcalUsed(this.getContext(), multiplier * mapTrainingToValue(training));

            }

        });

    }

    private int getYears(UserProfile user) {

        Calendar today = Calendar.getInstance();
        today.setTime(Calendar.getInstance().getTime());

        int year = today.get(Calendar.YEAR);
        int month = today.get(Calendar.MONTH) + 1;
        int day = today.get(Calendar.DAY_OF_MONTH);

        year = year - user.getYear();

        if(month - user.getMonth() > 0)
            return year;

        if(month == user.getMonth() && day >= user.getDay())
            return year;

        return year - 1;


    }

    private void updateDailyGoal() {

        double percentage, percentageSteps, percentageWater, percentageKcal;

        if(dailySteps == 0)
            percentageSteps = 0;
        else
            percentageSteps = (1.0 * steps)/dailySteps;

        if(dailyWater == 0)
            percentageWater = 0;
        else
            percentageWater = (1.0 * water)/dailyWater;

        if(dailyKcal == 0)
            percentageKcal = 0;
        else
            percentageKcal = (1.0 * kcal)/dailyKcal;

        percentage = (percentageSteps + percentageWater + percentageKcal)/3;

        progressBar.setProgress((int) percentage);

    }

    private static int mapTimeToValue(String time) {
        switch (time) {
            case "0 min":
                return 0;
            case "15 min":
                return 1;
            case "30 min":
                return 2;
            case "45 min":
                return 3;
            case "1 h":
                return 4;
            case "1.25 h":
                return 5;
            case "1.5 h":
                return 6;
            case "1.75 h":
                return 7;
            case "2 h":
                return 8;
            case "2.5 h":
                return 10;
            case "3 h":
                return 12;
            case "3.5 h":
                return 14;
            case "4 h":
                return 16;
        }

        return 0;
    }

    private static int mapTrainingToValue(String training) {

        switch (training) {
            case "Easy":
                return KCAL_EASY;
            case "Medium":
                return KCAL_MEDIUM;
            case "Hard":
                return KCAL_HARD;
            case "Competitive":
                return KCAL_COMPETITIVE;
            default:
                return 0;

        }

    }

}

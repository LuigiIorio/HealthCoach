package com.example.healthcoach.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
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
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.healthcoach.R;
import com.example.healthcoach.models.UserProfile;
import com.example.healthcoach.viewmodels.HomeActivityViewModel;

import java.util.Calendar;

public class ProfileFragment extends Fragment {

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


    /**
     * Inflates the fragment's layout and initializes the UI elements and listeners.
     * Calls the ViewModel to update fitness values from Google Fit.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate the fragment's layout.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState Bundle object containing the activity's previously saved state.
     * @return                   Return the View for the fragment's UI.
     */

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(HomeActivityViewModel.class);
        viewModel.updateFitValues(this.getContext());

        inizialiseUI(view);
        setupListener();

        return view;

    }


    /**
     * Initializes the UI elements in the profile fragment.
     * Sets up LiveData observers to update UI elements based on data changes.
     * This includes profile picture, weight, height, and other user metrics.
     *
     * @param view  The root view of the fragment.
     */

    private void inizialiseUI(View view) {
        profilePic = view.findViewById(R.id.profilePic);
        nickname = view.findViewById(R.id.nickname);
        weight = view.findViewById(R.id.weightValue);
        height = view.findViewById(R.id.heightValue);
        progressBar = view.findViewById(R.id.stepsProgressBar);
        workoutSpinner = view.findViewById(R.id.activitySpinner);
        timeSpinner = view.findViewById(R.id.timeSpinner);
        waterIntake = view.findViewById(R.id.waterIntake);
        submitButton = view.findViewById(R.id.submitButton);

        viewModel.getProfileImage().observe(getViewLifecycleOwner(), uri -> {
            if (uri != null) {
                Log.d("ProfileImage", "URI is not null: " + uri.toString());
                Glide.with(this)
                        .load(uri)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                Log.e("Glide", "Load failed", e);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                Log.d("Glide", "Resource ready");
                                return false;
                            }
                        })
                        .into(profilePic);
            } else {
                Log.d("ProfileImage", "URI is null");
            }
        });

        viewModel.getUser().observe(getViewLifecycleOwner(), userProfile -> {
            if (userProfile != null) {
                weight.setText(userProfile.getWeight() + "Kg");
                height.setText(userProfile.getHeight() + "cm");
                nickname.setText(userProfile.getFullName() + " (" + getYears(userProfile) + ")");
                dailySteps = userProfile.getDailySteps();
                dailyWater = userProfile.getDailyWater();
                dailyKcal = userProfile.getDailyKcal();
            }
        });


        viewModel.getSteps().observe(getViewLifecycleOwner(), steps -> {
            this.steps = steps;
            viewModel.getWater().observe(getViewLifecycleOwner(), water -> {
                this.water = water;
                viewModel.getKcal().observe(getViewLifecycleOwner(), kcal -> {
                    this.kcal = kcal;
                    Log.i("Bar", "Sono Qui");
                    updateDailyGoal();
                });
            });
        });




    }


    /**
     * Sets up the listener for the submit button.
     * When clicked, it uploads the user's water intake and workout data.
     */


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

            workoutSpinner.setSelection(0);
            timeSpinner.setSelection(0);
            waterIntake.setText("");

        });

    }

    /**
     * Calculates the age of the user based on the birth date provided in the UserProfile.
     *
     * @param user  The UserProfile object containing the user's birth date.
     * @return      The calculated age of the user.
     */


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

    /**
     * Updates the daily goal progress bar based on the user's daily metrics.
     * It calculates the progress for steps, water intake, and calories, then updates the progress bar.
     */

    private void updateDailyGoal() {

        double percentage, percentageSteps, percentageWater, percentageKcal;

        if(dailySteps == 0)
            percentageSteps = 0;
        else
            percentageSteps = ((1.0 * steps)/dailySteps) * 100;

        if(dailyWater == 0)
            percentageWater = 0;
        else
            percentageWater = ((1.0 * water)/dailyWater) * 100;

        if(dailyKcal == 0)
            percentageKcal = 0;
        else
            percentageKcal = ((1.0 * kcal)/dailyKcal) * 100;

        percentage = (percentageSteps + percentageWater + percentageKcal)/3;

        progressBar.setProgress((int) percentage);

        Log.e("Steps", steps+"/"+dailySteps+" "+percentageSteps);
        Log.e("Water", water+"/"+dailyWater+" "+percentageWater);
        Log.e("Kcal", kcal+"/"+dailyKcal+" "+percentageKcal);
        Log.e("Total Percentage", percentage+ "");

    }

    /**
     * Maps the time duration string to its corresponding value.
     *
     * @param time  The time duration in string format.
     * @return      The value mapped to the time string.
     */

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

    /**
     * Maps the training difficulty level to its corresponding kcal value.
     *
     * @param training  The training difficulty level in string format.
     * @return          The kcal value mapped to the training level.
     */


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

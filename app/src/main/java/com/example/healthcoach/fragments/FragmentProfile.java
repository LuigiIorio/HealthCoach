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
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.healthcoach.R;
import com.example.healthcoach.models.UserProfile;
import com.example.healthcoach.viewmodels.HomeActivityViewModel;

import java.util.Calendar;
import java.util.Date;

public class FragmentProfile extends Fragment {

    private ImageView profilePic;
    private TextView nickname, weight, height;
    private ProgressBar progressBar;
    private Spinner workoutSpinner, timeSpinner;
    private EditText waterIntake;
    private Button submitButton;
    private HomeActivityViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(HomeActivityViewModel.class);

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

            }
        });

    }

    private void setupListener() {



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

}

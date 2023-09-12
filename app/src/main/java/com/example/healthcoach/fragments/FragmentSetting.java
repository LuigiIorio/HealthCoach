package com.example.healthcoach.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.healthcoach.R;

public class FragmentSetting extends Fragment {

    private ImageView profilePic;
    private TextView profilePicEdit;
    private EditText weightInput, heightInput, stepsInput, waterInput, kcalInput,
            newPasswordInput, confirmPasswordInput, oldPasswordInput;

    private Button submitButton, logoutButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        profilePic = view.findViewById(R.id.profilePic);

        profilePicEdit = view.findViewById(R.id.infoProfileTextView);

        weightInput = view.findViewById(R.id.weightInput);
        heightInput = view.findViewById(R.id.heightInput);
        stepsInput = view.findViewById(R.id.stepsInput);
        waterInput = view.findViewById(R.id.waterInput);
        kcalInput = view.findViewById(R.id.kcalInput);
        newPasswordInput = view.findViewById(R.id.newPasswordInput);
        confirmPasswordInput = view.findViewById(R.id.confirmPasswordInput);
        oldPasswordInput = view.findViewById(R.id.oldPasswordInput);

        submitButton = view.findViewById(R.id.submitButton);
        logoutButton = view.findViewById(R.id.logoutButton);

        inizialiseUI();

        return view;

    }

    private void inizialiseUI() {



    }

}

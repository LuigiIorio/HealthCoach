package com.example.healthcoach.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;


import com.bumptech.glide.Glide;
import com.example.healthcoach.R;
import com.example.healthcoach.models.UserProfile;
import com.example.healthcoach.viewmodels.HomeActivityViewModel;


public class FragmentSetting extends Fragment {

    private ImageView profilePic;
    private TextView profilePicEdit;
    private EditText weightInput, heightInput, stepsInput, waterInput, kcalInput,
            newPasswordInput, confirmPasswordInput, oldPasswordInput;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private Button submitButton, logoutButton;
    private HomeActivityViewModel viewModel;
    private boolean newImage = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(HomeActivityViewModel.class);

        inizialiseUI(view);
        setupListeners();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST) {
            if (data != null) {
                // Ottieni l'URI dell'immagine selezionata
                Uri selectedImageUri = data.getData();

                profilePic.setImageURI(selectedImageUri);
                profilePic.setImageTintList(null);
                imageUri = selectedImageUri;
                newImage = true;

            } else {

                TypedValue typedValue = new TypedValue();
                int colorOnPrimary = typedValue.data;

                profilePic.setImageResource(R.drawable.ic_profile);
                profilePic.setImageTintList(ColorStateList.valueOf(colorOnPrimary));
                imageUri = null;
                newImage = false;

            }
        }
    }

    private void inizialiseUI(View view) {

        profilePic = view.findViewById(R.id.profilePic);
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

        viewModel.getProfileImage().observe(getViewLifecycleOwner(), uri -> {
            if (uri != null) {
                Glide.with(this).load(uri).into(profilePic);
            } else {
                profilePic.setImageResource(R.drawable.ic_profile);
            }
        });

    }

    private void setupListeners() {

        profilePic.setOnClickListener(view -> {

            // Creazione di un intent per aprire la galleria delle immagini
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*"); // Filtraggio solo per file di immagine

            // Avvio dell'attività per selezionare un'immagine
            startActivityForResult(intent, PICK_IMAGE_REQUEST);

        });

        submitButton.setOnClickListener(view -> {

            UserProfile user = viewModel.getUser().getValue();

            if(user == null)
                return;

            if(checkUserInfo(user) | checkDailyGoal(user) | checkPassword(user)) {

                viewModel.updateUser(user);
                FragmentTransaction transaction = this.getActivity().getSupportFragmentManager().beginTransaction();
                FragmentSetting newFragment = new FragmentSetting();
                transaction.replace(R.id.fragment_container, newFragment);
                transaction.commit();

            }

        });

    }

    private boolean checkUserInfo(UserProfile user) {

        boolean edited = false;

        if(imageUri != null && newImage) {
            viewModel.updateProfilePic(imageUri);
        }

        try {

            user.setWeight(Integer.parseInt(weightInput.getText().toString()));
            edited = true;

        } catch (NumberFormatException e) {}

        try {

            user.setHeight(Integer.parseInt(heightInput.getText().toString()));
            edited = true;

        } catch (NumberFormatException e) {}

        return edited;

    }

    private boolean checkDailyGoal(UserProfile user) {

        boolean edited = false;

        try {

            user.setDailySteps(Integer.parseInt(stepsInput.getText().toString()));
            edited = true;

        } catch (NumberFormatException e) {}

        try {

            user.setDailyWater(Integer.parseInt(waterInput.getText().toString()));
            edited = true;

        } catch (NumberFormatException e) {}

        try {

            user.setDailyKcal(Integer.parseInt(kcalInput.getText().toString()));
            edited = true;

        } catch (NumberFormatException e) {}

        return edited;

    }

    private boolean checkPassword(UserProfile user) {

        if(user.getPassword().equals(oldPasswordInput.getText().toString())) {

            if(newPasswordInput.getText().toString().equals(confirmPasswordInput.getText().toString())) {

                user.setPassword(newPasswordInput.getText().toString());
                viewModel.updatePassword(user.getPassword());

                return true;

            }

            else {

                Toast.makeText(this.getContext(), "Password must be the same", Toast.LENGTH_SHORT).show();
                return false;

            }

        }

        else {

            Toast.makeText(this.getContext(), "Old Password does not match", Toast.LENGTH_SHORT).show();
            return false;

        }

    }

}



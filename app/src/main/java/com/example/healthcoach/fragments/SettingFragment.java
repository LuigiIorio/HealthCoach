package com.example.healthcoach.fragments;

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
import androidx.lifecycle.ViewModelProvider;


import com.bumptech.glide.Glide;
import com.example.healthcoach.R;
import com.example.healthcoach.models.UserProfile;
import com.example.healthcoach.viewmodels.HomeActivityViewModel;


public class SettingFragment extends Fragment {

    private ImageView profilePic;
    private TextView profilePicEdit;
    private EditText weightInput, heightInput, stepsInput, waterInput, kcalInput,
            newPasswordInput, confirmPasswordInput, oldPasswordInput;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private Button submitButton, logoutButton, googleButton;
    private HomeActivityViewModel viewModel;
    private boolean newImage = false;


    /**
     * Inflates the fragment's layout and initializes UI elements and listeners.
     *
     * @param inflater           The LayoutInflater object to inflate the fragment's layout.
     * @param container          If non-null, this is the parent view to attach the fragment's UI.
     * @param savedInstanceState Previously saved state of the fragment.
     * @return                   The View for the fragment's UI.
     */

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(HomeActivityViewModel.class);

        inizialiseUI(view);
        setupListeners();

        return view;
    }

    /**
     * Handles the result of selecting an image from the gallery.
     *
     * @param requestCode The request code originally supplied to startActivityForResult().
     * @param resultCode  The integer result code returned by the child activity.
     * @param data        An Intent carrying the result data.
     */

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

    /**
     * Initializes the UI elements in the settings fragment.
     * Sets up LiveData observers to update profile picture and other UI elements based on data changes.
     *
     * @param view The root view of the fragment.
     */


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
        googleButton = view.findViewById(R.id.googleLoginButton);

        viewModel.getProfileImage().observe(getViewLifecycleOwner(), uri -> {
            if (uri != null) {
                Glide.with(this).load(uri).into(profilePic);
            } else {
                profilePic.setImageResource(R.drawable.ic_profile);
            }
        });

        if(viewModel.checkGoogleMerge(this.getContext())) {

            googleButton.setVisibility(View.INVISIBLE);
            ((ViewGroup) googleButton.getParent()).removeView(googleButton);

        }

    }

    /**
     * Sets up listeners for UI elements like profile picture, submit button, Google login button, and logout button.
     */


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
                SettingFragment newFragment = new SettingFragment();
                transaction.replace(R.id.fragment_container, newFragment);
                transaction.commit();

            }

        });

        googleButton.setOnClickListener(view -> {

            viewModel.mergeGoogleAccount(this.getContext());

        });

        logoutButton.setOnClickListener(view -> {

            viewModel.logoutUser(this.getActivity());

        });

    }

    /**
     * Checks and updates the user's information like weight and height.
     *
     * @param user The UserProfile object containing the user's current details.
     * @return     True if any information is edited, false otherwise.
     */


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

    /**
     * Checks and updates the user's daily goals for steps, water intake, and calories.
     *
     * @param user The UserProfile object containing the user's current daily goals.
     * @return     True if any goal is edited, false otherwise.
     */

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

    /**
     * Validates and updates the user's password.
     *
     * @param user The UserProfile object containing the user's current password.
     * @return     True if the password is successfully updated, false otherwise.
     */


    private boolean checkPassword(UserProfile user) {


        // Check if user or its properties are null
        /*
        if (user == null || user.getPassword() == null) {
            Toast.makeText(this.getContext(), "User or password is null", Toast.LENGTH_SHORT).show();
            return false;
        } */

        // Get text from EditText fields
        String oldPassword = oldPasswordInput.getText().toString();
        String newPassword = newPasswordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();

        // Check if any of the input fields are null or empty
        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this.getContext(), "All fields must be filled in", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check if new password length is less than 8 characters
        if (newPassword.length() < 8) {
            Toast.makeText(this.getContext(), "New password must be at least 8 characters", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check old password
        if (user.getPassword().equals(oldPassword)) {
            // Check if new password and confirm password match
            if (newPassword.equals(confirmPassword)) {
                // Update password
                user.setPassword(newPassword);
                viewModel.updatePassword(user.getPassword());
                return true;
            } else {
                Toast.makeText(this.getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(this.getContext(), "Old Password does not match", Toast.LENGTH_SHORT).show();
            return false;
        }
    }


}



package com.example.healthcoach.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.healthcoach.R;
import com.example.healthcoach.models.UserProfile;
import com.example.healthcoach.viewmodels.SignUpViewModel;

public class SignUpInformation extends AppCompatActivity {

    private SignUpViewModel viewModel;
    private EditText fullNameText, weightInput, heightInput, stepsInput, waterInput, kcalInput;
    private ImageView profilePic;
    private TextView hoverProfilePic;
    private Spinner genderInfo;
    private DatePicker birthdayPicker;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_info);

        viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);

        inizialiseUI();
        setupListeners();

    }

    private void inizialiseUI() {

        fullNameText = findViewById(R.id.fullNameText);
        weightInput = findViewById(R.id.weightInput);
        heightInput = findViewById(R.id.heightInput);
        stepsInput = findViewById(R.id.stepsInput);
        waterInput = findViewById(R.id.waterInput);
        kcalInput = findViewById(R.id.kcalInput);

    }

    private void setupListeners() {

        profilePic.setOnClickListener(view -> openGallery());

        submitButton.setOnClickListener(view -> {

            try {

                String fullName = fullNameText.getText().toString();
                int weight = Integer.parseInt(weightInput.getText().toString());
                int height = Integer.parseInt(heightInput.getText().toString());
                int steps = Integer.parseInt(stepsInput.getText().toString());
                int water = Integer.parseInt(waterInput.getText().toString());
                int kcal = Integer.parseInt(kcalInput.getText().toString());
                int day = birthdayPicker.getDayOfMonth();
                int month = birthdayPicker.getMonth();
                int year = birthdayPicker.getYear();
                String gender = genderInfo.getSelectedItem().toString();


                if(fullName.equals("")) {
                    Toast.makeText(view.getContext(), "Full Name cannot be empty", Toast.LENGTH_SHORT).show();
                }
                else {

                    UserProfile user = viewModel.getUser().getValue();

                    user.setFullName(fullName);
                    user.setWeight(weight);
                    user.setHeight(height);
                    user.setDailySteps(steps);
                    user.setDailyWater(water);
                    user.setDailyKcal(kcal);
                    user.setDay(day);
                    user.setMonth(month);
                    user.setYear(year);
                    user.setGender(gender);

                    viewModel.setUser(user, this);

                }


            } catch (NumberFormatException e) {}


        });

    }

    private void openGallery() {
        ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    // Callback is invoked after the user selects a media item or closes the
                    // photo picker.
                    if (uri != null) {
                        profilePic.setImageURI(uri);
                        hoverProfilePic.setText("");
                    } else {
                        profilePic.setImageResource(R.drawable.ic_profile);
                        hoverProfilePic.setText(R.string.click_to_edit);
                    }
                });

        String mimeType = "image/*";
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(new ActivityResultContracts.PickVisualMedia.SingleMimeType(mimeType))
                .build());
    }


}

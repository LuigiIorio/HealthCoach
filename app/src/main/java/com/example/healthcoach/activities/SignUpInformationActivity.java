package com.example.healthcoach.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.healthcoach.R;
import com.example.healthcoach.models.UserProfile;
import com.example.healthcoach.viewmodels.SignUpViewModel;

public class SignUpInformationActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private SignUpViewModel viewModel;
    private EditText fullNameText, weightInput, heightInput, stepsInput, waterInput, kcalInput;
    private ImageView profilePic;
    private TextView hoverProfilePic;
    private Spinner genderInfo;
    private DatePicker birthdayPicker;
    private Button submitButton;
    private String imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_info);

        viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);
        viewModel.retriveUserInformation();

        inizialiseUI();
        setupListeners();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST) {
            if (data != null) {
                // Ottieni l'URI dell'immagine selezionata
                Uri selectedImageUri = data.getData();

                profilePic.setImageURI(selectedImageUri);
                profilePic.setImageTintList(null);
                hoverProfilePic.setText("");
                imageUri = selectedImageUri.toString();


            } else {

                TypedValue typedValue = new TypedValue();
                getTheme().resolveAttribute(R.attr.colorOnPrimary, typedValue, true);
                int colorOnPrimary = typedValue.data;

                profilePic.setImageResource(R.drawable.ic_profile);
                profilePic.setImageTintList(ColorStateList.valueOf(colorOnPrimary));
                hoverProfilePic.setText(R.string.click_to_edit);

            }
        }
    }

    private void inizialiseUI() {

        fullNameText = findViewById(R.id.fullNameText);
        weightInput = findViewById(R.id.weightInput);
        heightInput = findViewById(R.id.heightInput);
        stepsInput = findViewById(R.id.stepsInput);
        waterInput = findViewById(R.id.waterInput);
        kcalInput = findViewById(R.id.kcalInput);
        profilePic = findViewById(R.id.profilePicImg);
        hoverProfilePic = findViewById(R.id.hoverProfilePic);
        genderInfo = findViewById(R.id.genderList);
        birthdayPicker = findViewById(R.id.birthDatePicker);
        submitButton = findViewById(R.id.submitButton);

    }

    private void setupListeners() {

        profilePic.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        submitButton.setOnClickListener(view -> {
            String fullName = fullNameText.getText().toString().trim();
            String weightStr = weightInput.getText().toString().trim();
            String heightStr = heightInput.getText().toString().trim();
            String stepsStr = stepsInput.getText().toString().trim();
            String waterStr = waterInput.getText().toString().trim();
            String kcalStr = kcalInput.getText().toString().trim();
            String gender = genderInfo.getSelectedItem().toString().trim();

            if (fullName.isEmpty() || weightStr.isEmpty() || heightStr.isEmpty() || stepsStr.isEmpty() || waterStr.isEmpty() || kcalStr.isEmpty() || gender.isEmpty()) {
                Toast.makeText(view.getContext(), "All fields must be filled", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int weight = Integer.parseInt(weightStr);
                int height = Integer.parseInt(heightStr);
                int steps = Integer.parseInt(stepsStr);
                int water = Integer.parseInt(waterStr);
                int kcal = Integer.parseInt(kcalStr);
                int day = birthdayPicker.getDayOfMonth();
                int month = birthdayPicker.getMonth();
                int year = birthdayPicker.getYear();

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

                if (imageUri != null) {
                    user.setImage(imageUri.toString());
                } else {
                    Toast.makeText(view.getContext(), "Please upload an image", Toast.LENGTH_SHORT).show();
                    return;
                }

                viewModel.setUser(user, this);

            } catch (NumberFormatException e) {
                Toast.makeText(view.getContext(), "Invalid numerical input", Toast.LENGTH_SHORT).show();
            }
        });
    }




}

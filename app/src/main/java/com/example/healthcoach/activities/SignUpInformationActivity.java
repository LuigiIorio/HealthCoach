package com.example.healthcoach.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
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

public class SignUpInformationActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
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

                viewModel.getUser().getValue().setImage(selectedImageUri.toString());
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
            // Creazione di un intent per aprire la galleria delle immagini
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*"); // Filtraggio solo per file di immagine

            // Avvio dell'attività per selezionare un'immagine
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

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

}

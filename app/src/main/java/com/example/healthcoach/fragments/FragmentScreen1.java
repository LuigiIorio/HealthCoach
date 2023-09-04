package com.example.healthcoach.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import com.example.healthcoach.R;
import com.example.healthcoach.interfaces.HeartRateRepository;
import com.example.healthcoach.interfaces.WaterIntakeRepository;
import com.example.healthcoach.recordingapi.HeartRateBPM;
import com.example.healthcoach.recordingapi.Hydration;
import com.example.healthcoach.viewmodels.HeartRateViewModel;
import com.example.healthcoach.viewmodels.WaterIntakeViewModel;

public class FragmentScreen1 extends Fragment {

    // UI Components related to water intake
    private EditText waterIntakeEditText;
    private Button addWaterIntakeButton;

    // UI Component to display the heart rate
    private TextView heartRateTextView;

    // ViewModel for storing totalWaterIntake
    private WaterIntakeViewModel waterIntakeViewModel;

    // ViewModel for heart rate
    private HeartRateViewModel heartRateViewModel;

    // Hydration instance for Google Fit
    private Hydration hydration;

    // Define a request code for our permissions request.
    private static final int BODY_SENSOR_PERMISSION_REQUEST_CODE = 1001;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkBodySensorPermissionAndInitialize();
    }

    private void checkBodySensorPermissionAndInitialize() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.BODY_SENSORS) == PackageManager.PERMISSION_GRANTED) {
            // Initialize ViewModels here, but LiveData observation will be set in onCreateView.
            initializeViewModels();
        } else {
            requestPermissions(new String[]{android.Manifest.permission.BODY_SENSORS}, BODY_SENSOR_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Hydration.REQUEST_OAUTH_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(requireContext(), "Signed in to Google Fit successfully!", Toast.LENGTH_SHORT).show();
                // Ensure that we refresh the GoogleSignInAccount information after re-signing in
                hydration.refreshGoogleSignInAccount();
            } else {
                Toast.makeText(requireContext(), "Failed to sign in to Google Fit.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initializeViewModels() {
        hydration = new Hydration(getActivity());

        waterIntakeViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends ViewModel> T create(Class<T> modelClass) {
                return (T) new WaterIntakeViewModel((WaterIntakeRepository) hydration);
            }
        }).get(WaterIntakeViewModel.class);

        HeartRateRepository heartRateRepository = new HeartRateBPM(getActivity());
        heartRateViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends ViewModel> T create(Class<T> modelClass) {
                return (T) new HeartRateViewModel(heartRateRepository, getActivity());
            }
        }).get(HeartRateViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen1, container, false);
        initUI(view);
        observeData();
        return view;
    }

    private void initUI(View view) {
        // Initialize water intake UI components
        waterIntakeEditText = view.findViewById(R.id.waterIntakeEditText);
        addWaterIntakeButton = view.findViewById(R.id.addWaterIntakeButton);

        // Initialize heart rate UI component
        heartRateTextView = view.findViewById(R.id.heartRateTextView);

        addWaterIntakeButton.setOnClickListener(v -> {
            String waterIntakeString = waterIntakeEditText.getText().toString();
            if (waterIntakeString.isEmpty()) return;

            float waterIntake;
            try {
                waterIntake = Float.parseFloat(waterIntakeString);
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Please enter a valid number", Toast.LENGTH_SHORT).show();
                return;
            }

            waterIntakeViewModel.addWater(waterIntake);
            Toast.makeText(requireContext(), "Water added successfully!", Toast.LENGTH_SHORT).show();
        });
    }

    private void observeData() {
        if (heartRateViewModel != null) {
            heartRateViewModel.getHeartRateLiveData().observe(getViewLifecycleOwner(), bpm -> {
                heartRateTextView.setText(String.format("Heart Rate: %.2f BPM", bpm));
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == BODY_SENSOR_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeViewModels();
                observeData(); // Added to ensure data is observed upon permission grant.
            } else {
                Toast.makeText(requireContext(), "Body sensor permission is required to read heart rate data.", Toast.LENGTH_LONG).show();
            }
        }
    }
}

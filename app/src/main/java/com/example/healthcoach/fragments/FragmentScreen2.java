package com.example.healthcoach.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.healthcoach.R;
import com.example.healthcoach.interfaces.WaterIntakeRepository;
import com.example.healthcoach.recordingapi.Hydration;
import com.example.healthcoach.viewmodels.Screen2ViewModel;
import com.example.healthcoach.viewmodels.WaterIntakeViewModel;


public class FragmentScreen2 extends Fragment {

    private TextView journalTextView;
    private Screen2ViewModel viewModel;
    private EditText waterIntakeEditText;
    private Button addWaterIntakeButton;
    private WaterIntakeViewModel waterIntakeViewModel;
    private Hydration hydration;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(Screen2ViewModel.class);
        initializeViewModels();
    }

    private final ActivityResultLauncher<Intent> googleSignInResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Toast.makeText(requireContext(), "Signed in to Google Fit successfully!", Toast.LENGTH_SHORT).show();
                    hydration.refreshGoogleSignInAccount();
                } else {
                    Toast.makeText(requireContext(), "Failed to sign in to Google Fit.", Toast.LENGTH_SHORT).show();
                }
            }
    );

    private void initializeViewModels() {
        hydration = new Hydration(getActivity());
        waterIntakeViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends ViewModel> T create(Class<T> modelClass) {
                return (T) new WaterIntakeViewModel((WaterIntakeRepository) hydration);
            }
        }).get(WaterIntakeViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen2, container, false);
        journalTextView = view.findViewById(R.id.journalTextView);
        waterIntakeEditText = view.findViewById(R.id.waterIntakeEditText);
        addWaterIntakeButton = view.findViewById(R.id.addWaterIntakeButton);
        observeData();
        initWaterIntakeUI();
        return view;
    }

    private void observeData() {
        viewModel.getJournalText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                journalTextView.setText(s);
            }
        });
    }

    private void initWaterIntakeUI() {
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
}

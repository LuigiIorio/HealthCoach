package com.example.healthcoach.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.healthcoach.R;
import com.example.healthcoach.interfaces.WaterIntakeRepository;
import com.example.healthcoach.recordingapi.Hydration;
import com.example.healthcoach.viewmodel.WaterIntakeViewModel;




public class FragmentScreen1 extends Fragment {

    // UI Components
    private EditText waterIntakeEditText;
    private Button addWaterIntakeButton;
    private TextView waterIntakeTextView;

    // ViewModel for storing totalWaterIntake
    private WaterIntakeViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WaterIntakeRepository hydrationRepository = new Hydration(getActivity());
        viewModel = new ViewModelProvider(requireActivity(), new ViewModelProvider.Factory() {
            @Override
            public <T extends ViewModel> T create(Class<T> modelClass) {
                return (T) new WaterIntakeViewModel(hydrationRepository);
            }
        }).get(WaterIntakeViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen1, container, false);

        waterIntakeEditText = view.findViewById(R.id.waterIntakeEditText);
        addWaterIntakeButton = view.findViewById(R.id.addWaterIntakeButton);
        waterIntakeTextView = view.findViewById(R.id.waterIntakeTextView);

        // Observe the LiveData
        viewModel.getTotalWaterIntake().observe(getViewLifecycleOwner(), intake -> {
            waterIntakeTextView.setText(String.valueOf(intake) + " ml");
        });

        addWaterIntakeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String waterIntakeString = waterIntakeEditText.getText().toString();
                if (waterIntakeString.isEmpty()) return;

                float waterIntake;
                try {
                    waterIntake = Float.parseFloat(waterIntakeString);
                } catch (NumberFormatException e) {
                    waterIntakeTextView.setText("Please enter a valid number");
                    return;
                }

                viewModel.addWater(waterIntake);
            }
        });

        return view;
    }
}

package com.example.healthcoach;

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
import androidx.lifecycle.ViewModelProvider;

import com.example.healthcoach.viewmodel.WaterIntakeViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.HistoryClient;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class FragmentScreen1 extends Fragment {

    // Google Fit components
    private GoogleSignInAccount googleSignInAccount;
    private HistoryClient historyClient;

    // UI Components
    private EditText waterIntakeEditText;
    private Button addWaterIntakeButton;
    private TextView waterIntakeTextView;

    // ViewModel for storing totalWaterIntake
    private WaterIntakeViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(WaterIntakeViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen1, container, false);

        waterIntakeEditText = view.findViewById(R.id.waterIntakeEditText);
        addWaterIntakeButton = view.findViewById(R.id.addWaterIntakeButton);
        waterIntakeTextView = view.findViewById(R.id.waterIntakeTextView);

        // Initialize GoogleSignInAccount and HistoryClient
        googleSignInAccount = GoogleSignIn.getAccountForExtension(getActivity(), FitnessOptions.builder()
                .addDataType(DataType.TYPE_HYDRATION, FitnessOptions.ACCESS_WRITE)
                .build());
        historyClient = Fitness.getHistoryClient(getActivity(), googleSignInAccount);

        addWaterIntakeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (googleSignInAccount != null) {
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
                    waterIntakeTextView.setText(String.valueOf(viewModel.getTotalWaterIntake()) + " ml");
                    insertWaterIntakeData(waterIntake);
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        waterIntakeTextView.setText(String.valueOf(viewModel.getTotalWaterIntake()) + " ml");
    }

    private void insertWaterIntakeData(float waterIntake) {
        if (googleSignInAccount != null) {
            DataSource dataSource = new DataSource.Builder()
                    .setAppPackageName(getActivity())
                    .setDataType(DataType.TYPE_HYDRATION)
                    .setType(DataSource.TYPE_RAW)
                    .build();

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(System.currentTimeMillis());

            DataPoint dataPoint = DataPoint.builder(dataSource)
                    .setField(Field.FIELD_VOLUME, waterIntake)
                    .setTimeInterval(cal.getTimeInMillis(), cal.getTimeInMillis(), TimeUnit.MILLISECONDS)
                    .build();

            DataSet dataSet = DataSet.builder(dataSource)
                    .add(dataPoint)
                    .build();

            historyClient.insertData(dataSet)
                    .addOnSuccessListener(aVoid -> {
                        // Handle success
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                    });
        }
    }
}

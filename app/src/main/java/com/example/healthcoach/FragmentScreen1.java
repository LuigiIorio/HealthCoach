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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.concurrent.TimeUnit;

public class FragmentScreen1 extends Fragment {

    private static final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1;
    private GoogleSignInAccount googleSignInAccount;

    private EditText waterIntakeEditText;
    private Button addWaterIntakeButton;
    private TextView waterIntakeTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen1, container, false);

        waterIntakeEditText = view.findViewById(R.id.waterIntakeEditText);
        addWaterIntakeButton = view.findViewById(R.id.addWaterIntakeButton);
        waterIntakeTextView = view.findViewById(R.id.waterIntakeTextView);

        // Initialize GoogleSignInAccount
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_HYDRATION)
                .build();

        googleSignInAccount = GoogleSignIn.getAccountForExtension(getActivity(), fitnessOptions);

        addWaterIntakeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if googleSignInAccount is not null
                if (googleSignInAccount != null) {
                    // Get the water intake amount from the edit text
                    String waterIntakeString = waterIntakeEditText.getText().toString();

                    // Convert the water intake amount to a float
                    float waterIntake = Float.parseFloat(waterIntakeString);

                    // Create a DataSource object for the TYPE_WATER_CONSUMED data type
                    DataSource waterConsumptionDataSource = new DataSource.Builder()
                            .setAppPackageName(getActivity())
                            .setDataType(DataType.TYPE_HYDRATION)
                            .setType(DataSource.TYPE_RAW)
                            .build();

                    // Create a DataPoint object
                    DataPoint waterConsumptionDataPoint = DataPoint.builder(waterConsumptionDataSource)
                            .setTimestamp(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                            .setField(Field.FIELD_VOLUME, waterIntake)
                            .build();

                    // Create a DataSet object and add the DataPoint to it
                    DataSet dataSet = DataSet.builder(waterConsumptionDataSource)
                            .add(waterConsumptionDataPoint)
                            .build();

                    // Insert the DataSet into Google Fit
                    Fitness.getHistoryClient(getActivity(), googleSignInAccount)
                            .insertData(dataSet)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Data inserted successfully
                                    waterIntakeTextView.setText(waterIntakeString + " ml added");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle failure
                                }
                            });
                } else {
                    // Handle the case where googleSignInAccount is null
                }
            }
        });

        return view;
    }
}

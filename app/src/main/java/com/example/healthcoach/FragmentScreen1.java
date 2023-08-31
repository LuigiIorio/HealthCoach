package com.example.healthcoach;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.Scope;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen1, container, false);

        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_NUTRITION)
                .build();

        googleSignInAccount = GoogleSignIn.getAccountForExtension(getActivity(), fitnessOptions);

        Button addNutritionButton = view.findViewById(R.id.addNutritionButton);
        addNutritionButton.setOnClickListener(new View.OnClickListener() {





            @Override
            public void onClick(View v) {
                // Create a DataSource object for the TYPE_NUTRITION data type
                DataSource nutritionDataSource = new DataSource.Builder()
                        .setAppPackageName(getActivity())
                        .setDataType(DataType.TYPE_NUTRITION)
                        .setType(DataSource.TYPE_RAW)
                        .build();

                // Create a DataPoint object
                DataPoint nutritionDataPoint = DataPoint.builder(nutritionDataSource)
                        .setTimestamp(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                        .setField(Field.FIELD_MEAL_TYPE, Field.MEAL_TYPE_SNACK)
                        .setField(Field.FIELD_FOOD_ITEM, "Apple")
                        .setField(Field.FIELD_CALORIES, 95.0f)
                        .build();

                // Create a DataSet object and add the DataPoint to it
                DataSet dataSet = DataSet.builder(nutritionDataSource)
                        .add(nutritionDataPoint)
                        .build();

                // Insert the DataSet into Google Fit
                Fitness.getHistoryClient(getActivity(), googleSignInAccount)
                        .insertData(dataSet)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Data inserted successfully
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle failure
                            }
                        });
            }
        });



        return view;
    }
}


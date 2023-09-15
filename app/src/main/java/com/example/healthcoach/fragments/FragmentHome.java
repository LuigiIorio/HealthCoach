package com.example.healthcoach.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;
import com.example.healthcoach.R;
import com.example.healthcoach.viewmodels.HomeActivityViewModel;
import com.example.healthcoach.viewmodels.StepViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;

import java.util.ArrayList;
import java.util.List;


public class FragmentHome extends Fragment {

    private ProgressBar stepsProgressBar, kcalProgressBar, waterProgressBar, bpmProgressBar;
    private TextView totalSteps;
    private AnyChartView lineChart;

    private HomeActivityViewModel homeActivityViewModel;
    private StepViewModel stepViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        inizialiseUI(view);

        return view;

    }

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        homeActivityViewModel = new ViewModelProvider(requireActivity()).get(HomeActivityViewModel.class);
        stepViewModel = new ViewModelProvider(requireActivity()).get(StepViewModel.class);
    }



    private void inizialiseUI(View view) {

        View includedLayout = view.findViewById(R.id.include);

        stepsProgressBar = includedLayout.findViewById(R.id.circularProgressIndicator);
        totalSteps = includedLayout.findViewById(R.id.numberSteps);

        kcalProgressBar = view.findViewById(R.id.kcalCircle);
        waterProgressBar = view.findViewById(R.id.waterCircle);
        bpmProgressBar = view.findViewById(R.id.bpmCircle);

        lineChart = view.findViewById(R.id.historyChart);

        homeActivityViewModel.getUser().observe(this, steps -> {

            if(homeActivityViewModel.getUser().getValue() != null) {
                homeActivityViewModel.fetchData(this.getContext(), lineChart);
            }

        });

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("FragmentHome", "onResume called");

        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(getContext());

        if (googleSignInAccount != null) {
            Log.d("FragmentHome", "User is signed in");
            homeActivityViewModel.fetchTodaySteps(getContext());
            stepViewModel.startRecordingSteps(getContext());

            homeActivityViewModel.getStepCount().observe(getViewLifecycleOwner(), steps -> {
                totalSteps.setText("Steps: " + steps);
            });
        } else {
            Log.e("FragmentHome", "User is not signed in");
        }
    }



    @Override
    public void onPause() {
        super.onPause();
        stepViewModel.stopRecordingSteps(getContext());
    }

}

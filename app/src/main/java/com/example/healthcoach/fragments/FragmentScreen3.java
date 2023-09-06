package com.example.healthcoach.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.healthcoach.R;
import com.example.healthcoach.recordingapi.Hydration;
import com.example.healthcoach.recordingapi.Weight;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;


public class FragmentScreen3 extends Fragment {

    private Hydration hydration;
    private Weight weight;
    private TextView historyTextView;
    private Spinner dataTypeSpinner;
    private Button fetchDataButton;
    private int selectedYear;
    private int selectedMonth;
    private int selectedDay;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen3, container, false);
        initializeVariables(view);
        setupCalendarView(view);
        setupSpinner(view);
        setupFetchDataButton();
        return view;
    }

    private void initializeVariables(View view) {
        hydration = new Hydration(getActivity());
        weight = new Weight(getActivity());
        historyTextView = view.findViewById(R.id.historyTextView);
        dataTypeSpinner = view.findViewById(R.id.dataTypeSpinner);
        fetchDataButton = view.findViewById(R.id.fetchDataButton);

        // Initialize selectedYear, selectedMonth, and selectedDay
        Calendar calendar = Calendar.getInstance();
        selectedYear = calendar.get(Calendar.YEAR);
        selectedMonth = calendar.get(Calendar.MONTH);
        selectedDay = calendar.get(Calendar.DAY_OF_MONTH);
    }

    private void setupCalendarView(View view) {
        CalendarView calendarView = view.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectedYear = year;
                selectedMonth = month;
                selectedDay = dayOfMonth;
            }
        });
    }

    private void setupSpinner(View view) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.data_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataTypeSpinner.setAdapter(adapter);
    }

    private void setupFetchDataButton() {
        fetchDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long startTime = getDateInMillis(selectedYear, selectedMonth, selectedDay);
                long endTime = startTime + 24 * 60 * 60 * 1000;
                updateDataBasedOnType(dataTypeSpinner.getSelectedItem().toString(), startTime, endTime);
            }
        });
    }






    private long getDateInMillis(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar.getTimeInMillis();
    }

    private void updateDataBasedOnType(String type, long startTime, long endTime) {
        TextView historyTextView = getView().findViewById(R.id.historyTextView);

        if ("Hydration".equals(type)) {
            hydration.readHydrationData(startTime, endTime, new OnSuccessListener<DataReadResponse>() {
                @Override
                public void onSuccess(DataReadResponse dataReadResponse) {
                    float totalHydration = 0;
                    for (DataSet dataSet : dataReadResponse.getDataSets()) {
                        for (DataPoint point : dataSet.getDataPoints()) {
                            for (Field field : point.getDataType().getFields()) {
                                float hydrationValue = point.getValue(field).asFloat();
                                totalHydration += hydrationValue;
                            }
                        }
                    }
                    // Update UI with totalHydration
                    historyTextView.setText(String.format("Total water intake: %.2f L", totalHydration));
                }
            });
        } else if ("Weight".equals(type)) {
            weight.readWeightData(startTime, endTime, new OnSuccessListener<DataReadResponse>() {
                @Override
                public void onSuccess(DataReadResponse dataReadResponse) {
                    float latestWeight = 0;
                    long latestTime = 0;
                    for (DataSet dataSet : dataReadResponse.getDataSets()) {
                        for (DataPoint point : dataSet.getDataPoints()) {
                            for (Field field : point.getDataType().getFields()) {
                                float weightValue = point.getValue(field).asFloat();
                                long endTime = point.getEndTime(TimeUnit.MILLISECONDS);
                                if (endTime > latestTime) {
                                    latestTime = endTime;
                                    latestWeight = weightValue;
                                }
                            }
                        }
                    }
                    // Update UI with latestWeight
                    historyTextView.setText(String.format("Latest weight: %.2f kg", latestWeight));
                }
            });
        }
    }


}

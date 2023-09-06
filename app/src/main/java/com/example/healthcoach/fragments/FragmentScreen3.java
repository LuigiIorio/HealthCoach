package com.example.healthcoach.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Calendar;

public class FragmentScreen3 extends Fragment {

    private Hydration hydration;
    private TextView historyTextView;
    private Spinner dataTypeSpinner;
    private Button fetchDataButton;
    private int selectedYear;
    private int selectedMonth;
    private int selectedDay;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hydration = new Hydration(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen3, container, false);
        CalendarView calendarView = view.findViewById(R.id.calendarView);
        historyTextView = view.findViewById(R.id.historyTextView);
        dataTypeSpinner = view.findViewById(R.id.dataTypeSpinner);
        fetchDataButton = view.findViewById(R.id.fetchDataButton);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.data_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataTypeSpinner.setAdapter(adapter);

        dataTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Do nothing for now
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });

        fetchDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long startTime = getDateInMillis(selectedYear, selectedMonth, selectedDay);
                long endTime = startTime + 24 * 60 * 60 * 1000;
                updateDataBasedOnType(dataTypeSpinner.getSelectedItem().toString(), startTime, endTime);
            }
        });

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectedYear = year;
                selectedMonth = month;
                selectedDay = dayOfMonth;
            }
        });

        return view;
    }

    private long getDateInMillis(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar.getTimeInMillis();
    }

    private void updateDataBasedOnType(String type, long startTime, long endTime) {
        if ("Hydration".equals(type)) {
            hydration.readHydrationData(startTime, endTime, new OnSuccessListener<DataReadResponse>() {
                @Override
                public void onSuccess(DataReadResponse dataReadResponse) {
                    float totalHydration = 0;
                    for (DataPoint dp : dataReadResponse.getDataSet(DataType.TYPE_HYDRATION).getDataPoints()) {
                        for (Field field : dp.getDataType().getFields()) {
                            totalHydration += dp.getValue(field).asFloat();
                        }
                    }
                    if (totalHydration == 0) {
                        historyTextView.setText("No hydration data");
                    } else {
                        historyTextView.setText("Total Hydration: " + totalHydration + " ml");
                    }
                }
            });
        } else if ("Weight".equals(type)) {
            historyTextView.setText("No weight data");
        }
    }


}

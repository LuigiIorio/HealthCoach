package com.example.healthcoach.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.healthcoach.R;
import com.example.healthcoach.recordingapi.Hydration;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.result.DataReadResponse;

import java.util.Calendar;


public class FragmentScreen3 extends Fragment {

    private Hydration hydration;
    private TextView historyTextView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hydration = new Hydration(getActivity());
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen3, container, false);
        CalendarView calendarView = view.findViewById(R.id.calendarView);
        historyTextView = view.findViewById(R.id.historyTextView);  // Make sure this ID exists in your XML

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                long startTime = getDateInMillis(year, month, dayOfMonth);
                long endTime = startTime + 24 * 60 * 60 * 1000;

                hydration.readHydrationData(startTime, endTime, dataReadResponse -> {
                    displayData(dataReadResponse);
                });
            }
        });

        return view;
    }


    private long getDateInMillis(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar.getTimeInMillis();
    }

    private void displayData(DataReadResponse dataReadResponse) {
        StringBuilder builder = new StringBuilder();
        for (DataSet dataSet : dataReadResponse.getDataSets()) {
            for (DataPoint point : dataSet.getDataPoints()) {
                builder.append("Volume: ").append(point.getValue(Field.FIELD_VOLUME)).append("\n");
            }
        }
        historyTextView.setText(builder.toString());
    }


}

package com.example.healthcoach.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.healthcoach.R;

public class FragmentScreen3 extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_screen3, container, false);

        // Find the TextView and set its text
        TextView messageTextView = view.findViewById(R.id.messageTextView);
        messageTextView.setText("Welcome to Screen 3");

        return view;
    }
}

package com.example.healthcoach;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.widget.TextView;

import com.example.healthcoach.R;

public class FragmentScreen2 extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment's layout
        View view = inflater.inflate(R.layout.fragment_screen2, container, false);

        // Find the TextView and set the message
        TextView messageTextView = view.findViewById(R.id.messageTextView);
        messageTextView.setText("Welcome to Screen 2");

        return view;
    }
}

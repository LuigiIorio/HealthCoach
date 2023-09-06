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

public class FragmentScreen1 extends Fragment {

    // UI Component
    private TextView messageTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen1, container, false);
        initUI(view);
        return view;
    }

    private void initUI(View view) {
        messageTextView = view.findViewById(R.id.messageTextView);
        // If you have additional logic or UI components to initialize, you can do it here.
    }
}

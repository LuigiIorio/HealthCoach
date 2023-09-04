package com.example.healthcoach.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.Observer;

import com.example.healthcoach.R;
import com.example.healthcoach.viewmodels.Screen2ViewModel;



public class FragmentScreen2 extends Fragment {

    private TextView journalTextView;
    private Screen2ViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(Screen2ViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen2, container, false);
        journalTextView = view.findViewById(R.id.journalTextView);
        observeData();
        return view;
    }

    private void observeData() {
        viewModel.getJournalText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                journalTextView.setText(s);
            }
        });
    }
}

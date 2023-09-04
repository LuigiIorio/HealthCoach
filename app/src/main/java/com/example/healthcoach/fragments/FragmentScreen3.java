package com.example.healthcoach.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.healthcoach.R;
import androidx.lifecycle.ViewModelProvider;
import com.example.healthcoach.viewmodels.Screen3ViewModel;

public class FragmentScreen3 extends Fragment {

    private Screen3ViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(Screen3ViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_screen3, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView textView = view.findViewById(R.id.text_screen3);
        observeData(textView);
    }

    private void observeData(TextView textView) {
        viewModel.getScreenText().observe(getViewLifecycleOwner(), textView::setText);
    }
}

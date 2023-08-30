package com.example.healthcoach;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcoach.R;

import java.util.List;

public class FragmentScreen3 extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private UserViewModel userViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen3, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        userAdapter = new UserAdapter();
        recyclerView.setAdapter(userAdapter);

        // Update the TextViews with user data in the layout
        TextView emailTextView = view.findViewById(R.id.emailTextView);
        TextView ageTextView = view.findViewById(R.id.ageTextView);
        TextView genderTextView = view.findViewById(R.id.genderTextView);
        TextView birthdateTextView = view.findViewById(R.id.birthdateTextView);
        TextView weightTextView = view.findViewById(R.id.weightTextView);
        TextView heightTextView = view.findViewById(R.id.heightTextView);

        // Get the UserViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Get the currently logged in user's email (you need to implement this)
        String userEmail = getUserEmail(); // Replace this with the actual method to get the user's email

        // Observe the selected user's data from the ViewModel
        userViewModel.getSelectedUser(userEmail).observe(getViewLifecycleOwner(), new Observer<UserEntity>() {
            @Override
            public void onChanged(UserEntity userEntity) {
                if (userEntity != null) {
                    emailTextView.setText(userEntity.getEmail());
                    ageTextView.setText(userEntity.getAge());
                    genderTextView.setText(userEntity.getGender());
                    birthdateTextView.setText(userEntity.getBirthdate());
                    weightTextView.setText(userEntity.getWeight());
                    heightTextView.setText(userEntity.getHeight());
                }
            }
        });

        return view;
    }

    // Implement the method to get the user's email
    private String getUserEmail() {
        // Replace this with the actual implementation
        return "user@example.com";
    }
}

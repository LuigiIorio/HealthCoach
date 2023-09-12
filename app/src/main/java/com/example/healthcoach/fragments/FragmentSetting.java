package com.example.healthcoach.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import android.Manifest;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.example.healthcoach.R;
import com.example.healthcoach.activities.LoginActivity;
import com.example.healthcoach.viewmodels.HomeActivityViewModel;
import com.example.healthcoach.viewmodels.SettingsViewModel;
import com.example.healthcoach.viewmodels.SettingsViewModelFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.example.healthcoach.viewmodels.Event;




public class FragmentSetting extends Fragment {

    private ImageView profilePic;
    private TextView profilePicEdit;
    private EditText weightInput, heightInput, stepsInput, waterInput, kcalInput,
            newPasswordInput, confirmPasswordInput, oldPasswordInput;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 123;
    private Uri imageUri;
    private Button submitButton, logoutButton;
    private SettingsViewModel viewModel;

    private HomeActivityViewModel homeViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        profilePic = view.findViewById(R.id.profilePic);
        profilePicEdit = view.findViewById(R.id.infoProfileTextView);
        weightInput = view.findViewById(R.id.weightInput);
        heightInput = view.findViewById(R.id.heightInput);
        stepsInput = view.findViewById(R.id.stepsInput);
        waterInput = view.findViewById(R.id.waterInput);
        kcalInput = view.findViewById(R.id.kcalInput);
        newPasswordInput = view.findViewById(R.id.newPasswordInput);
        confirmPasswordInput = view.findViewById(R.id.confirmPasswordInput);
        oldPasswordInput = view.findViewById(R.id.oldPasswordInput);
        submitButton = view.findViewById(R.id.submitButton);
        logoutButton = view.findViewById(R.id.logoutButton);

        viewModel = new ViewModelProvider(this, new SettingsViewModelFactory(requireContext())).get(SettingsViewModel.class);
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeActivityViewModel.class);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            viewModel.fetchProfileUrlFromFirebase(userId);

            submitButton.setOnClickListener(v -> {
                if (imageUri != null) {
                    viewModel.uploadImage(imageUri, requireContext());
                    viewModel.saveProfileUrlToFirebase(userId, imageUri.toString());
                    viewModel.cacheLastUri(requireContext(), imageUri);
                } else {
                    // Handle case when imageUri is null
                }
            });

            logoutButton.setOnClickListener(v -> {
                if (viewModel.logoutUser()) {
                    // Navigate to login screen
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    // Show error message
                    Toast.makeText(requireContext(), "Failed to logout", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Handle the case when the user is not authenticated
            // You may want to show a login screen or take appropriate action.
            // For now, you can disable certain UI elements or display a message.
            submitButton.setEnabled(false);
            logoutButton.setEnabled(false);
        }

        Uri lastCachedUri = viewModel.getLastCachedUri(requireContext());
        if (lastCachedUri != null) {
            Picasso.get().load(lastCachedUri).into(profilePic);
        }

        viewModel.getUploadedImageUri().observe(getViewLifecycleOwner(), uri -> {
            if (uri != null) {
                Picasso.get().load(uri).into(profilePic);
                profilePicEdit.setVisibility(View.GONE);
            } else {
                profilePicEdit.setVisibility(View.VISIBLE);
            }
        });

        initializeUI();

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            viewModel.uploadImage(imageUri, requireContext());
        }
    }


    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            // Explain why you need the permission
        }

        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                // Permission denied
            }
        }
    }


    private void initializeUI() {
        profilePic.setOnClickListener(v -> openImageChooser());
        profilePicEdit.setOnClickListener(v -> openImageChooser());

        // Unified submitButton onClickListener
        submitButton.setOnClickListener(v -> {
            if (imageUri != null) {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // get user id
                viewModel.uploadImage(imageUri, requireContext());
                viewModel.saveProfileUrlToFirebase(userId, imageUri.toString()); // Save URL to Firebase
                viewModel.cacheLastUri(requireContext(), imageUri);
            } else {
                // Handle case when imageUri is null
            }
        });

        logoutButton.setOnClickListener(v -> {
            if (viewModel.logoutUser()) {
                // Navigate to login screen
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            } else {
                // Show error message
                Toast.makeText(requireContext(), "Failed to logout", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Uri lastUri = viewModel.getLastCachedUri(requireContext());
        if (lastUri != null) {
            Picasso.get().load(lastUri).into(profilePic);
        }
    }

}



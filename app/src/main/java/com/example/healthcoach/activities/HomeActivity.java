package com.example.healthcoach.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.healthcoach.R;
import com.example.healthcoach.fragments.FragmentScreen1;
import com.example.healthcoach.fragments.FragmentScreen2;
import com.example.healthcoach.fragments.FragmentScreen3;
import com.example.healthcoach.fragments.FragmentScreen4;
import com.example.healthcoach.viewmodels.HomeViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.Nullable;

public class HomeActivity extends AppCompatActivity {

    private TextView welcomeTextView;
    private Fragment fragment1, fragment2, fragment3, fragment4;
    private Fragment activeFragment;
    private HomeViewModel homeViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        welcomeTextView = findViewById(R.id.welcomeTextView);

        // Initialize ViewModel
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Observe ViewModel for changes to the welcome message
        homeViewModel.getWelcomeMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                welcomeTextView.setText(s);
            }
        });

        // Initialize fragments
        fragment1 = new FragmentScreen1();
        fragment2 = new FragmentScreen2();
        fragment3 = new FragmentScreen3();  // Diary Fragment
        fragment4 = new FragmentScreen4();  // Profile Fragment

        // Set initial active fragment
        activeFragment = fragment1;

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragment1, "1").commit();

        fragment1.setRetainInstance(true);
        fragment2.setRetainInstance(true);
        fragment3.setRetainInstance(true);
        fragment4.setRetainInstance(true);
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment;
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        selectedFragment = fragment1;
                        break;
                    case R.id.navigation_journal:
                        selectedFragment = fragment2;
                        break;
                    case R.id.navigation_diary:
                        selectedFragment = fragment3;
                        break;
                    case R.id.navigation_profile:
                        selectedFragment = fragment4;
                        break;
                    default:
                        return false;
                }

                if (selectedFragment != activeFragment) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment).commit();
                    activeFragment = selectedFragment;
                }

                return true;
            };

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(HomeActivity.this, MainActivity.class));
        finish();
    }
}

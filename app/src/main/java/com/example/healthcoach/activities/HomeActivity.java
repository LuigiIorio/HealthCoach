package com.example.healthcoach.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.healthcoach.R;
import com.example.healthcoach.fragments.FragmentScreen1;
import com.example.healthcoach.fragments.FragmentScreen2;
import com.example.healthcoach.fragments.FragmentScreen3;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    private TextView welcomeTextView;
    private Fragment fragment1, fragment2, fragment3;
    private Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        welcomeTextView = findViewById(R.id.welcomeTextView);

        // Initialize fragments
        fragment1 = new FragmentScreen1();
        fragment2 = new FragmentScreen2();
        fragment3 = new FragmentScreen3();

        // Set initial active fragment
        activeFragment = fragment1;

        // Add the code for bottom navigation view setup here
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        // Add the code to inflate the FragmentScreen1 fragment here
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragment1, "1").commit();

        // Set retain instance for fragments
        fragment1.setRetainInstance(true);
        fragment2.setRetainInstance(true);
        fragment3.setRetainInstance(true);
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
                    case R.id.navigation_profile:
                        selectedFragment = fragment3;
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
        // Navigate back to the login page (MainActivity)
        startActivity(new Intent(HomeActivity.this, MainActivity.class));
        finish(); // Close the current activity
    }
}

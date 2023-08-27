package com.example.healthcoach;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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

        // Set the initial fragment to display
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragment3, "3").hide(fragment3)
                .add(R.id.fragment_container, fragment2, "2").hide(fragment2)
                .add(R.id.fragment_container, fragment1, "1").commit();
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment;
                switch (item.getItemId()) {
                    case R.id.navigation_screen1:
                        selectedFragment = fragment1;
                        break;
                    case R.id.navigation_screen2:
                        selectedFragment = fragment2;
                        break;
                    case R.id.navigation_screen3:
                        selectedFragment = fragment3;
                        break;
                    default:
                        return false;
                }

                if (selectedFragment != activeFragment) {
                    getSupportFragmentManager().beginTransaction()
                            .hide(activeFragment).show(selectedFragment).commit();
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

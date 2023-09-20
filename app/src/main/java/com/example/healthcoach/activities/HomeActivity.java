package com.example.healthcoach.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.healthcoach.R;
import com.example.healthcoach.fragments.HomeFragment;
import com.example.healthcoach.fragments.ProfileFragment;
import com.example.healthcoach.fragments.HistoryFragment;
import com.example.healthcoach.viewmodels.HomeActivityViewModel;
import com.example.healthcoach.fragments.SettingFragment;
import com.example.healthcoach.viewmodels.StepViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.Nullable;



public class HomeActivity extends AppCompatActivity {

    private Fragment fragment1, fragment2, fragment3, fragment4;
    private Fragment activeFragment;

    private HomeActivityViewModel homeActivityViewModel;

    /**
     * Initializes the HomeActivity and its associated fragments.
     *
     * - Sets up the view model for data manipulation and observation.
     * - Initializes four different fragments: HomeFragment, ProfileFragment, HistoryFragment, and SettingFragment.
     * - Sets the initial active fragment to HomeFragment.
     * - Fetches today's hydration data via the view model.
     * - Sets up the BottomNavigationView and its event listener for fragment navigation.
     * - Adds the initial fragment to the container.
     *
     * @param savedInstanceState A mapping from String keys to various Parcelable values.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        homeActivityViewModel = new ViewModelProvider(this).get(HomeActivityViewModel.class);

        // Initialize fragments
        fragment1 = new HomeFragment();
        fragment2 = new ProfileFragment();
        fragment3 = new HistoryFragment();
        fragment4 = new SettingFragment();

        // Set initial active fragment
        activeFragment = fragment1;

        // Fetch today's hydration data
        homeActivityViewModel.fetchTodayHydration(this);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragment1, "1").commit();

        fragment1.setRetainInstance(true);
        fragment2.setRetainInstance(true);
        fragment3.setRetainInstance(true);
        fragment4.setRetainInstance(true);
    }


    /**
     * Handles navigation item clicks in the BottomNavigationView.
     *
     * - Switches between different fragments based on the clicked navigation item.
     * - Replaces the current fragment with the selected fragment.
     * - Updates the activeFragment variable to keep track of the currently displayed fragment.
     *
     * @return true if navigation item is handled, false otherwise.
     */
    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment;
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        selectedFragment = fragment1;
                        break;
                    case R.id.navigation_profile:
                        selectedFragment = fragment2;
                        break;
                    case R.id.navigation_history:
                        selectedFragment = fragment3;
                        break;
                    case R.id.navigation_setting:
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



}

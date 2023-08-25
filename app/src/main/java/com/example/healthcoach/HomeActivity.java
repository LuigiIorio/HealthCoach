
package com.example.healthcoach;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    private TextView welcomeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        welcomeTextView = findViewById(R.id.welcomeTextView);
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        // Navigate back to the login page (MainActivity)
        startActivity(new Intent(HomeActivity.this, MainActivity.class));
        finish(); // Close the current activity
    }



}
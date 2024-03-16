package com.example.myandroidapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RateActivity extends AppCompatActivity {
    private RatingBar ratingBar;
    private Button submitRatingButton;
    private DatabaseReference ratingsRef;
    private FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        ratingsRef = FirebaseDatabase.getInstance().getReference().child("ratings");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Initialize Views
        ratingBar = findViewById(R.id.ratingBar);
        submitRatingButton = findViewById(R.id.submitRatingButton);

        // Submit Rating Button Listener
        submitRatingButton.setOnClickListener(v -> submitRating());
    }
    private void submitRating() {
        float rating = ratingBar.getRating();
        if (rating > 0) {
            // Save rating to Firebase
            ratingsRef.child(currentUser.getUid()).setValue(rating)
                    .addOnSuccessListener(aVoid -> Toast.makeText(RateActivity.this, "Rating submitted successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(RateActivity.this, "Failed to submit rating", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(RateActivity.this, "Please provide a rating", Toast.LENGTH_SHORT).show();
        }
    }
}
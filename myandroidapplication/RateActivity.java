package com.example.myandroidapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RateActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private EditText reviewEditText;
    private Button likeButton, dislikeButton, submitRatingButton;
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
        reviewEditText = findViewById(R.id.reviewEditText);
        likeButton = findViewById(R.id.likeButton);
        dislikeButton = findViewById(R.id.dislikeButton);
        submitRatingButton = findViewById(R.id.submitRatingButton);

        // Submit Rating Button Listener
        submitRatingButton.setOnClickListener(v -> submitRating());

        // Like Button Listener
        likeButton.setOnClickListener(v -> {
            // Handle Like Button Click
            // Save like status to Firebase
            ratingsRef.child(currentUser.getUid()).child("like").setValue(true);
            ratingsRef.child(currentUser.getUid()).child("dislike").setValue(false);
            Toast.makeText(RateActivity.this, "Liked!", Toast.LENGTH_SHORT).show();
        });

        // Dislike Button Listener
        dislikeButton.setOnClickListener(v -> {
            // Handle Dislike Button Click
            // Save dislike status to Firebase
            ratingsRef.child(currentUser.getUid()).child("like").setValue(false);
            ratingsRef.child(currentUser.getUid()).child("dislike").setValue(true);
            Toast.makeText(RateActivity.this, "Disliked!", Toast.LENGTH_SHORT).show();
        });
    }
    private void submitRating() {
        float rating = ratingBar.getRating();
        String review = reviewEditText.getText().toString().trim();

        if (rating > 0) {
            // Save rating and review to Firebase
            ratingsRef.child(currentUser.getUid()).child("rating").setValue(rating);
            ratingsRef.child(currentUser.getUid()).child("review").setValue(review);

            Toast.makeText(RateActivity.this, "Rating and review submitted successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(RateActivity.this, "Please provide a rating", Toast.LENGTH_SHORT).show();
        }
    }
}
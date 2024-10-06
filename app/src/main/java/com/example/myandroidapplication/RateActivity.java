package com.example.myandroidapplication;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myandroidapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RateActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private EditText reviewEditText;
    private ImageView likeButton, dislikeButton;
    private Button submitRatingButton;
    private DatabaseReference ratingsRef;
    private FirebaseUser currentUser;
    private TextView[] ratingCountTextViews = new TextView[5];
    private TextView likeCountTextView, dislikeCountTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        ratingsRef = FirebaseDatabase.getInstance().getReference().child("ratings");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        ratingBar = findViewById(R.id.ratingBar);
        reviewEditText = findViewById(R.id.reviewEditText);
        likeButton = findViewById(R.id.likeButton);
        dislikeButton = findViewById(R.id.dislikeButton);
        ratingCountTextViews[0] = findViewById(R.id.rating1CountTextView);
        ratingCountTextViews[1] = findViewById(R.id.rating2CountTextView);
        ratingCountTextViews[2] = findViewById(R.id.rating3CountTextView);
        ratingCountTextViews[3] = findViewById(R.id.rating4CountTextView);
        ratingCountTextViews[4] = findViewById(R.id.rating5CountTextView);
        likeCountTextView = findViewById(R.id.likeCountTextView);
        dislikeCountTextView = findViewById(R.id.dislikeCountTextView);
        submitRatingButton = findViewById(R.id.submitRatingButton);

        fetchCounts();
        submitRatingButton.setOnClickListener(v -> submitRating());


        likeButton.setOnClickListener(v -> {
            ratingsRef.child(currentUser.getUid()).child("like").setValue(true);
            ratingsRef.child(currentUser.getUid()).child("dislike").setValue(false);
            Toast.makeText(RateActivity.this, "Liked!", Toast.LENGTH_SHORT).show();
            fetchCounts();
        });

        dislikeButton.setOnClickListener(v -> {
            ratingsRef.child(currentUser.getUid()).child("like").setValue(false);
            ratingsRef.child(currentUser.getUid()).child("dislike").setValue(true);
            Toast.makeText(RateActivity.this, "Disliked!", Toast.LENGTH_SHORT).show();
            fetchCounts();

        });
    }

    private void fetchCounts() {
        ratingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int[] ratingCounts = new int[5];
                int likeCount = 0, dislikeCount = 0;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.child("rating").exists()) {
                        float rating = dataSnapshot.child("rating").getValue(Float.class);
                        if (rating >= 1 && rating <= 5) {
                            int index = (int) rating - 1;
                            ratingCounts[index]++;
                        }
                    }

                    if (dataSnapshot.child("like").exists() && dataSnapshot.child("like").getValue(Boolean.class))
                        likeCount++;
                    if (dataSnapshot.child("dislike").exists() && dataSnapshot.child("dislike").getValue(Boolean.class))
                        dislikeCount++;
                }

                for (int i = 0; i < ratingCounts.length; i++) {
                    ratingCountTextViews[i].setText("Rating " + (i + 1) + ": " + ratingCounts[i] + " person(s)");
                }
                likeCountTextView.setText("Likes: " + likeCount);
                dislikeCountTextView.setText("Dislikes: " + dislikeCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RateActivity.this, "Failed to fetch ratings", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitRating() {
        float rating = ratingBar.getRating();
        String review = reviewEditText.getText().toString().trim();

        if (rating > 0) {
            ratingsRef.child(currentUser.getUid()).child("rating").setValue(rating);
            ratingsRef.child(currentUser.getUid()).child("review").setValue(review);

            Toast.makeText(RateActivity.this, "Rating and review submitted successfully", Toast.LENGTH_SHORT).show();
            fetchCounts();
        } else {
            Toast.makeText(RateActivity.this, "Please provide a rating", Toast.LENGTH_SHORT).show();
        }
    }
}

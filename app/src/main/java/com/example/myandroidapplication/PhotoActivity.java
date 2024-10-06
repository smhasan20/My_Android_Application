package com.example.myandroidapplication;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.myandroidapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PhotoActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imageView;
    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        imageView = findViewById(R.id.imageView);
        Button btnChoose = findViewById(R.id.btnChoose);
        Button btnUpload = findViewById(R.id.btnUpload);

        btnChoose.setOnClickListener(view -> openFileChooser());

        btnUpload.setOnClickListener(view -> uploadFile());


        loadUserPhoto();
    }
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void uploadFile() {
        if (imageUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("photos").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("profile.jpg");

            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(PhotoActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();


                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {

                            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("photo").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            databaseRef.child("photoUrl").setValue(uri.toString());


                            loadUserPhoto();
                        });
                    })
                    .addOnFailureListener(e -> Toast.makeText(PhotoActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    private void loadUserPhoto() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("photo").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("photoUrl")) {
                    String photoUrl = dataSnapshot.child("photoUrl").getValue(String.class);
                    if (photoUrl != null && !photoUrl.isEmpty()) {

                        Glide.with(PhotoActivity.this)
                                .load(photoUrl)
                                .placeholder(R.drawable.oggy)
                                .error(R.drawable.error)
                                .into(imageView);
                    } else {

                        Log.d("PhotoActivity", "Photo URL is null or empty");
                    }
                } else {

                    Log.d("PhotoActivity", "Photo URL does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.d("PhotoActivity", "Database error: " + databaseError.getMessage());
            }
        });
    }
    }

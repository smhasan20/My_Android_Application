package com.example.myandroidapplication;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.myandroidapplication.R;
import com.example.myandroidapplication.WorkItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditWorkItemActivity extends AppCompatActivity {

    private EditText editTextWorkItem;
    private Button buttonSave;
    private Button buttonSelectDateTime; // Add a button for selecting date and time

    private DatabaseReference databaseReference;
    private String workItemId;
    private Calendar selectedCalendar; // Calendar instance to store selected date and time

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_work_item);

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("work_items");

        // Get work item ID passed from WorklistActivity
        workItemId = getIntent().getStringExtra("workItemId");

        // Initialize UI components
        editTextWorkItem = findViewById(R.id.editTextWorkItem);
        buttonSave = findViewById(R.id.buttonSave);
        buttonSelectDateTime = findViewById(R.id.buttonSelectDateTime); // Initialize the button

        // Load work item details for editing
        loadWorkItemDetails();

        // Set click listener for selecting date and time
        buttonSelectDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker(); // Call method to show date and time picker dialogs
            }
        });

        // Set click listener for saving changes
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a new WorkItem instance with updated data
                String updatedWorkItemText = editTextWorkItem.getText().toString().trim();
                long updatedDueDateTime = selectedCalendar.getTimeInMillis(); // Use the selected date and time
                WorkItem updatedWorkItem = new WorkItem(workItemId, updatedWorkItemText);
                updatedWorkItem.setDueDate(updatedDueDateTime);

                // Call the saveChanges method with the updatedWorkItem parameter
                saveChanges(updatedWorkItem);
            }
        });
    }

    private void loadWorkItemDetails() {
        databaseReference.child(workItemId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                WorkItem workItem = dataSnapshot.getValue(WorkItem.class);
                if (workItem != null) {
                    editTextWorkItem.setText(workItem.getText());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditWorkItemActivity.this, "Failed to load work item details.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDateTimePicker() {
        // Initialize selectedCalendar with the current date and time
        selectedCalendar = Calendar.getInstance();

        // Launch a date picker dialog to select the date
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Set the selected date to the calendar instance
                        selectedCalendar.set(Calendar.YEAR, year);
                        selectedCalendar.set(Calendar.MONTH, monthOfYear);
                        selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        // Launch a time picker dialog to select the time
                        TimePickerDialog timePickerDialog = new TimePickerDialog(EditWorkItemActivity.this,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        // Set the selected time to the calendar instance
                                        selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        selectedCalendar.set(Calendar.MINUTE, minute);

                                        // Update the button
                                        buttonSelectDateTime.setText(getFormattedDateTime(selectedCalendar.getTimeInMillis()));
                                    }
                                }, selectedCalendar.get(Calendar.HOUR_OF_DAY), selectedCalendar.get(Calendar.MINUTE), false);

                        // Show the time picker dialog
                        timePickerDialog.show();
                    }
                }, selectedCalendar.get(Calendar.YEAR), selectedCalendar.get(Calendar.MONTH), selectedCalendar.get(Calendar.DAY_OF_MONTH));

        // Show the date picker dialog
        datePickerDialog.show();
    }

    // Inside the saveChanges method after saving changes to the database
    private void saveChanges(WorkItem updatedWorkItem) {
        // Update the work item's text, date, and time in the database
        databaseReference.child(workItemId).setValue(updatedWorkItem)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditWorkItemActivity.this, "Changes saved.", Toast.LENGTH_SHORT).show();

                        finish(); // Finish the activity after saving changes
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditWorkItemActivity.this, "Failed to save changes.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Utility method to format date and time
    private String getFormattedDateTime(long dateTimeMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateTimeMillis);

        // Format the date and time as desired, e.g., "March 17, 2024 10:30 AM"
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }
}

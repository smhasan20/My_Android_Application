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
    private Button buttonSelectDateTime;

    private DatabaseReference databaseReference;
    private String workItemId;
    private Calendar selectedCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_work_item);


        databaseReference = FirebaseDatabase.getInstance().getReference("work_items");


        workItemId = getIntent().getStringExtra("workItemId");


        editTextWorkItem = findViewById(R.id.editTextWorkItem);
        buttonSave = findViewById(R.id.buttonSave);
        buttonSelectDateTime = findViewById(R.id.buttonSelectDateTime); // Initialize the button


        loadWorkItemDetails();


        buttonSelectDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker();
            }
        });


        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String updatedWorkItemText = editTextWorkItem.getText().toString().trim();
                long updatedDueDateTime = selectedCalendar.getTimeInMillis(); // Use the selected date and time
                WorkItem updatedWorkItem = new WorkItem(workItemId, updatedWorkItemText);
                updatedWorkItem.setDueDate(updatedDueDateTime);


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

        selectedCalendar = Calendar.getInstance();


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        selectedCalendar.set(Calendar.YEAR, year);
                        selectedCalendar.set(Calendar.MONTH, monthOfYear);
                        selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);


                        TimePickerDialog timePickerDialog = new TimePickerDialog(EditWorkItemActivity.this,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                        selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        selectedCalendar.set(Calendar.MINUTE, minute);


                                        buttonSelectDateTime.setText(getFormattedDateTime(selectedCalendar.getTimeInMillis()));
                                    }
                                }, selectedCalendar.get(Calendar.HOUR_OF_DAY), selectedCalendar.get(Calendar.MINUTE), false);


                        timePickerDialog.show();
                    }
                }, selectedCalendar.get(Calendar.YEAR), selectedCalendar.get(Calendar.MONTH), selectedCalendar.get(Calendar.DAY_OF_MONTH));


        datePickerDialog.show();
    }


    private void saveChanges(WorkItem updatedWorkItem) {

        databaseReference.child(workItemId).setValue(updatedWorkItem)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditWorkItemActivity.this, "Changes saved.", Toast.LENGTH_SHORT).show();

                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditWorkItemActivity.this, "Failed to save changes.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private String getFormattedDateTime(long dateTimeMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateTimeMillis);


        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }
}

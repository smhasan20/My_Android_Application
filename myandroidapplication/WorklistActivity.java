package com.example.myandroidapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WorklistActivity extends AppCompatActivity implements WorkItemAdapter.OnWorkItemClickListener {

    private EditText editTextWorkItem;
    private Button buttonAddWorkItem;
    private RecyclerView recyclerViewWorkItems;

    private DatabaseReference databaseReference;
    private List<WorkItem> workItemList;
    private WorkItemAdapter workItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worklist);

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("work_items");

        // Initialize UI components
        editTextWorkItem = findViewById(R.id.editTextWorkItem);
        buttonAddWorkItem = findViewById(R.id.buttonAddWorkItem);
        recyclerViewWorkItems = findViewById(R.id.recyclerViewWorkItems);

        // Initialize RecyclerView
        recyclerViewWorkItems.setHasFixedSize(true);
        recyclerViewWorkItems.setLayoutManager(new LinearLayoutManager(this));

        // Initialize list to store work items
        workItemList = new ArrayList<>();

        // Initialize adapter for RecyclerView
        workItemAdapter = new WorkItemAdapter(this, workItemList, this);

        recyclerViewWorkItems.setAdapter(workItemAdapter);

        // Load work items from Firebase Database
        loadWorkItems();

        // Set click listener for adding a new work item
        buttonAddWorkItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWorkItem();
            }
        });
    }

    private void loadWorkItems() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                workItemList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    WorkItem workItem = snapshot.getValue(WorkItem.class);
                    workItemList.add(workItem);
                }
                // Sort the list of work items
                Collections.sort(workItemList, new Comparator<WorkItem>() {
                    @Override
                    public int compare(WorkItem workItem1, WorkItem workItem2) {
                        // Compare work items based on their due dates
                        return Long.compare(workItem1.getDueDate(), workItem2.getDueDate());
                    }
                });
                workItemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(WorklistActivity.this, "Failed to load work items.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void addWorkItem() {
        String workItemText = editTextWorkItem.getText().toString().trim();
        if (!workItemText.isEmpty()) {
            // Get the current date and time
            Calendar currentCalendar = Calendar.getInstance();

            // Launch a date picker dialog to select the date
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            // Set the selected date to the calendar instance
                            currentCalendar.set(Calendar.YEAR, year);
                            currentCalendar.set(Calendar.MONTH, monthOfYear);
                            currentCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                            // Launch a time picker dialog to select the time
                            TimePickerDialog timePickerDialog = new TimePickerDialog(WorklistActivity.this,
                                    new TimePickerDialog.OnTimeSetListener() {
                                        @Override
                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                            // Set the selected time to the calendar instance
                                            currentCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                            currentCalendar.set(Calendar.MINUTE, minute);

                                            // Add 1 day to the due date if the selected time is in the past
                                            if (currentCalendar.getTimeInMillis() < System.currentTimeMillis()) {
                                                currentCalendar.add(Calendar.DAY_OF_YEAR, 1);
                                            }

                                            // Convert the due date to milliseconds
                                            long dueDate = currentCalendar.getTimeInMillis();

                                            // Create a new work item with the specified text and due date
                                            String id = databaseReference.push().getKey();
                                            WorkItem workItem = new WorkItem(id, workItemText);
                                            workItem.setDueDate(dueDate);

                                            // Save the work item to the database
                                            if (id != null) {
                                                databaseReference.child(id).setValue(workItem)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Toast.makeText(WorklistActivity.this, "Work item added.", Toast.LENGTH_SHORT).show();
                                                                editTextWorkItem.setText("");
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(WorklistActivity.this, "Failed to add work item.", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        }
                                    }, currentCalendar.get(Calendar.HOUR_OF_DAY), currentCalendar.get(Calendar.MINUTE), false);

                            // Show the time picker dialog
                            timePickerDialog.show();
                        }
                    }, currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DAY_OF_MONTH));

            // Show the date picker dialog
            datePickerDialog.show();
        } else {
            Toast.makeText(this, "Please enter a work item.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onEditClick(WorkItem workItem) {
        // Implement edit functionality here
        // Open the EditWorkItemActivity and pass the work item id to it
        Intent intent = new Intent(this, EditWorkItemActivity.class);
        intent.putExtra("workItemId", workItem.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(WorkItem workItem) {
        // Delete the work item from Firebase Database
        databaseReference.child(workItem.getId()).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(WorklistActivity.this, "Work item deleted.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(WorklistActivity.this, "Failed to delete work item.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

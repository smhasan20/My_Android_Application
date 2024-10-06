package com.example.myandroidapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WorklistActivity extends AppCompatActivity implements WorkItemAdapter.OnWorkItemClickListener {

    private EditText editTextWorkItem;
    private Button buttonAddWorkItem;
    private RecyclerView recyclerViewWorkItems;

    private DatabaseReference databaseReference;
    private List<WorkItem> workItemList;
    private WorkItemAdapter workItemAdapter;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private Spinner spinnerCategory;
    private String[] categories = {"Education", "Sports", "Entertainment", "Tour", "Others"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worklist);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


        databaseReference = FirebaseDatabase.getInstance().getReference("work_items");


        editTextWorkItem = findViewById(R.id.editTextWorkItem);
        buttonAddWorkItem = findViewById(R.id.buttonAddWorkItem);
        recyclerViewWorkItems = findViewById(R.id.recyclerViewWorkItems);

        spinnerCategory = findViewById(R.id.spinnerCategory);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.category_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);


        recyclerViewWorkItems.setHasFixedSize(true);
        recyclerViewWorkItems.setLayoutManager(new LinearLayoutManager(this));


        workItemList = new ArrayList<>();


        workItemAdapter = new WorkItemAdapter(this, workItemList, this);

        recyclerViewWorkItems.setAdapter(workItemAdapter);


        loadWorkItems();


        buttonAddWorkItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWorkItem();
            }
        });
    }

    private void loadWorkItems() {

        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();


        DatabaseReference userWorkItemsRef = databaseReference.child(currentUserID);

        userWorkItemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                workItemList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    WorkItem workItem = snapshot.getValue(WorkItem.class);
                    workItemList.add(workItem);
                }

                Collections.sort(workItemList, new Comparator<WorkItem>() {
                    @Override
                    public int compare(WorkItem workItem1, WorkItem workItem2) {
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

            Calendar currentCalendar = Calendar.getInstance();


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                            currentCalendar.set(Calendar.YEAR, year);
                            currentCalendar.set(Calendar.MONTH, monthOfYear);
                            currentCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);


                            TimePickerDialog timePickerDialog = new TimePickerDialog(WorklistActivity.this,
                                    new TimePickerDialog.OnTimeSetListener() {
                                        @Override
                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                            currentCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                            currentCalendar.set(Calendar.MINUTE, minute);


                                            long dueDate = currentCalendar.getTimeInMillis();


                                            String selectedCategory = spinnerCategory.getSelectedItem().toString();


                                            String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();


                                            DatabaseReference userWorkItemsRef = databaseReference.child(currentUserID);


                                            String id = userWorkItemsRef.push().getKey();
                                            WorkItem workItem = new WorkItem(id, workItemText, selectedCategory, dueDate);


                                            if (id != null) {
                                                userWorkItemsRef.child(id).setValue(workItem)
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


                            timePickerDialog.show();
                        }
                    }, currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DAY_OF_MONTH));


            datePickerDialog.show();
        } else {
            Toast.makeText(this, "Please enter a work item.", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onEditClick(WorkItem workItem) {

        Intent intent = new Intent(this, EditWorkItemActivity.class);
        intent.putExtra("workItemId", workItem.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(WorkItem workItem) {

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

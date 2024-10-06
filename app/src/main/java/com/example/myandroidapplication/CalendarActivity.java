package com.example.myandroidapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

public class CalendarActivity extends AppCompatActivity {

    private TextView textView;
    private DatePicker datePicker;
    private Button selectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        textView=(TextView) findViewById(R.id.textViewId);
        selectButton=(Button) findViewById(R.id.buttonId);
        datePicker=(DatePicker) findViewById(R.id.datePickerId);

        textView.setText(currentDate());
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText(currentDate());
            }
        });
    }

    String currentDate(){
        StringBuilder stringBuilder=new StringBuilder();

        stringBuilder.append("Current Date: ");
        stringBuilder.append(datePicker.getDayOfMonth()+"/");
        stringBuilder.append((datePicker.getMonth()+1)+"/");
        stringBuilder.append(datePicker.getYear());

        return stringBuilder.toString();
    }
}
package com.example.myandroidapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.ktx.Firebase;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button Logout;
    private CardView notescardview,worklistcardview,photocardview,currencycardview,detectioncardview,mapcardview,ratecardview,entertainmentcardview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        notescardview=findViewById(R.id.notes);
        worklistcardview=findViewById(R.id.worklist);
        photocardview=findViewById(R.id.photo);
        currencycardview=findViewById(R.id.currency);
        detectioncardview=findViewById(R.id.detection);
        mapcardview=findViewById(R.id.map);
        ratecardview=findViewById(R.id.rate);
        entertainmentcardview=findViewById(R.id.entertainment);


        notescardview.setOnClickListener(this);
        worklistcardview.setOnClickListener(this);
        photocardview.setOnClickListener(this);
        currencycardview.setOnClickListener(this);
        detectioncardview.setOnClickListener(this);
        mapcardview.setOnClickListener(this);
        ratecardview.setOnClickListener(this);
        entertainmentcardview.setOnClickListener(this);


        Logout=findViewById(R.id.logout);
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
               startActivity(new Intent(MainActivity.this,LoginActivity.class));
                finish();
                Toast.makeText(MainActivity.this,"Successfully Logout",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {


        if(v.getId()==R.id.worklist)
        {
            Intent intent = new Intent(MainActivity.this,WorklistActivity.class);
            startActivity(intent);
        }
        else if(v.getId()==R.id.notes)
        {
            Intent intent = new Intent(MainActivity.this,NotesActivity.class);
            startActivity(intent);
        }
        else if(v.getId()==R.id.photo)
        {
            Intent intent = new Intent(MainActivity.this,PhotoActivity.class);
            startActivity(intent);
        }
        else if(v.getId()==R.id.currency)
        {
            Intent intent = new Intent(MainActivity.this,CurrencyActivity.class);
            startActivity(intent);
        }
        else if(v.getId()==R.id.detection)
        {
            Intent intent = new Intent(MainActivity.this,DetectionActivity.class);
            startActivity(intent);
        }
        else if(v.getId()==R.id.map)
        {
            Intent intent = new Intent(MainActivity.this,MapActivity.class);
            startActivity(intent);
        }
        else if(v.getId()==R.id.rate)
        {
            Intent intent = new Intent(MainActivity.this,RateActivity.class);
            startActivity(intent);
        }
        else if(v.getId()==R.id.entertainment)
        {
            Intent intent = new Intent(MainActivity.this,EntertainmentActivity.class);
            startActivity(intent);
        }

    }
}
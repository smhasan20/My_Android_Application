package com.example.myandroidapplication;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Handle alarm received
        String workItemText = intent.getStringExtra("workItemText");
        Toast.makeText(context, "Alarm for work item: " + workItemText, Toast.LENGTH_LONG).show();
    }
}

package com.example.network; // Change this to your actual package

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class NotificationHistory extends AppCompatActivity {

    private TextView tvHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_history); // XML layout

        tvHistory = findViewById(R.id.tvHistory);
        tvHistory.setMovementMethod(new ScrollingMovementMethod()); // enable scrolling

        // Retrieve stored notification history
        SharedPreferences prefs = getSharedPreferences("notifications", MODE_PRIVATE);
        String history = prefs.getString("history", "No notifications yet.");
        tvHistory.setText(history);
    }
}

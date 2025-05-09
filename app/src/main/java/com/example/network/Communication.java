package com.example.network;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;


public class Communication extends AppCompatActivity {

    EditText etPhone, etMessage;
    Button btnSendSMS, btnMakeCall, btnNotify;

    private final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communication);

        etPhone = findViewById(R.id.etPhone);
        etMessage = findViewById(R.id.etMessage);
        btnSendSMS = findViewById(R.id.btnSendSMS);
        btnMakeCall = findViewById(R.id.btnMakeCall);
        btnNotify = findViewById(R.id.btnNotify);

        requestPermissions(); // Ask user for runtime permissions

        btnSendSMS.setOnClickListener(view -> sendSMS());
        btnMakeCall.setOnClickListener(view -> makeCall());
        btnNotify.setOnClickListener(view -> showNotification());
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.SEND_SMS, Manifest.permission.CALL_PHONE},
                PERMISSION_REQUEST_CODE);
    }

    private void sendSMS() {
        String phone = etPhone.getText().toString();
        String message = etMessage.getText().toString();

        if (!phone.isEmpty() && !message.isEmpty()) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phone, null, message, null, null);
            Toast.makeText(this, "SMS Sent", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Enter phone and message", Toast.LENGTH_SHORT).show();
        }
    }

    private void makeCall() {
        String phone = etPhone.getText().toString();
        if (!phone.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + phone));

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {
                startActivity(intent);
            } else {
                Toast.makeText(this, "Call permission not granted", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Enter phone number", Toast.LENGTH_SHORT).show();
        }
    }

    private void showNotification() {
        String CHANNEL_ID = "communication_channel";

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Comm Channel", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Notification")
                .setContentText("This is a test notification!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        manager.notify(1, builder.build());
    }
}

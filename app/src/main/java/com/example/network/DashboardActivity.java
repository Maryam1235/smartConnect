package com.example.network;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        ImageView wifi = findViewById(R.id.wifi);
        ImageView bluetooth = findViewById(R.id.imgNews); // This is your Bluetooth icon

        // Click for WiFi
        wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(go);
            }
        });

        // Click for Bluetooth
        bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

                if (bluetoothAdapter == null) {
                    Toast.makeText(DashboardActivity.this, "Bluetooth is not supported on this device", Toast.LENGTH_LONG).show();
                } else {
                    if (!bluetoothAdapter.isEnabled()) {
                        // Ask user to enable Bluetooth
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
                    } else {
                        Toast.makeText(DashboardActivity.this, "Bluetooth is already enabled", Toast.LENGTH_SHORT).show();
                        // You can optionally navigate to a Bluetooth Activity
                        // startActivity(new Intent(getApplicationContext(), BluetoothActivity.class));
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Bluetooth Enabled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Bluetooth Enabling Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

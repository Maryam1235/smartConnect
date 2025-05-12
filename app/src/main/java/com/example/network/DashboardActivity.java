package com.example.network;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class DashboardActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private static final int REQUEST_LOCATION_PERMISSION = 2;
    private static final int REQUEST_BLUETOOTH_CONNECT_PERMISSION = 3;
    private BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        ImageView sms = findViewById(R.id.imgCalender);
        ImageView wifi = findViewById(R.id.wifi);
        ImageView bluetooth = findViewById(R.id.imgNews);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // WiFi click
        wifi.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), NetworkActivity.class)));

        // Bluetooth click (opens bottom sheet after permission and Bluetooth ON check)
        bluetooth.setOnClickListener(v -> {
            if (bluetoothAdapter == null) {
                Toast.makeText(this, "Bluetooth not supported on this device", Toast.LENGTH_LONG).show();
                return;
            }

            // Request Bluetooth Connect permission on Android 12+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT_PERMISSION);
                    return;
                }
            }

            // Check if Bluetooth is enabled
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
                return;
            }

            // Check and request location permission before scanning (still required for discovery)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            } else {
                showBluetoothBottomSheet();
            }
        });

        // SMS click
        sms.setOnClickListener(v -> startActivity(new Intent(this, Communication.class)));

        // Update Bluetooth icon based on Bluetooth status
        updateBluetoothStatus();
    }

    // Show Bluetooth Devices Bottom Sheet
    private void showBluetoothBottomSheet() {
        BluetoothDeviceBottomSheet bottomSheet = new BluetoothDeviceBottomSheet(this);
        bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
    }

    // Update Bluetooth icon based on whether Bluetooth is enabled or not
    private void updateBluetoothStatus() {
        ImageView bluetoothIcon = findViewById(R.id.imgNews);
        if (bluetoothAdapter == null) {
            bluetoothIcon.setImageResource(R.drawable.bluetooth_regular); // Bluetooth not supported
        } else if (bluetoothAdapter.isEnabled()) {
            bluetoothIcon.setImageResource(R.drawable.bluetooth_enabled); // Bluetooth is enabled
        } else {
            bluetoothIcon.setImageResource(R.drawable.bluetooth_regular); // Bluetooth is off
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Bluetooth Enabled", Toast.LENGTH_SHORT).show();
                updateBluetoothStatus(); // Update icon after enabling Bluetooth
                showBluetoothBottomSheet();  // Proceed to show devices after enabling Bluetooth
            } else {
                Toast.makeText(this, "Bluetooth enabling cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showBluetoothBottomSheet(); // Proceed with scanning if location permission granted
            } else {
                Toast.makeText(this, "Location permission is required for Bluetooth scanning.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_BLUETOOTH_CONNECT_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Bluetooth permission granted. Now click Bluetooth again.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Bluetooth connect permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

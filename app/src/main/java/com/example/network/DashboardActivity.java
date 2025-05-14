package com.example.network;

import android.Manifest;
<<<<<<< Updated upstream
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
=======
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
>>>>>>> Stashed changes
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class DashboardActivity extends AppCompatActivity {

<<<<<<< Updated upstream
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private static final int REQUEST_LOCATION_PERMISSION = 2;
    private static final int REQUEST_BLUETOOTH_CONNECT_PERMISSION = 3;
    private BluetoothAdapter bluetoothAdapter;
=======
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int GALLERY_REQUEST_CODE = 200;
    private static final int REQUEST_PERMISSION_CODE = 101;

    private ImageView camera, wifi, bluetooth;
    private Uri imageUri; // Store URI for camera photo
>>>>>>> Stashed changes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

<<<<<<< Updated upstream
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
=======
        checkAndRequestPermissions();

        camera = findViewById(R.id.imgDonation); // Camera ImageView
        wifi = findViewById(R.id.wifi);           // WiFi ImageView
        bluetooth = findViewById(R.id.imgNews);   // Bluetooth ImageView

        wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(go);
>>>>>>> Stashed changes
            }

<<<<<<< Updated upstream
            // Request Bluetooth Connect permission on Android 12+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT_PERMISSION);
                    return;
                }
=======
        bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DashboardActivity.this, "Bluetooth clicked", Toast.LENGTH_SHORT).show();
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageOptionsDialog();
>>>>>>> Stashed changes
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

    private void showImageOptionsDialog() {
        final CharSequence[] options = {"Take a Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
        builder.setTitle("Choose an option")
                .setItems(options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Take a Photo")) {
                            openCamera();
                        } else if (options[item].equals("Choose from Gallery")) {
                            openGallery();
                        } else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
        builder.show();
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

<<<<<<< Updated upstream
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
=======
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                Toast.makeText(this, "Photo captured and saved.", Toast.LENGTH_SHORT).show();
                imageUri = null; // optional cleanup
            } else if (requestCode == GALLERY_REQUEST_CODE) {
                Toast.makeText(this, "Image selected from gallery.", Toast.LENGTH_SHORT).show();
>>>>>>> Stashed changes
            }
        }
    }

    private void checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, REQUEST_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_CODE) {
            boolean granted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    granted = false;
                    break;
                }
            }


        }
    }
}

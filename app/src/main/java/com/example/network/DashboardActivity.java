package com.example.network;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class DashboardActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private static final int REQUEST_LOCATION_PERMISSION = 2;
    private static final int REQUEST_BLUETOOTH_CONNECT_PERMISSION = 3;
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int GALLERY_REQUEST_CODE = 200;
    private static final int REQUEST_PERMISSION_CODE = 101;

    private BluetoothAdapter bluetoothAdapter;
    private ImageView camera, wifi, bluetooth;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ImageView location= findViewById(R.id.location);
        ImageView sms = findViewById(R.id.imgCalender);

        camera = findViewById(R.id.imgDonation);
        wifi = findViewById(R.id.wifi);
        bluetooth = findViewById(R.id.imgNews);


        ImageView wifi = findViewById(R.id.wifi);
        ImageView bluetooth = findViewById(R.id.imgNews); // This is your Bluetooth icon


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // WiFi click
        wifi.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), NetworkActivity.class)));

        // Bluetooth click
        bluetooth.setOnClickListener(v -> {
            if (bluetoothAdapter == null) {
                Toast.makeText(this, "Bluetooth not supported on this device", Toast.LENGTH_LONG).show();
                return;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                            REQUEST_BLUETOOTH_CONNECT_PERMISSION);
                    return;
                }
            }

            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
                return;
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION_PERMISSION);
            } else {
                showBluetoothBottomSheet();
            }
        });


        // Camera click
        camera.setOnClickListener(v -> {
            checkAndRequestPermissions();
            showImageOptionsDialog();
        });

        // Click for Location
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, LocateActivity.class);
                startActivity(intent);
            }
        });

        // SMS click
        sms.setOnClickListener(v -> startActivity(new Intent(this, Communication.class)));

        // Update Bluetooth status icon
        updateBluetoothStatus();
    }

    private void updateBluetoothStatus() {
        if (bluetoothAdapter == null) {
            bluetooth.setImageResource(R.drawable.bluetooth_regular);
        } else if (bluetoothAdapter.isEnabled()) {
            bluetooth.setImageResource(R.drawable.bluetooth_enabled);
        } else {
            bluetooth.setImageResource(R.drawable.bluetooth_regular);
        }
    }

    private void showBluetoothBottomSheet() {
        BluetoothDeviceBottomSheet bottomSheet = new BluetoothDeviceBottomSheet(this);
        bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
    }

    private void showImageOptionsDialog() {
        final CharSequence[] options = {"Take a Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose an option")
                .setItems(options, (dialog, item) -> {
                    if (options[item].equals("Take a Photo")) {
                        openCamera();
                    } else if (options[item].equals("Choose from Gallery")) {
                        openGallery();
                    } else if (options[item].equals("Cancel")) {
                        dialog.dismiss();
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BLUETOOTH && resultCode == RESULT_OK) {
            Toast.makeText(this, "Bluetooth Enabled", Toast.LENGTH_SHORT).show();
            updateBluetoothStatus();
            showBluetoothBottomSheet();
        } else if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            Toast.makeText(this, "Photo captured and saved.", Toast.LENGTH_SHORT).show();
            imageUri = null;
        } else if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            Toast.makeText(this, "Image selected from gallery.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION_PERMISSION && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showBluetoothBottomSheet();
        } else if (requestCode == REQUEST_BLUETOOTH_CONNECT_PERMISSION && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Bluetooth permission granted. Now click Bluetooth again.", Toast.LENGTH_SHORT).show();
        } else if (requestCode == REQUEST_PERMISSION_CODE) {
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

package com.example.network;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

public class NetworkActivity extends AppCompatActivity {
    ImageView imageView;
    TextView networkStatus;
    Button openSettingsButton;
    String imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a7/React-icon.svg/512px-React-icon.svg.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);

        imageView = findViewById(R.id.imageView);
        networkStatus = findViewById(R.id.networkStatus);
        openSettingsButton = findViewById(R.id.openSettingsButton);

        if (isNetworkAvailable()) {
            loadImage();
        } else {
            imageView.setVisibility(View.GONE);
            networkStatus.setVisibility(View.VISIBLE);
            openSettingsButton.setVisibility(View.VISIBLE);

            openSettingsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Open mobile network settings
                    Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);

                    startActivity(intent);
                }
            });
        }
    }

    private void loadImage() {
        RequestQueue queue = Volley.newRequestQueue(this);

        ImageRequest imageRequest = new ImageRequest(imageUrl,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        imageView.setImageBitmap(response);
                        imageView.setVisibility(View.VISIBLE);
                    }
                },
                0, 0, null, null,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(NetworkActivity.this, "Failed to load image", Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(imageRequest);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}

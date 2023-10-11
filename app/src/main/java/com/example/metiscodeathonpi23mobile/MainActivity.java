package com.example.metiscodeathonpi23mobile;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements LocationUpdateListener {
    private Button btnStart;
    private TextView tvLocation;
    private boolean isCollecting = false;

    private LocationTracker tracker;
    private Compass compass;

    private TrackedPath trackedPath;

    public void onLocationUpdate(Location location) {
        TrackedPoint trackedPoint = new TrackedPoint();

        trackedPoint.longitude = location.getLongitude();
        trackedPoint.latitude = location.getLatitude();
        trackedPoint.azimuth = compass.azimuth;
        trackedPoint.direction = compass.direction;

        // TODO: create a new Location object and add it to TrackedPath.locationList
        String text = "(" + trackedPoint.longitude + ", " + trackedPoint.latitude + ") " + compass.azimuth + " : " + compass.direction;
        trackedPath.locationList.add(trackedPoint);
        tvLocation.setText(text);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tracker = new LocationTracker(this, this);
        compass = new Compass(this);

        setContentView(R.layout.activity_main);
        btnStart = findViewById(R.id.btnStart);
        tvLocation = findViewById(R.id.tvLocation);

        btnStart.setOnClickListener(view -> handleButtonClick());
    }

    @SuppressLint("SetTextI18n")
    private void handleButtonClick() {
        if (isCollecting) {
            isCollecting = false;
            btnStart.setText("Start");
            tracker.stop();
            compass.stop();
        } else {
            trackedPath = new TrackedPath();
            isCollecting = true;
            btnStart.setText("Stop");
            tracker.start();
            compass.start();
        }
    }

    private void postEndpoint(String url, TrackedPath trackedPath)
    {
        Gson gson = new Gson();
        String trackedPathString = gson.toJson(trackedPath);
        new Thread(new Runnable() {

            @Override
            public void run() {
                RequestBody postBody = new FormBody.Builder()
                        .add("TrackedPathString", trackedPathString)
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(postBody)
                        .build();
                OkHttpClient client = new OkHttpClient();
                Call call = client.newCall(request);

                Response response = null;
                try {
                    response = call.execute();
                    String serverResponse = response.body().string();
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            //display to serverResponse to UI
                        }
                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }).start();
    }

    private void getEndpoint(String url)
    {
        //get
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful())
                {
                    String responseString = response.body().string();
                    Gson gson = new Gson();
                    TrackedPath trackedPath = new TrackedPath();
                    trackedPath = gson.fromJson(responseString, TrackedPath.class);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            //display trackedPath to UI component
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }
        });

    }
}

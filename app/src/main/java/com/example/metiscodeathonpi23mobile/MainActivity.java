package com.example.metiscodeathonpi23mobile;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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
}

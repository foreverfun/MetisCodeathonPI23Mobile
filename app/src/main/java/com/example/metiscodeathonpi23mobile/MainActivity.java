package com.example.metiscodeathonpi23mobile;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements LocationUpdateListener, PictureTakerCallback {
    private Button btnStart;
    private Button btnTakePicture;
    private TextView tvLocation;
    private boolean isCollecting = false;

    private LocationTracker tracker;
    private Compass compass;
    private PictureTaker pictureTaker;

    private TrackedPath trackedPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tracker = new LocationTracker(this, this);
        compass = new Compass(this);
        pictureTaker = new PictureTaker(this, this);

        setContentView(R.layout.activity_main);
        btnStart = findViewById(R.id.btnStart);
        btnTakePicture = findViewById(R.id.btnTakePicture);
        tvLocation = findViewById(R.id.tvLocation);

        btnStart.setOnClickListener(view -> handleStartClick());

        btnTakePicture.setOnClickListener(view -> handleTakePictureClick());
    }

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

    public void onPictureTaken() {}

    @SuppressLint("SetTextI18n")
    private void handleStartClick() {

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

    @SuppressLint("SetTextI18n")
    private void handleTakePictureClick() {
        pictureTaker.takePicture();
    }
}

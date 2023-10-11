package com.example.metiscodeathonpi23mobile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalTime; // import the LocalTime class


public class MainActivity extends AppCompatActivity {
    private LocationManager locationManager;
    private Button btnStart;
    private TextView tvLocation;
    private boolean isCollecting = false;

    private Compass compass;

    class TrackedPoint {
        public double latitude;
        public double longitude;
        long elevation;
    }

    class TrackedPath {
        ArrayList<TrackedPoint> locationList = new ArrayList<TrackedPoint>();
        LocalDate localDate = LocalDate.now(); // Create a date object
        LocalTime localTime = LocalTime.now();
    }

    TrackedPath trackedPath = new TrackedPath();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnStart = findViewById(R.id.btnStart);
        tvLocation = findViewById(R.id.tvLocation);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        compass = new Compass(this);

        btnStart.setOnClickListener(view -> handleButtonClick());
    }

    @SuppressLint("SetTextI18n")
    private void handleButtonClick() {
        if (isCollecting) {
            isCollecting = false;
            btnStart.setText("Start");
            locationManager.removeUpdates(locationListener);
            compass.stop();
        } else {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return;
            }

            isCollecting = true;
            btnStart.setText("Stop");
            // TODO: update based on distance traveled instead of based on time passed (time works nicely for dev/debugging)
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, locationListener);
            compass.start();
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            TrackedPoint trackedPoint = new TrackedPoint();
            trackedPoint.longitude = location.getLongitude();
            trackedPoint.latitude = location.getLatitude();

            // TODO: create a new Location object and add it to TrackedPath.locationList
            String text = "(" + trackedPoint.longitude + ", " + trackedPoint.latitude + ") " + compass.azimuthRads + " : " + compass.direction;
            trackedPath.locationList.add(trackedPoint);
            tvLocation.setText(text);
        }
    };
}

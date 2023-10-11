package com.example.metiscodeathonpi23mobile;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.sql.Time;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalTime; // import the LocalTime class
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private LocationManager locationManager;
    private Button btnStart;
    private TextView tvLocation;
    private boolean isCollecting = false;

    // TODO: locationList should be an ArrayList of Locations
    //       we need a Location class for this with latitude, longitude, and sensor fields (e.g. elevation)
    //       locationList should be in a class called TrackedPath, which would also have the date/time stamp
    //       of when it was created, and we need to be able to serialize it to JSON to send to AWS
    //private ArrayList<String> locationList;

    class Location {
        public double latitude;
        public double longitude;
        long elevation;
    }

    class TrackedPath {
        ArratList<Location> locationList = new ArrayList<Location>();
        LocalDate localDate = LocalDate.now(); // Create a date object
        LocalTime localTime = LocalTime.now();
    }

    Location location = new Location();
    TrackedPath trackedPath = new TrackedPath();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnStart = findViewById(R.id.btnStart);
        tvLocation = findViewById(R.id.tvLocation);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //locationList = new ArrayList<>();

        btnStart.setOnClickListener(view -> handleButtonClick());
    }

    private void handleButtonClick() {
        if (isCollecting) {
            isCollecting = false;
            btnStart.setText("Start");
            locationManager.removeUpdates(locationListener);
        } else {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return;
            }

            isCollecting = true;
            btnStart.setText("Stop");
            // TODO: update based on distance traveled instead of every 5 seconds (5 seconds works nicely for dev/debugging)
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            location.longitude = location.getLongitude();
            location.latitude = location.getLatitude();

            // TODO: create a new Location object and add it to TrackedPath.locationList
            String text = "(" + location.longitude + ", " + location.latitude + ")";
            trackedPath.locationList.add(text);
            tvLocation.setText(text);
        }
    };
}

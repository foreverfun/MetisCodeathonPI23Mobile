package com.example.metiscodeathonpi23mobile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import android.location.Location;
import android.graphics.Color;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements LocationUpdateListener, PictureTakerCallback, OnMapReadyCallback {
    private static double offsetLat = 0;
    private static double offsetLon = 0;
    private Button btnStart;
    private Button btnTakePicture;
    private TextView tvLocation;
    private boolean isCollecting = false;

    private LocationTracker tracker;
    private Compass compass;
    private PictureTaker pictureTaker;

    private TrackedPath trackedPath;
    private Polyline polyline;

    private GoogleMap myMap;

    String apiUrl = "https://hezidt069i.execute-api.us-east-2.amazonaws.com/Develop/walked-paths";

    private RestClient restClient = new RestClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tracker = new LocationTracker(this, this);
        compass = new Compass(this);
        pictureTaker = new PictureTaker(this, this);

        trackedPath = new TrackedPath();

        setContentView(R.layout.activity_main);
        btnStart = findViewById(R.id.btnStart);
        btnTakePicture = findViewById(R.id.btnTakePicture);
        tvLocation = findViewById(R.id.tvLocation);

        btnStart.setOnClickListener(view -> handleStartClick());
        btnTakePicture.setOnClickListener(view -> handleTakePictureClick());

        //Testing
        Button testingBtn = findViewById(R.id.btnTesting);
        testingBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TestingActivity.class);
                startActivity(intent);
            }
        });
        
        // setup the GoogleMap
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView_walkedPath);

        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.mapView_walkedPath, mapFragment);
            fragmentTransaction.commit();
        }

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;
        myMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        pictureTaker.onActivityResult(requestCode, resultCode, data);
    }

    private void AddTrackedPoint(TrackedPoint trackedPoint)
    {
        String text = "(" + trackedPoint.longitude + ", " + trackedPoint.latitude + ") " + compass.azimuth + " : " + compass.direction;
        trackedPath.locationList.add(trackedPoint);
        tvLocation.setText(text);

        //To track current path
        LatLng current = new LatLng(trackedPoint.latitude, trackedPoint.longitude);
        myMap.addMarker(new MarkerOptions().position(current).title(trackedPath.localTime.toString()));
        if (trackedPath.locationList.size() > 1) {
            TrackedPoint lastTracked = trackedPath.locationList.get(trackedPath.locationList.size() - 2);

            LatLng last = new LatLng(lastTracked.latitude, lastTracked.longitude);
            if(polyline == null) {
                PolylineOptions polylineOptions = new PolylineOptions()
                        .add(last, current)
                        .width(5f)
                        .color(Color.RED);
                polyline = myMap.addPolyline(polylineOptions);
            } else {
                List<LatLng> points = polyline.getPoints();
                points.add(current);
                polyline.setPoints(points);
            }
        }

        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 20));
    }

    public void onLocationUpdate(Location location) {
        TrackedPoint trackedPoint = new TrackedPoint();

        trackedPoint.longitude = location.getLongitude() + offsetLon;
        trackedPoint.latitude = location.getLatitude() + offsetLat;
        trackedPoint.azimuth = compass.azimuth;
        trackedPoint.direction = compass.direction;

        AddTrackedPoint(trackedPoint);
    }

    public void onPictureTaken(String base64image) {

        TrackedPoint trackedPoint = new TrackedPoint();

        Location location = tracker.getLocation();

        trackedPoint.longitude = location.getLongitude() + offsetLon;
        trackedPoint.latitude = location.getLatitude() + offsetLat;
        trackedPoint.azimuth = compass.azimuth;
        trackedPoint.direction = compass.direction;
        trackedPoint.base64Image = base64image;

        Gson gson = new Gson();
        String trackedPointString = gson.toJson(trackedPoint);
        Log.d("MainActivity", "onPictureTaken - trackedPoint: " + trackedPointString);

        AddTrackedPoint(trackedPoint);
    }

    @SuppressLint("SetTextI18n")
    private void handleStartClick() {

        if (isCollecting) {
            isCollecting = false;
            btnStart.setText("Start");
            tracker.stop();
            compass.stop();
            MakePostEndpointRequest();
        } else {
            myMap.clear();
            polyline = null;
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

    @SuppressLint("SetTextI18n")
    private void MakePostEndpointRequest()
    {
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("body", gson.toJson(trackedPath));
        String jsonBody = gson.toJson(jsonObject);
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        tvLocation.setText("Request failed: " + e.getMessage());
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseBody = response.body().string();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            tvLocation.setText(responseBody);
                        }
                    });
                } else {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            tvLocation.setText("Unsuccessful response: " + response.code());
                        }
                    });
                }
            }
        };

        restClient.makePostRequest(apiUrl, jsonBody, callback);
    }

}

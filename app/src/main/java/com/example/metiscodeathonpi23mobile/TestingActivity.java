package com.example.metiscodeathonpi23mobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
public class TestingActivity extends AppCompatActivity {

    private Button btnGetTrackedPath, btnPostTrackedPath;
    private TextView dResponseString;

    String apiUrl = "https://hezidt069i.execute-api.us-east-2.amazonaws.com/Develop/walked-paths";

    private RestClient restClient = new RestClient();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);

        btnGetTrackedPath = findViewById(R.id.btnGetTrackedPath);
        btnPostTrackedPath = findViewById(R.id.btnPostTrackedPath);
        dResponseString = findViewById(R.id.response_string);

        //Home
        Button homeBtn = findViewById(R.id.btnHome);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestingActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnGetTrackedPath.setOnClickListener(view -> handleGetTrackedPathClick());
        btnPostTrackedPath.setOnClickListener(view -> handlePostTrackedPathClick());
    }

    @SuppressLint("SetTextI18n")
    private void handleGetTrackedPathClick() {
        restClient.makeGetRequest(apiUrl, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    runOnUiThread(() -> dResponseString.setText(responseBody));
                    // Process the response data
                } else {
                    // Handle the unsuccessful response
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void handlePostTrackedPathClick() {
        String jsonBody = "{\"body\": \"{\\\"localDate\\\":{},\\\"localTime\\\":{},\\\"locationList\\\":[{\\\"azimuth\\\":268.7975158691406,\\\"direction\\\":\\\"W\\\",\\\"latitude\\\":38.99842395,\\\"longitude\\\":-96.79225892999999},{\\\"azimuth\\\":268.8315124511719,\\\"direction\\\":\\\"W\\\",\\\"latitude\\\":38.99861302,\\\"longitude\\\":-96.79221908},{\\\"azimuth\\\":269.89239501953125,\\\"direction\\\":\\\"W\\\",\\\"latitude\\\":38.99849173,\\\"longitude\\\":-96.79224174999999},{\\\"azimuth\\\":259.8670349121094,\\\"direction\\\":\\\"W\\\",\\\"latitude\\\":38.99844019,\\\"longitude\\\":-96.79224289999999},{\\\"azimuth\\\":253.1647186279297,\\\"direction\\\":\\\"W\\\",\\\"latitude\\\":38.99842425,\\\"longitude\\\":-96.79224814},{\\\"azimuth\\\":264.7764587402344,\\\"direction\\\":\\\"W\\\",\\\"latitude\\\":38.99842918,\\\"longitude\\\":-96.79226236},{\\\"azimuth\\\":260.4714660644531,\\\"direction\\\":\\\"W\\\",\\\"latitude\\\":38.99843002,\\\"longitude\\\":-96.79226887},{\\\"azimuth\\\":263.6086730957031,\\\"direction\\\":\\\"W\\\",\\\"latitude\\\":38.99842383,\\\"longitude\\\":-96.79226879999999},{\\\"azimuth\\\":264.70550537109375,\\\"direction\\\":\\\"W\\\",\\\"latitude\\\":38.99842113,\\\"longitude\\\":-96.79227164},{\\\"azimuth\\\":262.26727294921875,\\\"direction\\\":\\\"W\\\",\\\"latitude\\\":38.99841762,\\\"longitude\\\":-96.79227309999999},{\\\"azimuth\\\":350.2537536621094,\\\"direction\\\":\\\"N\\\",\\\"latitude\\\":38.99841814,\\\"longitude\\\":-96.79226795999999},{\\\"azimuth\\\":199.9546661376953,\\\"direction\\\":\\\"S\\\",\\\"latitude\\\":38.9984177,\\\"longitude\\\":-96.79226763999999},{\\\"azimuth\\\":210.77056884765625,\\\"direction\\\":\\\"SW\\\",\\\"latitude\\\":38.99841505,\\\"longitude\\\":-96.79226670999999},{\\\"azimuth\\\":252.42404174804688,\\\"direction\\\":\\\"W\\\",\\\"latitude\\\":38.99841822,\\\"longitude\\\":-96.79225998},{\\\"azimuth\\\":249.75404357910156,\\\"direction\\\":\\\"W\\\",\\\"latitude\\\":38.99842201,\\\"longitude\\\":-96.79225894999999},{\\\"azimuth\\\":256.9810485839844,\\\"direction\\\":\\\"W\\\",\\\"latitude\\\":38.9984212,\\\"longitude\\\":-96.79225538},{\\\"azimuth\\\":251.7278289794922,\\\"direction\\\":\\\"W\\\",\\\"latitude\\\":38.99842122,\\\"longitude\\\":-96.79225027999999},{\\\"azimuth\\\":250.96189880371094,\\\"direction\\\":\\\"W\\\",\\\"latitude\\\":38.99842023,\\\"longitude\\\":-96.79224599}]}\"}";

        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        dResponseString.setText("Request failed: " + e.getMessage());
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
                            dResponseString.setText(responseBody);
                        }
                    });
                } else {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            dResponseString.setText("Unsuccessful response: " + response.code());
                        }
                    });
                }
            }
        };

        restClient.makePostRequest(apiUrl, jsonBody, callback);
    }
}
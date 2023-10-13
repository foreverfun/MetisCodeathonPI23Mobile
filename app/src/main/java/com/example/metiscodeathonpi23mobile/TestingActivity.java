package com.example.metiscodeathonpi23mobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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

    private Button btnGetTrackedPath;
    private TextView dResponseString;

    private RestClient restClient = new RestClient();
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);

        btnGetTrackedPath = findViewById(R.id.btnGetTrackedPath);
        dResponseString = findViewById(R.id.response_string);

        //Home
        Button homeBtn = findViewById(R.id.btnHome);
        homeBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestingActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnGetTrackedPath.setOnClickListener(view -> handleGetTrackedPathClick());
    }

    @SuppressLint("SetTextI18n")
    private void handleGetTrackedPathClick() {
        String apiUrl = "https://hezidt069i.execute-api.us-east-2.amazonaws.com/Develop/walked-paths";
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
}
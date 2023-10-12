package com.example.metiscodeathonpi23mobile;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.io.IOException;

public class RestClient {
    private final OkHttpClient client = new OkHttpClient();
    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public void makeGetRequest(String url, final Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onFailure(call, new IOException("Unexpected response: " + response));
                } else {
                    callback.onResponse(call, response);
                }
            }
        });
    }

    public Response makePostRequest(String url, String jsonBody) throws IOException {
        RequestBody requestBody = RequestBody.create(jsonBody, JSON);

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .build();

        Call call = client.newCall(request);

        try {
            Response response = call.execute();
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception
            return null; // or throw a custom exception
        }
    }
}
//How to use RestClient
//    String apiUrl = "https://hezidt069i.execute-api.us-east-2.amazonaws.com/Develop/walked-paths";
//    restClient.makeGetRequest(apiUrl, new Callback() {
//        @Override
//        public void onFailure(Call call, IOException e) {
//            // Handle the failure
//        }
//
//        @Override
//        public void onResponse(Call call, Response response) throws IOException {
//            if (response.isSuccessful()) {
//                String responseBody = response.body().string();
//                // Process the response data
//            } else {
//                // Handle the unsuccessful response
//            }
//        }
//    });

//Serailize and Deserialize
// GET
//    Gson gson = new Gson();
//    Type listType = new TypeToken<ArrayList<TrackedPath>>(){}.getType();
//    ArrayList<TrackedPath> trackedPaths = gson.fromJson(responseString, listType);
// POST
//    Gson gson = new Gson();
//    String trackedPathString = gson.toJson(trackedPath);

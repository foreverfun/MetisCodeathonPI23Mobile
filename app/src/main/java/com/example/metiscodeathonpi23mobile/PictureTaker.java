package com.example.metiscodeathonpi23mobile;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PictureTaker {
    public static final int REQUEST_IMAGE_CAPTURE = 1001;
    public static final int REQUEST_CAMERA_STORAGE_PERMISSION = 1002;
    private Activity activity;
    private PictureTakerCallback callback;
    private Uri photoURI;

    public PictureTaker(Activity activity, PictureTakerCallback callback) {
        this.activity = activity;
        this.callback = callback;
    }

    public void takePicture() {
        Log.d("METIS Codeathon", "takePicture");
        if (checkPermissions()) {
            startCamera();
        } else {
            requestPermissions();
        }
    }

    private boolean checkPermissions() {
        int cameraPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        int writeExternalStoragePermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);


        Log.d("METIS Codeathon", "cameraPermission " + cameraPermission);
        Log.d("METIS Codeathon", "writeExternalStoragePermission " + writeExternalStoragePermission);

        return cameraPermission == PackageManager.PERMISSION_GRANTED && writeExternalStoragePermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        Log.d("METIS Codeathon", "requestPermissions");
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_STORAGE_PERMISSION);
    }

    private void startCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                // Handle error
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(activity,
                        "com.example.metiscodeathonpi23mobile",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name using a GUID
        String imageFileName = UUID.randomUUID().toString();
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        // Current path is used to pass it to the callback to let the caller activity know where the file is located.
        return image;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d("METIS Codeathon", "grantResults.length: " + grantResults.length);
        if (requestCode == REQUEST_CAMERA_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Permissions were granted, start the camera
                startCamera();
            } else {
                // Permissions were denied. Handle appropriately, possibly inform the user they can't proceed without permission.
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            callback.onPictureTaken();
        }
    }
}



//package com.example.metiscodeathonpi23mobile;
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.net.Uri;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.util.Log;
//
//import java.io.File;
//import java.util.UUID;
//
//public class PictureTaker {
//    private static final int REQUEST_IMAGE_CAPTURE = 1;
//    private Activity activity;
//    private PictureInfo lastPictureInfo;
//    private LocationTracker tracker;
//
//    public PictureTaker(Activity activity, LocationTracker tracker) {
//        this.activity = activity;
//        this.tracker = tracker;
//    }
//
//    private File createImageFile() {
//        String imageFileName = UUID.randomUUID().toString();
//        File storageDir = activity.getExternalFilesDir(null);
//        File image = new File(storageDir, imageFileName + ".jpg");
//        if (image.exists()) {
//            image.delete();
//        }
//        try {
//            image.createNewFile();
//
//            lastPictureInfo = new PictureInfo();
//            lastPictureInfo.guid = imageFileName;
//            lastPictureInfo.picturePath = image.getAbsolutePath();
//            lastPictureInfo.timeTaken = System.currentTimeMillis();
//
//            Location location = tracker.getLocation();
//            if(location != null) {
//                lastPictureInfo.latitude = location.getLatitude();
//                lastPictureInfo.longitude = location.getLongitude();
//            }
//
//            return image;
//        } catch (Exception ex) {
//            Log.e("PictureTaker", "Error creating image file", ex);
//            return null;
//        }
//    }
//
//    public void takePicture() {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
//            File photoFile = createImageFile();
//            if (photoFile != null) {
//                Uri photoURI = Uri.fromFile(photoFile);
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//            }
//        }
//    }
//
//    public PictureInfo getLastPictureInfo() {
//        return lastPictureInfo;
//    }
//}

package com.example.metiscodeathonpi23mobile;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.UUID;

public class PictureTaker_2 {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int REQUEST_CAMERA_PERMISSION = 2;
    public static final int REQUEST_CAMERA_STORAGE_PERMISSION = 3;
    private Activity activity;
    private PictureInfo lastPictureInfo;
    private LocationTracker tracker;
    private PictureTakerCallback callback;

    public PictureTaker_2(Activity activity, LocationTracker tracker, PictureTakerCallback callback) {
        this.activity = activity;
        this.tracker = tracker;
        this.callback = callback;
    }

    private File createImageFile() {
        String imageFileName = UUID.randomUUID().toString();
        File storageDir = activity.getExternalFilesDir(null);
        File image = new File(storageDir, imageFileName + ".jpg");
        if (image.exists()) {
            image.delete();
        }
        try {
            image.createNewFile();

            lastPictureInfo = new PictureInfo();
            lastPictureInfo.guid = imageFileName;
            lastPictureInfo.picturePath = image.getAbsolutePath();
            lastPictureInfo.timeTaken = System.currentTimeMillis();

//            Location location = tracker.getLocation();
//            if(location != null) {
//                lastPictureInfo.latitude = location.getLatitude();
//                lastPictureInfo.longitude = location.getLongitude();
//            }

            return image;
        } catch (Exception ex) {
            Log.e("PictureTaker", "Error creating image file", ex);
            return null;
        }
    }

    private boolean checkPermissions() {
        int cameraPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        int writeExternalStoragePermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return
            cameraPermission == PackageManager.PERMISSION_GRANTED
         && writeExternalStoragePermission == PackageManager.PERMISSION_GRANTED;
    }

    public void takePicture() {
        // Check if the camera permission has been granted
        if (checkPermissions()) {
            // Permission is granted, start the camera
            startCamera();
        } else {
            // Permission is not granted, request the permission

            requestPermissions();
            //ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }


    private void requestPermissions() {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_STORAGE_PERMISSION);
    }

    // public void takePicture() {
    //     // Check if the camera permission has been granted
    //     if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
    //         ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ) {
    //         // Permission is granted, start the camera
    //         startCamera();
    //     } else {
    //         // Permission is not granted, request the permission
    //         ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
    //     }
    // }

    public void startCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

            // File photoFile = createImageFile();
            // if (photoFile != null) {
            //     Uri photoURI = Uri.fromFile(photoFile);
            //     takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            //     activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            // }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted, start the camera
                startCamera();
            } else {
                // Permission was denied. Handle appropriately, possibly informing the user they can't proceed without permission.
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            callback.onPictureTaken();
        }
    }
}

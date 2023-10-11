package com.example.metiscodeathonpi23mobile;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import java.io.File;
import java.util.UUID;

public class PictureTaker {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private Activity activity;
    private PictureInfo lastPictureInfo;

    public PictureTaker(Activity activity) {
        this.activity = activity;
    }

    public void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile != null) {
                Uri photoURI = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
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
            // Record info for later retrieval
            lastPictureInfo = new PictureInfo();
            lastPictureInfo.guid = imageFileName;
            lastPictureInfo.picturePath = image.getAbsolutePath();
            lastPictureInfo.timeTaken = System.currentTimeMillis();
            lastPictureInfo.latitude = getLatitude();
            lastPictureInfo.longitude = getLongitude();
            return image;
        } catch (Exception ex) {
            Log.e("PictureTaker", "Error creating image file", ex);
            return null;
        }
    }

    private double getLatitude() {
        // TODO Add actual logic to retrieve latitude
        return 0.0;
    }

    private double getLongitude() {
        // TODO Add actual logic to retrieve longitude
        return 0.0;
    }

    public PictureInfo getLastPictureInfo() {
        return lastPictureInfo;
    }
}

package com.example.metiscodeathonpi23mobile;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.OutputStream;

public class PictureTaker {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private final Activity activity;
    private final PictureTakerCallback callback;
    private Uri imageUri;

    public PictureTaker(Activity activity, PictureTakerCallback callback) {
        this.activity = activity;
        this.callback = callback;
    }

    public void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {

            // Generate the URI where the photo should go
            imageUri = createImageUri();

            // Continue only if the File was successfully created
            if (imageUri != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private Uri createImageUri() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "img_" + System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

        return activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Log.d("PictureTaker", "Image captured and saved to: " + imageUri.toString());
            callback.onPictureTaken(imageUri.toString());
        } else {
            Log.d("PictureTaker", "Image capture failed or cancelled");
            // Delete the empty image file if the user cancelled the camera activity.
            if (imageUri != null) {
                activity.getContentResolver().delete(imageUri, null, null);
            }
        }
    }
}

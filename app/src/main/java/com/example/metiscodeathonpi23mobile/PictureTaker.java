package com.example.metiscodeathonpi23mobile;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;

public class PictureTaker {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int targetImageSize = 1024;
    private final Activity activity;
    private final PictureTakerCallback callback;
    private Uri imageUri;

    public PictureTaker(Activity activity, PictureTakerCallback callback) {
        this.activity = activity;
        this.callback = callback;
    }

    private static final int REQUEST_CAMERA_PERMISSION = 123;

    private boolean requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION
            );

            return false;
        } else {
            return true;
        }
    }

    public void takePicture() {
        if (!requestCameraPermission()) {
            return;
        }

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

            ImageDecoder.Source source = ImageDecoder.createSource(activity.getContentResolver(), imageUri);
            try {
                // ImageUtils.resizeImage isn't working with the Uri created from taking a picture
                // so do it this way
                Bitmap originalBitmap = ImageDecoder.decodeBitmap(source);
                int originalWidth = originalBitmap.getWidth();
                int originalHeight = originalBitmap.getHeight();
                float aspectRatio = (float) originalWidth / (float) originalHeight;
                // Set the target dimensions while maintaining the original aspect ratio.
                int targetWidth = targetImageSize;
                int targetHeight = Math.round(targetWidth / aspectRatio);

                if (targetHeight > targetWidth) {
                    targetHeight = targetImageSize;
                    targetWidth = Math.round(targetHeight * aspectRatio);
                }

                Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, false);
                String base64image = ImageUtils.bitmapToBase64(resizedBitmap);
                callback.onPictureTaken(base64image);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Log.d("PictureTaker", "Image capture failed or cancelled");
            // Delete the empty image file if the user cancelled the camera activity.
            if (imageUri != null) {
                activity.getContentResolver().delete(imageUri, null, null);
            }
        }
    }
}

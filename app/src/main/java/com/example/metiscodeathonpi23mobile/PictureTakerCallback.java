package com.example.metiscodeathonpi23mobile;

import android.content.Intent;
public interface PictureTakerCallback {
    void onPictureTaken(String base64Image);
}
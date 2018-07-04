package com.example.vinay.camtest;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.support.v4.app.ActivityCompat;
import android.view.TextureView;
import android.widget.TextView;

/**
 * Created by vinay on 7/3/18.
 */

public class MyTextureViewSurfaceListener implements TextureView.SurfaceTextureListener {

    MyCamera mCamera;
    MyTextureViewSurfaceListener(MyCamera camera) { mCamera = camera; }

    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mCamera.openCamera();
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        mCamera.configureTransform(width, height);
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return true;
    }

}

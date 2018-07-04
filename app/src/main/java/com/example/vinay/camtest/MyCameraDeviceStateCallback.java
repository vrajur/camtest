package com.example.vinay.camtest;

import android.hardware.camera2.CameraDevice;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Created by vinay on 7/3/18.
 */

public class MyCameraDeviceStateCallback extends CameraDevice.StateCallback {

    MyCamera mCamera;
    MyCameraDeviceStateCallback(MyCamera camera) { mCamera = camera; }

    @Override
    public void onOpened(@NonNull CameraDevice camera) {
        Log.d("CameraDevice", "Camera Opened!");
        mCamera.cameraDevice = camera;
        mCamera.createCameraPreview();
    }

    @Override
    public void onDisconnected(@NonNull CameraDevice camera) {
        mCamera.cameraDevice.close();
    }

    @Override
    public void onError(@NonNull CameraDevice camera, int error) {
        mCamera.cameraDevice.close();
        mCamera.cameraDevice = null;
    }
}

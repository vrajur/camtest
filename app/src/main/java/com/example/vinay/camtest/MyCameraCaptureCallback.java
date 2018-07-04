package com.example.vinay.camtest;

import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by vinay on 7/3/18.
 */

public class MyCameraCaptureCallback extends CameraCaptureSession.CaptureCallback {

    MainActivity mActivity;

    MyCameraCaptureCallback(MainActivity activity) { mActivity = activity; }

    @Override
    public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
        super.onCaptureCompleted(session, request, result);
//        Toast.makeText(mActivity.getApplicationContext(), "Capture Completed", Toast.LENGTH_SHORT).show();
//        mActivity.createCameraPreview();

        if (result != null) {
//            Log.d("CameraCapture", Long.toString(result.getFrameNumber()));
        }

    }
}

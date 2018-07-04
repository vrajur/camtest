package com.example.vinay.camtest;

import android.app.Activity;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.stream.Stream;

public class MainActivity extends AppCompatActivity {

//    CameraManager cameraManager;
//    String cameraId;
//    CameraDevice cameraDevice;
//    CaptureRequest.Builder captureRequestBuilder;
//    CameraCaptureSession cameraCaptureSession;
//
//    HandlerThread mBackgroundThread;
//    Handler mBackgroundHandler;
//
//    Size imageDimension;
//    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
//
//    static {
//        ORIENTATIONS.append(Surface.ROTATION_0, 0);
//        ORIENTATIONS.append(Surface.ROTATION_90, 90);
//        ORIENTATIONS.append(Surface.ROTATION_180, 270);
//        ORIENTATIONS.append(Surface.ROTATION_270, 180);
//    }

    MyCamera camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        camera = new MyCamera(this);
        camera.begin();
    }


    @Override
    public void onPause() {
        super.onPause();
        camera.end();
    }
}

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
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.widget.Toast;

import java.util.Arrays;

/**
 * Created by vinay on 7/3/18.
 */

public class MyCamera {

    MainActivity mainActivity;
    MyCamera(MainActivity activity) { mainActivity = activity; }

    CameraManager cameraManager;
    String cameraId;
    CameraDevice cameraDevice;
    CaptureRequest.Builder captureRequestBuilder;
    CameraCaptureSession cameraCaptureSession;

    HandlerThread mBackgroundThread;
    Handler mBackgroundHandler;

    Size imageDimension;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 0);
        ORIENTATIONS.append(Surface.ROTATION_90, 90);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }


    public void begin() {

        getCameraManger();
        getCameraId(mainActivity);
        startBackgroundThread();
        TextureView textureView = (TextureView) mainActivity.findViewById(R.id.textureView);
        textureView.setSurfaceTextureListener(new MyTextureViewSurfaceListener(this));

    }

    public void end() {
        closeCamera();
        stopBackgroundThread();
    }

    private void getCameraManger() {
        // Get Camera Manager:
        cameraManager = (CameraManager) mainActivity.getSystemService(Context.CAMERA_SERVICE);
    }

    private void getCameraId(Activity activity) {
        // Get Camera ID:
        try {
            for (String camId : cameraManager.getCameraIdList()) {
                try {
                    Log.d("CameraIDs", "Camera ID: " + camId);
                    CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(camId);
                    if (characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {
                        cameraId = camId;
                        Toast.makeText(mainActivity, "Assigned to Rear-Facing Camera (ID: " + cameraId + " )", Toast.LENGTH_SHORT).show();
                    }
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void openCamera() {
        if (!PermissionsChecker.checkCameraPermissions(mainActivity)) {
            PermissionsChecker.requestCameraPermissions(mainActivity);
            if (!PermissionsChecker.checkCameraPermissions(mainActivity)) {
                Toast.makeText(mainActivity, "Camera Permission Required (Not Enabled)", Toast.LENGTH_SHORT).show();
            }
        }
        try {
            StreamConfigurationMap map = (cameraManager.getCameraCharacteristics(cameraId).get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP));
            assert map != null;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
            TextureView textureView = (TextureView) mainActivity.findViewById(R.id.textureView);
            configureTransform(textureView.getWidth(), textureView.getHeight());
            cameraManager.openCamera(cameraId, new MyCameraDeviceStateCallback(this), mBackgroundHandler);
            Log.d("Camera", "Requested Camera ID: " + cameraId);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void closeCamera() {
        if (null != cameraDevice) {
            cameraDevice.close();
            cameraDevice = null;
        }
    }

    public void createCameraPreview() {
        TextureView textureView = (TextureView) mainActivity.findViewById(R.id.textureView);
        assert textureView != null;
        SurfaceTexture texture = textureView.getSurfaceTexture();
        assert texture != null;
        texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
        Surface surface = new Surface(texture);
        try {
            captureRequestBuilder = cameraDevice.createCaptureRequest(cameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    if (null == cameraDevice) {
                        return;
                    }
                    cameraCaptureSession = session;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    Toast.makeText(mainActivity, "Camera Configuration Failed", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void configureTransform(int width, int height) {
        Activity activity = mainActivity;
        TextureView textureView = (TextureView) activity.findViewById(R.id.textureView);
        if (null == activity || null == textureView || null == imageDimension) {
            return;
        }

        int rotation = mainActivity.getWindowManager().getDefaultDisplay().getRotation();
        Log.d("SensorOrientation", "Sensor Orientation " + rotation);
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, width, height);
        RectF bufferRect = new RectF(0, 0, textureView.getHeight(), textureView.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();

        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
//            bufferRect.set(0, 0, textureView.getHeight(), textureView.getWidth());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) height / textureView.getHeight(),
                    (float) width / textureView.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);

        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        textureView.setTransform(matrix);
        Log.d("CameraOrientation", "CenterX: " + centerX + "\t CenterY: " + centerY);
    }

    public void updatePreview() {
        if (null == cameraDevice) {
            Log.d("Camera Error", "updatePreview error");
            return;
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), new MyCameraCaptureCallback(mainActivity), mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

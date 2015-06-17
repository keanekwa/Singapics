package com.example.user.singapics;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;


public class CameraActivity extends ActionBarActivity {

    private Camera camera;
    private SurfaceView mSurfaceView;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#e74c3c")));
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (camera == null) {
            try {
                camera = Camera.open();
                //photoButton.setEnabled(true);
            } catch (Exception e) {
                Toast.makeText(CameraActivity.this, "No camera detected",
                        Toast.LENGTH_LONG).show();
            }
        }

        //makes the camera view square
        mSurfaceView = (SurfaceView)findViewById(R.id.camera_surface_view);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int height = 0;
        int width = metrics.widthPixels;
        height = (width/4) * 3;
        mSurfaceView.getLayoutParams().height = height;
        mSurfaceView.getLayoutParams().width = width;

        //set shutter button
        mImageView = (ImageView)findViewById(R.id.camera_photo_button);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (camera == null) return;
                camera.takePicture(new Camera.ShutterCallback() {
                    @Override
                    public void onShutter() {
                        // nothing to do
                    }

                }, null, new Camera.PictureCallback() {

                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        Toast.makeText(CameraActivity.this, "Picture captured",
                                Toast.LENGTH_LONG).show();
                        camera.stopPreview();
                        camera.release();
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("DATA", data);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }

                });
            }
        });

        SurfaceHolder holder = mSurfaceView.getHolder();
        holder.addCallback(new Callback() {

            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (camera != null) {
                        camera.setDisplayOrientation(0);
                        camera.setPreviewDisplay(holder);
                        camera.startPreview();
                    }
                } catch (IOException e) {
                    Toast.makeText(CameraActivity.this, "Picture captured",
                            Toast.LENGTH_LONG).show();
                }
            }

            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {
                // nothing to do here
            }

            public void surfaceDestroyed(SurfaceHolder holder) {
                // nothing here
            }

        });
    }
}

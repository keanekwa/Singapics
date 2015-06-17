package com.example.user.singapics;

import android.content.Intent;
import android.hardware.Camera;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


public class CameraActivity extends ActionBarActivity {

    private Camera camera;
    private SurfaceView mSurfaceView;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

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
        int value = 0;
        if(metrics.widthPixels < metrics.heightPixels)value = metrics.widthPixels;
        else value= metrics.heightPixels;
        mSurfaceView.getLayoutParams().height = value;
        mSurfaceView.getLayoutParams().width = value;

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
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("DATA", data);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }

                });
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_camera, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

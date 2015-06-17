package com.example.user.singapics;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;


public class PostNewActivity extends ActionBarActivity {

    private EditText mTitleEditText;
    private EditText mCaptionEditText;
    private Spinner mCategorySpinner;
    private Button mPostButton;
    private ProgressBar mLoading;
    private ParseFile scaledPhotoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_new);
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#e74c3c")));
        actionBar.setDisplayHomeAsUpEnabled(true);

        mTitleEditText = (EditText)findViewById(R.id.titleEditText);
        mCaptionEditText = (EditText)findViewById(R.id.captionEditText);
        mCategorySpinner = (Spinner)findViewById(R.id.categorySpinner);
        mPostButton = (Button)findViewById(R.id.finalizeButton);
        mLoading = (ProgressBar)findViewById(R.id.loadingProgressBar);

        mPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(String.valueOf(mCategorySpinner.getSelectedItem()).equals("Category")){
                    Toast.makeText(PostNewActivity.this, "Choose a category", Toast.LENGTH_LONG);
                }
                else {
                    mLoading.setVisibility(View.VISIBLE);
                    mPostButton.setVisibility(View.INVISIBLE);
                    final ParseObject newPost = new ParseObject("allPostings");
                    newPost.put("createdBy", ParseUser.getCurrentUser().getUsername());
                    newPost.put("imgTitle", mTitleEditText.getText().toString());
                    newPost.put("likeNumber", 0);
                    switch (String.valueOf(mCategorySpinner.getSelectedItem())){
                        case "Best of the Past":
                            newPost.put("category", "BestOfPast");
                            break;
                        case "A Day as a Singaporean":
                            newPost.put("category", "DayAsSGean");
                            break;
                        case "Future Hopes for Singapore":
                            newPost.put("category", "FutureHopes");
                            break;
                        default:
                            newPost.put("category", "DayAsSGean");
                            break;
                    }
                    Drawable drawable = getResources().getDrawable(R.drawable.singapicsicon);

                    Bitmap image = ((BitmapDrawable) drawable).getBitmap();

                    // Resize photo
                    Bitmap imageScaled = Bitmap.createScaledBitmap(image, 200, 200
                            * image.getHeight() / image.getWidth(), false);

                    /* Override Android default landscape orientation and save portrait
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    Bitmap rotatedScaledImage = Bitmap.createBitmap(imageScaled, 0,
                            0, imageScaled.getWidth(), imageScaled.getHeight(),
                            matrix, true);*/

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    //rotatedScaledImage.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    imageScaled.compress(Bitmap.CompressFormat.JPEG, 100, bos);

                    byte[] scaledData = bos.toByteArray();

                    // Save the scaled image to Parse
                    final ParseFile photoFile = new ParseFile("test_photo.jpg", scaledData);
                    photoFile.saveInBackground(new SaveCallback() {

                        public void done(ParseException e) {
                            if (e != null) {
                                Toast.makeText(PostNewActivity.this,
                                        "Error saving: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            } else {
                                newPost.put("actualImage", photoFile);
                                newPost.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        Toast.makeText(PostNewActivity.this, "Post Uploaded!", Toast.LENGTH_LONG).show();
                                        Intent mainActIntent = new Intent(PostNewActivity.this, MainActivity.class);
                                        startActivity(mainActIntent);
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post_new, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            /* todo add settings activity Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            MainActivity.this.startActivity(intent);*/
        }

        if (id == R.id.action_logout) {
            final ProgressDialog mLogoutLoader = new ProgressDialog(PostNewActivity.this);
            mLogoutLoader.setMessage(getString(R.string.logout_dialog_message));
            mLogoutLoader.show();
            ParseUser.getCurrentUser().logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {
                    mLogoutLoader.dismiss();
                    Intent intent = new Intent(PostNewActivity.this, LoginActivity.class);
                    PostNewActivity.this.startActivity(intent);
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

    private void createSquareLayout() {
        //doesn't work for some reason
        RelativeLayout mRelativeLayout = (RelativeLayout)findViewById(R.id.mainRL);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int value = 0;

        if(metrics.widthPixels < metrics.heightPixels){
            value = metrics.widthPixels;
        } else {
            value= metrics.heightPixels;
        }

        mRelativeLayout.getLayoutParams().height = value;
        mRelativeLayout.getLayoutParams().width = value;
    }
}

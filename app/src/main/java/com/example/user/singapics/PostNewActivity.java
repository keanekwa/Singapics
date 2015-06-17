package com.example.user.singapics;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.io.InputStream;


public class PostNewActivity extends ActionBarActivity {

    private EditText mTitleEditText;
    private EditText mCaptionEditText;
    private Spinner mCategorySpinner;
    private Button mPostButton;
    private Button mTakePhoto;
    private ProgressBar mLoading;

    private LinearLayout mButtonsLayout;
    private RelativeLayout mImageLayout;
    private boolean isPicChosen;
    private byte[] chosenPic;
    private ImageView chosenPicPrevew;
    private Button mRemoveButton;
    private Button mUploadButton;

    private static int TAKE_PHOTO_CODE = 21;
    private static int UPLOAD_PHOTO_CODE = 25;

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
        mTakePhoto = (Button)findViewById(R.id.takePhotoButton);
        mButtonsLayout = (LinearLayout)findViewById(R.id.noImgLL);
        mImageLayout = (RelativeLayout)findViewById(R.id.picChosenRL);
        chosenPicPrevew = (ImageView)findViewById(R.id.chosenImage);
        mRemoveButton = (Button)findViewById(R.id.removeButton);
        mUploadButton = (Button)findViewById(R.id.uploadPhotoButton);

        setPicChosen(false);

        mRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPicChosen){
                    setPicChosen(false);
                }
            }
        });

        mTakePhoto.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                Intent intent = new Intent(PostNewActivity.this, CameraActivity.class);
                startActivityForResult(intent, TAKE_PHOTO_CODE);
            }

        });

        mUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create intent to Open Image applications like Gallery, Google Photos
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Start the Intent
                startActivityForResult(galleryIntent, UPLOAD_PHOTO_CODE);
            }
        });

        mPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(String.valueOf(mCategorySpinner.getSelectedItem()).equals("Category")){
                    Toast.makeText(PostNewActivity.this, "Choose a category", Toast.LENGTH_LONG).show();
                }
                else if (!isPicChosen){
                    Toast.makeText(PostNewActivity.this, "Choose or take a picture", Toast.LENGTH_LONG).show();
                }
                else {
                    loading(true);
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
                    Bitmap image = BitmapFactory.decodeByteArray(chosenPic, 0, chosenPic.length);

                    // Resize photo
                    Bitmap imageScaled = Bitmap.createScaledBitmap(image, 300, 300
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
                    String fileName = ParseUser.getCurrentUser().getUsername() + "_photo.jpg";
                    final ParseFile photoFile = new ParseFile(fileName, scaledData);
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
                                        Toast.makeText(PostNewActivity.this, "Picture Uploaded!", Toast.LENGTH_LONG).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        if(requestCode==TAKE_PHOTO_CODE && resultCode==RESULT_OK){
            chosenPic = intent.getByteArrayExtra("DATA");
            setPicChosen(true);
        }
        else if(requestCode==UPLOAD_PHOTO_CODE && resultCode==RESULT_OK){
            if(intent != null){
                Uri selectedImage = intent.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Some cursor magic to fetch the file
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imgDecodableString = cursor.getString(columnIndex);
                cursor.close();

                // Get byte array of Image after decoding the String
                Bitmap bmp = BitmapFactory.decodeFile(imgDecodableString);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                chosenPic = stream.toByteArray();
                setPicChosen(true);
            }
            else{
                Toast.makeText(PostNewActivity.this, "No picture chosen", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void loading(Boolean toLoad){
        if(toLoad){
            mLoading.setVisibility(View.VISIBLE);
            mPostButton.setVisibility(View.INVISIBLE);
        }
        else{
            mLoading.setVisibility(View.INVISIBLE);
            mPostButton.setVisibility(View.VISIBLE);
        }
    }

    private void setPicChosen(Boolean isChosen){
        if(isChosen && chosenPic!=null){
            isPicChosen = true;
            Bitmap bmp = BitmapFactory.decodeByteArray(chosenPic, 0, chosenPic.length);
            chosenPicPrevew.setImageBitmap(bmp);
            mButtonsLayout.setVisibility(View.INVISIBLE);
            mImageLayout.setVisibility(View.VISIBLE);
        }
        else {
            mButtonsLayout.setVisibility(View.VISIBLE);
            mImageLayout.setVisibility(View.INVISIBLE);
            isPicChosen = false;
        }
    }
}

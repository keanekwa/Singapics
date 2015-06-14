package com.example.user.singapics;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;


public class PostNewActivity extends ActionBarActivity {

    private EditText mTitleEditText;
    private EditText mCaptionEditText;
    private Spinner mCategorySpinner;
    private Button mPostButton;

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

        mPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(String.valueOf(mCategorySpinner.getSelectedItem()).equals("Category")){
                    Toast.makeText(PostNewActivity.this, "Choose a category", Toast.LENGTH_LONG);
                }
                else {
                    ParseObject newPost = new ParseObject("allPostings");
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
                    newPost.saveInBackground();

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
}

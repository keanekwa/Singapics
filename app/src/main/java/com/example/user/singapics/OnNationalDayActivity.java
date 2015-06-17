package com.example.user.singapics;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;


public class OnNationalDayActivity extends ActionBarActivity {

    ArrayList<ParseObject> mPosts = new ArrayList<>();
    private ListView lvToShow;
    View mTextEntryView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_national_day);
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#e74c3c")));
        actionBar.setDisplayHomeAsUpEnabled(true);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("onNationalDay");
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    for (int j = 0; j < parseObjects.size(); j++) {
                        mPosts.add(parseObjects.get(j));
                    }

                }
            }
        });

        lvToShow =  (ListView)findViewById(R.id.postListView);
        ArrayAdapter<ParseObject> adapter;
        adapter = new wantAdapter(this, R.layout.want_list, mPosts);
        lvToShow.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_on_national_day, menu);
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

        if (id == R.id.action_newPost) {
           Dialog();
        }

        return super.onOptionsItemSelected(item);
    }

    private class wantAdapter extends ArrayAdapter<ParseObject> {
        //creating variables
        private int mResource;
        private ArrayList<ParseObject> mPosts;

        public wantAdapter(Context context, int resource, ArrayList<ParseObject> posts) {
            super(context, resource, posts);
            mResource = resource;
            mPosts = posts;
        }

        //display subject data in every row of listView
        @Override
        public View getView(final int position, View row, ViewGroup parent) {
            if (row == null) {
                row = LayoutInflater.from(getContext()).inflate(mResource, parent, false);
            }

            final ParseObject currentTopImage = mPosts.get(position);
            TextView titleTextView = (TextView) row.findViewById(R.id.userWant);
            titleTextView.setText(currentTopImage.getString("postTitle"));
            TextView subtitleTextView = (TextView) row.findViewById(R.id.postedBy2);
            subtitleTextView.setText(currentTopImage.getString("createdBy"));

            //set like button status on create
            final ImageView likeImageView = (ImageView) row.findViewById(R.id.likeImageView2);
            final TextView likeNumberTextView = (TextView) row.findViewById(R.id.likeNumber2);
            likeNumberTextView.setText(String.valueOf(currentTopImage.getInt("likeNumber")) + getString(R.string.space) + getString(R.string.likes));
            final ParseUser mCurrentUser = ParseUser.getCurrentUser();
            ArrayList<ParseUser> mFirstWhoLikedList = (ArrayList) currentTopImage.get("likePeopleArray");
            if (mFirstWhoLikedList == null) {
                mFirstWhoLikedList = new ArrayList<>();
            }
            boolean hasLiked = false;
            for (int i = 0; i < mFirstWhoLikedList.size(); i++) {
                if (mFirstWhoLikedList.get(i) == mCurrentUser) {
                    hasLiked = true;
                    break;
                }
            }
            if (hasLiked) {
                likeImageView.setImageDrawable(getResources().getDrawable(R.drawable.like_icon));
            }
            else {
                likeImageView.setImageDrawable(getResources().getDrawable(R.drawable.like_outline));
            }

            //when like button is clicked
            likeImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<ParseUser> mWhoLikedList = (ArrayList) currentTopImage.get("likePeopleArray");
                    if (mWhoLikedList == null) {
                        mWhoLikedList = new ArrayList<>();
                    }
                    boolean hasLiked = false;
                    for (int i = 0; i < mWhoLikedList.size(); i++) {
                        if (mWhoLikedList.get(i) == mCurrentUser) {
                            hasLiked = true;
                            break;
                        }
                    }
                    if (hasLiked) {
                        likeImageView.setImageDrawable(getResources().getDrawable(R.drawable.like_outline));
                        currentTopImage.put("likeNumber", (currentTopImage.getInt("likeNumber") - 1));
                        mWhoLikedList.remove(ParseUser.getCurrentUser());
                        currentTopImage.put("likePeopleArray", mWhoLikedList);
                    } else {
                        likeImageView.setImageDrawable(getResources().getDrawable(R.drawable.like_icon));
                        currentTopImage.put("likeNumber", (currentTopImage.getInt("likeNumber") + 1));
                        mWhoLikedList.add(ParseUser.getCurrentUser());
                        currentTopImage.put("likePeopleArray", mWhoLikedList);
                    }
                    currentTopImage.saveInBackground();
                    likeNumberTextView.setText(String.valueOf(currentTopImage.getInt("likeNumber")) + getString(R.string.space) + getString(R.string.likes));
                }
            });

            return row;
        }
    }

    public void Dialog() {
        LayoutInflater factory = LayoutInflater.from(this);
        mTextEntryView = factory.inflate(R.layout.post_new_want, null);
        Button pbutton = (Button)mTextEntryView.findViewById(R.id.finalizeButton);
        Button nbutton = (Button)mTextEntryView.findViewById(R.id.backButton);

        final Dialog alert = new Dialog(this);
               alert.setTitle("New Post");
                alert.setContentView(mTextEntryView);

        pbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                positiveButton();
                alert.dismiss();
            }
        });
        nbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alert.dismiss();
            }
        });
        alert.show();
    }

    public void positiveButton() {
        EditText mPostField = (EditText) mTextEntryView.findViewById(R.id.captionEditText);
        String post = mPostField.getText().toString();
        ParseObject postObject = new ParseObject("onNationalDay");
        postObject.put("postTitle",post);
        postObject.put("likeNumber",0);
        postObject.put("createdBy", ParseUser.getCurrentUser().getUsername());
        postObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Toast.makeText(OnNationalDayActivity.this,"Posted!",Toast.LENGTH_LONG).show();
            }
        });
        }
}

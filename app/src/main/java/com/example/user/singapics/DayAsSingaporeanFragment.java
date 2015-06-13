package com.example.user.singapics;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class DayAsSingaporeanFragment extends Fragment {
//TODO Do something when list item is pressed

    private ListView lvToShow;
    ArrayList <ParseObject> mDAS = new ArrayList<>();

    public static DayAsSingaporeanFragment newInstance() {
        DayAsSingaporeanFragment fragment = new DayAsSingaporeanFragment();

        return fragment;
    }

    public DayAsSingaporeanFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("allPostings");
        query.whereEqualTo("category","DayAsSGean");
        query.addDescendingOrder("likeNumber");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {

                if(e==null){
                    for (int j = 0; j < parseObjects.size(); j++) {
                        mDAS.add(parseObjects.get(j));
                        if (mDAS.size() == 5){
                            break;
                        }
                    }
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_day_as_singaporean, container, false);
        lvToShow =  (ListView)view.findViewById(R.id.imgListView4);
        ArrayAdapter<ParseObject> adapter;
        adapter = new DayAsSGeanAdapter(getActivity(), R.layout.photos_list, mDAS);
        lvToShow.setAdapter(adapter);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private class DayAsSGeanAdapter extends ArrayAdapter<ParseObject> {
        //creating variables
        private int mResource;
        private ArrayList<ParseObject> mTopPics;

        public DayAsSGeanAdapter(Context context, int resource, ArrayList<ParseObject> achievementTitles) {
            super(context, resource, achievementTitles);
            mResource = resource;
            mTopPics = achievementTitles;
        }

        //display subject data in every row of listView
        @Override
        public View getView(final int position, View row, ViewGroup parent) {
            if (row == null) {
                row = LayoutInflater.from(getContext()).inflate(mResource, parent, false);
            }

            final ParseObject currentTopImage = mTopPics.get(position);
            TextView titleTextView = (TextView) row.findViewById(R.id.imgTitle);
            titleTextView.setText(currentTopImage.get("imgTitle").toString());
            TextView likeNumberTextView = (TextView) row.findViewById(R.id.likeNumber);
            likeNumberTextView.setText(currentTopImage.get("likeNumber").toString());
            TextView subtitleTextView = (TextView) row.findViewById(R.id.postedBy);
            subtitleTextView.setText(currentTopImage.get("createdBy").toString());
            ParseFile fileObject = currentTopImage.getParseFile("actualImage");
            final ImageView actualImage = (ImageView) row.findViewById(R.id.topImgView);
            fileObject.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (e == null) {
                        Bitmap bmp = BitmapFactory
                                .decodeByteArray(
                                        data, 0,
                                        data.length);
                        actualImage.setImageBitmap(bmp);
                    }
                }
            });
            return row;
        }
    }
}

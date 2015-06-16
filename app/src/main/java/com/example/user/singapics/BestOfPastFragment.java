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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.util.ArrayList;

public class BestOfPastFragment extends Fragment {

    private ListView lvToShow;
    ArrayList <ParseObject> mBOP = new ArrayList<>();

    public static BestOfPastFragment newInstance() {
        BestOfPastFragment fragment = new BestOfPastFragment();

        return fragment;
    }

    public BestOfPastFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_best_of_past, container, false);
        lvToShow =  (ListView)view.findViewById(R.id.imgListView3);
        ArrayAdapter<ParseObject> adapter;
        adapter = new BestOFPastAdapter(getActivity(), R.layout.photos_list, MainActivity.mBOP);
        lvToShow.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        lvToShow.setAdapter(adapter);
        /* 'see more' button
        View buttonView = inflater.inflate(R.layout.footer_view, container, false);
        lvToShow.addFooterView(buttonView);*/
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

    private class BestOFPastAdapter extends ArrayAdapter<ParseObject> {
        //creating variables
        private int mResource;
        private ArrayList<ParseObject> mTopPics;

        public BestOFPastAdapter(Context context, int resource, ArrayList<ParseObject> achievementTitles) {
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
            titleTextView.setText(currentTopImage.getString("imgTitle"));
            TextView likeNumberTextView = (TextView) row.findViewById(R.id.likeNumber);
            likeNumberTextView.setText(String.valueOf(currentTopImage.getInt("likeNumber")) + getString(R.string.space) + getString(R.string.likes));
            TextView subtitleTextView = (TextView) row.findViewById(R.id.postedBy);
            subtitleTextView.setText(currentTopImage.getString("createdBy"));
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

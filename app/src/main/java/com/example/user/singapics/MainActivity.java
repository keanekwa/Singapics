package com.example.user.singapics;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    public static ArrayList<ParseObject> mFHF = new ArrayList<>();
    public static ArrayList<ParseObject> mDAS = new ArrayList<>();
    public static ArrayList<ParseObject> mBOP = new ArrayList<>();
    public static ArrayList<ParseObject> mTop = new ArrayList<>();
    public static  Boolean finishLoad = false;

    private BestOfPastFragment pastFrag;
    private DayAsSingaporeanFragment presentFrag;
    private FutureHopesFragment futureFrag;
    private TopPhotosFragment topFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#e74c3c")));
        actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#e74c3c")));
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        View customNav = LayoutInflater.from(this).inflate(R.layout.actionbar, null); // layout which contains your button.
        actionBar.setCustomView(customNav, layoutParams);


        if(ParseUser.getCurrentUser()==null){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(actionBar.newTab().setText(mSectionsPagerAdapter.getPageTitle(i)).setTabListener(this));
        }

      new UpdateTask().execute();

    }

    private class UpdateTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute(){
            mTop.clear();
            mFHF.clear();
            mBOP.clear();
            mDAS.clear();
        }
        @Override
        protected Boolean doInBackground(Void... Params) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("allPostings");
            query.addDescendingOrder("likeNumber");
            query.setLimit(15);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    if (e == null) {
                        for (int j = 0; j < parseObjects.size(); j++) {
                            mTop.add(parseObjects.get(j));
                            if (mTop.size() == 6) {
                                break;
                            }
                        }

                    }
                }
            });
            ParseQuery<ParseObject> query2 = ParseQuery.getQuery("allPostings");
            query2.addDescendingOrder("createdAt");
            query2.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    for (int j = 0; j < list.size(); j++) {
                        String category = list.get(j).getString("category");
                        switch (category) {
                            case "BestOfPast":
                                if (mBOP.size() != 6) mBOP.add(list.get(j));
                                break;
                            case "DayAsSGean":
                                if (mDAS.size() != 6) mDAS.add(list.get(j));
                                break;
                            case "FutureHopes":
                                if (mFHF.size() != 6) mFHF.add(list.get(j));
                                break;
                        }

                    }
                }
            });

            return true;
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_postNew) {
            Intent intent = new Intent(MainActivity.this, PostNewActivity.class);
            MainActivity.this.startActivity(intent);
        }

        if (id == R.id.action_Posts) {
            Intent intent = new Intent(MainActivity.this, OnNationalDayActivity.class);
            MainActivity.this.startActivity(intent);
        }

        if (id == R.id.action_settings) {
            /* todo add settings activity Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            MainActivity.this.startActivity(intent);*/
        }

        if (id == R.id.action_logout) {
            final ProgressDialog mLogoutLoader = new ProgressDialog(MainActivity.this);
            mLogoutLoader.setMessage(getString(R.string.logout_dialog_message));
            mLogoutLoader.show();
            ParseUser.getCurrentUser().logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {
                    mLogoutLoader.dismiss();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    MainActivity.this.startActivity(intent);
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    topFrag = TopPhotosFragment.newInstance();
                    return topFrag;
                case 1:
                    pastFrag = BestOfPastFragment.newInstance();
                    return pastFrag;
                case 2:
                    presentFrag = DayAsSingaporeanFragment.newInstance();
                    return presentFrag;
                case 3:
                    futureFrag = FutureHopesFragment.newInstance();
                    return futureFrag;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
                case 3:
                    return getString(R.string.title_section4).toUpperCase(l);
            }
            return null;
        }
    }


    /*Handles refreshing //TODO:AD's working on this halfway
    public void timeToRefresh(){
        if(pastFrag!=null) pastFrag.refresh();
        if(presentFrag!=null) presentFrag.refresh();
        if(futureFrag!=null) futureFrag.refresh();
        if(topFrag!=null) topFrag.refresh();
    }*/

}

package com.dots.focus.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.dots.focus.controller.DashboardController;


import com.dots.focus.R;
import com.dots.focus.model.DayBlock;
import com.dots.focus.model.HourBlock;
import com.dots.focus.util.FetchAppUtil;
import com.dots.focus.util.TrackAccessibilityUtil;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Calendar;
import java.util.List;

public class DashboardActivity extends BaseActivity {

    static final String TAG = "DashboardActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Button test1 = (Button) findViewById(R.id.test1_button);
        test1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "test1_button clicked.");
                TrackAccessibilityUtil.getCurrentDay(System.currentTimeMillis()).saveEventually(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null)  Log.d(TAG, "Day succeeded saving.");
                        else            Log.d(TAG, "Day failed saving.");
                    }
                });
                TrackAccessibilityUtil.getCurrentHour(System.currentTimeMillis()).saveEventually(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null)  Log.d(TAG, "Hour succeeded saving.");
                        else            Log.d(TAG, "Hour failed saving.");
                    }
                });
                Log.d(TAG, "test1_button finished.");
            }
        });
        Button test2 = (Button) findViewById(R.id.test2_button);
        test2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "test2_button clicked.");
                ParseQuery<DayBlock> query = ParseQuery.getQuery("DayBlock");
                // query.fromLocalDatastore();
                query.findInBackground(new FindCallback<DayBlock>() {
                    @Override
                    public void done(List<DayBlock> list, ParseException e) {
                        Log.d(TAG, "query done.");
                        if (e != null)
                            Log.d(TAG, "DayBlock error: " + e.getMessage());


                        else if (list == null)
                            Log.d(TAG, "DayBlock list is null");

                        else {
                            for (int i = 0; i < list.size(); ++i){
                                Calendar rightNow = Calendar.getInstance();
                                rightNow.setTimeInMillis(list.get(i).getTime());

                                Log.d(TAG, "DayBlock" + i + ", Year: " + rightNow.get(Calendar.YEAR)
                                    + ", Month: " + rightNow.get(Calendar.MONTH) +
                                    ", Day: " + rightNow.get(Calendar.DAY_OF_MONTH) +
                                    ", Hour: " + rightNow.get(Calendar.HOUR_OF_DAY)
                                );
                            }

                        }

                    }
                });

                ParseQuery<HourBlock> query2 = ParseQuery.getQuery("HourBlock");
                // query2.fromLocalDatastore();
                query2.findInBackground(new FindCallback<HourBlock>() {
                    @Override
                    public void done(List<HourBlock> list, ParseException e) {
                        Log.d(TAG, "query2 done.");
                        if (e != null)
                            Log.d(TAG, "HourBlock error: " + e.getMessage());

                        else if (list == null)
                            Log.d(TAG, "HourBlock list is null");

                        else {
                            for (int i = 0; i < list.size(); ++i)
                                Log.d(TAG, "HourBlock" + i + ": " + list.get(i).toString());
                        }

                    }
                });
                Log.d(TAG, "test2_button finished.");
            }
        });

        //Fetch Facebook user info if it is logged
        ParseUser currentUser = ParseUser.getCurrentUser();
        if ((currentUser != null) && currentUser.isAuthenticated()) {
            DashboardController.makeMeRequest();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // Check if the user is currently logged
            // and show any cached content
//      updateViewsWithProfileInfo();
        } else {
            // If the user is not logged in, go to the
            // activity showing the login view.
            startLoginActivity();
        }
    }

    private void startLoginActivity() {
        FetchAppUtil.loadParseApps();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


}
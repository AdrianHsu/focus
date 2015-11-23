package com.dots.focus.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.dots.focus.R;
import com.dots.focus.controller.LoginController;

import com.dots.focus.service.GetAppsService;
import com.dots.focus.util.CreateInfoUtil;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.parse.LogInCallback;
import com.parse.ParseFacebookUtils;
import com.parse.ParseException;
import com.parse.ParseUser;

import android.content.Intent;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import bolts.Task;


public class LoginActivity extends AppCompatActivity {

    static final String TAG = "LoginActivity";
    private Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (LoginController.hasLoggedIn()) {
            showSetInfoActivity();
//      Parse.enableLocalDatastore(this); //Exception not yet resolved
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {

        super.onResume();
        if (LoginController.checkPreviousLogin(this)) {
            showIntroActivity();
        }
    }

    public void onLoginClick(View v) {
        progressDialog = ProgressDialog.show(LoginActivity.this, "", "Logging in...", true);

        List<String> permissions = Arrays.asList("public_profile", "user_friends");
        // NOTE: for extended permissions, like "user_about_me", your app must be reviewed by the Facebook team
        // (https://developers.facebook.com/docs/facebook-login/permissions/)

        Task<ParseUser> users = ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                progressDialog.dismiss();
                if (user == null) {
                    Log.d("FBUser", "Uh oh. The user cancelled the Facebook login.");
                } else if (user.isNew()) {
                    user.saveEventually();
                    Log.d("FBUser", "User signed up and logged in through Facebook!");
                    Log.d("FBUser", user.toString());
                    Log.d("FBUser", "username: " + user.getUsername());
                    Log.d("FBUser", "email: " + user.getEmail());
                    Log.d("FBUser", "sessionToken: " + user.getSessionToken());
                    Log.d("FBUser", "objectId: " + user.getObjectId());

                    getFriendsInfo();
                    CreateInfoUtil.logInByFb(user.getUsername());
                    showSetInfoActivity();
                } else {
                    user.saveEventually();
                    Log.d("FBUser", "User logged in through Facebook!");
                    Log.d("FBUser", user.toString());
                    Log.d("FBUser", "username: " + user.getUsername());
                    Log.d("FBUser", "email: " + user.getEmail());
                    Log.d("FBUser", "sessionToken: " + user.getSessionToken());
                    Log.d("FBUser", "objectId: " + user.getObjectId());

                    getFriendsInfo();
                    CreateInfoUtil.logInByFb(user.getUsername());
                    showSetInfoActivity();
                }

            }
        });

        Intent intent = new Intent(this, GetAppsService.class);
        startService(intent);
    }

    private void showIntroActivity() {
        Intent intent = new Intent(this, IntroActivity.class);
        startActivity(intent);
    }

    private void showSetInfoActivity() {
        Intent intent = new Intent(this, CreateInfoActivity.class);
        startActivity(intent);
    }

    private void showDashboardActivity() {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
    }

    private void getFriendsInfo() {
        GraphRequestBatch batch = new GraphRequestBatch(
            GraphRequest.newMyFriendsRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONArrayCallback() {
                    @Override
                    public void onCompleted(JSONArray jsonArray, GraphResponse response) {
                    // Application code for users friends
                    Log.d("FBUser", "getFriendsData onCompleted : jsonArray " + jsonArray);
                    Log.d("FBUser", "getFriendsData onCompleted : response " + response);
                    try {
                        JSONObject jsonObject = response.getJSONObject();
                        Log.d("FBUser", "getFriendsData onCompleted : jsonObject " + jsonObject);
                        JSONObject summary = jsonObject.getJSONObject("summary");
                        Log.d("FBUser", "getFriendsData onCompleted : summary total_count - " + summary.getString("total_count"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            })
        );
        batch.addCallback(new GraphRequestBatch.Callback() {
            @Override
            public void onBatchCompleted(GraphRequestBatch graphRequests) {
                // Application code for when the batch finishes
            }
        });
        batch.executeAsync();

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,picture");


        /*
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
      new GraphRequest.GraphJSONObjectCallback() {
        @Override
        public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {

          if (jsonObject != null) {
            JSONObject userProfile = new JSONObject();

            try {
              userProfile.put("facebookId", jsonObject.getLong("id"));
              userProfile.put("name", jsonObject.getString("name"));

              //if (jsonObject.getString("gender") != null)
              //userProfile.put("gender", jsonObject.getString("gender"));

              //if (jsonObject.getString("email") != null)
              //userProfile.put("email", jsonObject.getString("email"));

              // Save the user profile info in a user property
              ParseUser currentUser = ParseUser.getCurrentUser();
              currentUser.put("profile", userProfile);
              currentUser.saveInBackground();

              // Show the user info
              updateViewsWithProfileInfo();
            } catch (JSONException e) {
              Log.d(Test1Android.TAG,
                      "Error parsing returned user data. " + e);
            }
          } else if (graphResponse.getError() != null) {
            switch (graphResponse.getError().getCategory()) {
              case LOGIN_RECOVERABLE:
                Log.d(Test1Android.TAG,
                        "Authentication error: " + graphResponse.getError());
                break;

              case TRANSIENT:
                Log.d(Test1Android.TAG,
                        "Transient error. Try again. " + graphResponse.getError());
                break;

              case OTHER:
                Log.d(Test1Android.TAG,
                        "Some other error: " + graphResponse.getError());
                break;
            }
          }
        }
      });

    request.executeAsync();

         */
    }

}

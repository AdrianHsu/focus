package com.dots.focus.controller;

import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;


public class DashboardController {

  static final String TAG = "DashboardController";

  public DashboardController(){}

  public static void makeMeRequest() {
    Log.d(TAG, "makeMeRequest");
    GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
      new GraphRequest.GraphJSONObjectCallback() {
        @Override
        public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
          if (jsonObject != null) {
            try {
              Long id = jsonObject.getLong("id");
              String name = jsonObject.getString("name");
              Log.d(TAG, jsonObject.toString());
              Log.d(TAG, "id: " + id);
              Log.d(TAG, "name: " + name);
              // Save the user profile info in a user property
              ParseUser currentUser = ParseUser.getCurrentUser();
              ParseInstallation currentInstallation = ParseInstallation.getCurrentInstallation();

              currentUser.put("installationId", currentInstallation.getInstallationId());
              currentUser.put("user_id", id);
              currentUser.put("user_name", name);
              currentInstallation.put("fbId", id);

              currentUser.saveEventually(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                  if (e == null)
                    Log.d(TAG, "Succeed updating.");
                  else
                    Log.d(TAG, "Failed updating: " + e.getMessage());
                }
              });
              currentInstallation.saveEventually();

            } catch (JSONException e) {
              Log.d(TAG,
                "Error parsing returned user data. " + e);
            }
          } else if (graphResponse.getError() != null) {
            switch (graphResponse.getError().getCategory()) {
              case LOGIN_RECOVERABLE:
                Log.d(TAG,
                  "Authentication error: " + graphResponse.getError());
                break;

              case TRANSIENT:
                Log.d(TAG,
                  "Transient error. Try again. " + graphResponse.getError());
                break;

              case OTHER:
                Log.d(TAG,
                  "Some other error: " + graphResponse.getError());
                break;
            }
          }
        }
      });

    request.executeAsync();
  }
}

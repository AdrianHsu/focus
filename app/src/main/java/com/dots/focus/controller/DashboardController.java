package com.dots.focus.controller;

import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import com.parse.ParseUser;
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
              Log.d(TAG, jsonObject.toString());
              Log.d(TAG, "id: " + jsonObject.getLong("id"));
              Log.d(TAG, "name: " + jsonObject.getString("name"));
              // Save the user profile info in a user property
              ParseUser currentUser = ParseUser.getCurrentUser();
              currentUser.put("id", jsonObject.getLong("id"));
              currentUser.put("name", jsonObject.getString("name"));
              currentUser.saveEventually();

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

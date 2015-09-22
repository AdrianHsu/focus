package com.dots.focus.controller;

import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import com.parse.ParseUser;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by AdrianHsu on 15/9/23.
 */
public class DashboardController {

  static final String TAG = "DashboardController";

  public DashboardController(){}

  public static void makeMeRequest() {

    GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
      new GraphRequest.GraphJSONObjectCallback() {
        @Override
        public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
          if (jsonObject != null) {
            JSONObject userProfile = new JSONObject();

            try {
              userProfile.put("facebookId", jsonObject.getLong("id"));
              userProfile.put("name", jsonObject.getString("name"));

//              if (jsonObject.getString("gender") != null)
//                userProfile.put("gender", jsonObject.getString("gender"));

//              if (jsonObject.getString("email") != null)
//                userProfile.put("email", jsonObject.getString("email"));

              // Save the user profile info in a user property
              ParseUser currentUser = ParseUser.getCurrentUser();
              currentUser.put("profile", userProfile);
              currentUser.saveInBackground();

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

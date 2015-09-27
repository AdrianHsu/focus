package com.dots.focus.controller;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.dots.focus.R;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;


/**
 * Created by AdrianHsu on 15/9/23.
 */
public class LoginController {

  static final String TAG = "LoginController";

  public LoginController(){};

  public static boolean hasLoggedIn() {
    // Check if there is a currently logged in user
    // and if it's linked to a Facebook account.
    ParseUser currentUser = ParseUser.getCurrentUser();
    if ((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)) {
      return true;
    }
    else {
      return false;
    }
  }
  public static boolean checkPreviousLogin(Context context) {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    boolean previouslyStarted = prefs.getBoolean(context.getString(R.string
        .pref_previously_started),
      false);
    if(!previouslyStarted) {
      SharedPreferences.Editor edit = prefs.edit();
      edit.putBoolean(context.getString(R.string.pref_previously_started), Boolean.TRUE);
      edit.commit();
      return true;
    } else {
      return false;
    }
  }
}

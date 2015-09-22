package com.dots.focus.controller;


import android.util.Log;

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
}

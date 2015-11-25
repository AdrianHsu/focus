package com.dots.focus.util;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

// Username == Email
// Gender: boolean, Male: true; Female: false
// Birth: int, Year Of Birth, ex: 1995
// Occupation: String, ex: Student

public class CreateInfoUtil {
    public static boolean loggingIn = false;
    static String TAG = "CreateInfoUtil";

    public static void setUserInfo(String text1, String text2, boolean store){
        Log.d(TAG, "setUserInfo: " + text1 + " " + text2);

        if(text1 == null || text2 == null)  return;
        ParseUser userInfo = ParseUser.getCurrentUser();

        switch (text1) {
            case "Gender":
                if(text2.equals("Male"))    userInfo.put("Gender", true);
                else if(text2.equals("Female")) userInfo.put("Gender", false);
                break;
            case "Birth":
                userInfo.put("Birth", Integer.valueOf(text2));
                break;
            case "Email":
                userInfo.setEmail(text2);
                break;
            case "AppIndex":
                userInfo.put("AppIndex", Integer.valueOf(text2));
                break;
            default:
                userInfo.put(text1, text2);
                break;
        }

        if(store)   update();
    }

    public static void logInByFb(String username) {
        loggingIn = true;
        ParseUser user = ParseUser.getCurrentUser();
        Log.d(TAG, "start log in by fb.");
        if (user != null) {
            Log.d(TAG, "user != null.");
            if (user.getUsername().equals(username)) {
                if (user.has("installationId")) {
                    Log.d(TAG, "already logged in.");
                    loggingIn = false;
                    return;
                }
            }
            Log.d(TAG, "logging out.");
            ParseUser.logOutInBackground();
        }

        if (ParseUser.getCurrentUser() != null) {
            loggingIn = false;
            return;
        }
        Log.d(TAG, "signing up.");
        signUp(username);
    }

    public static void update() {
        Log.d(TAG, "start update");
        ParseUser temp = ParseUser.getCurrentUser();
        if (temp != null) {
            temp.saveEventually();
            Log.d(TAG, "succeed update");
        }
        else Log.d(TAG, "fail update");
    }

    static void signUp(String username) {
        ParseUser userInfo = new ParseUser();
        userInfo.setUsername(username);
        userInfo.setPassword("focus");
        if (userInfo.getEmail() == null)    userInfo.setEmail("");
        userInfo.put("installationId",
                ParseInstallation.getCurrentInstallation().get("installationId"));
        userInfo.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    loggingIn = false;
                    Log.d(TAG, "succeed signing up.");
                    update();
                } else {
                    Log.d(TAG, "fail signing up.");
                    loggingIn = false;
                    // TBD
                }
            }
        });
    }
}

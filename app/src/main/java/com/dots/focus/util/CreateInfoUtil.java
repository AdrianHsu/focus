package com.dots.focus.util;

import android.util.Log;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

// Username == Email
// Gender: boolean, Male: true; Female: false
// Birth: int, Year Of Birth, ex: 1995
// Occupation: String, ex: Student

public class CreateInfoUtil {
    static boolean logIn;
    static String TAG = "CreateInfoUtil";
    static {
            ParseUser.getCurrentUser().put("installationId",
            ParseInstallation.getCurrentInstallation().get("installationId")
        );
    }

    public static void setUserInfo(String text1, String text2, boolean store){
        Log.d(TAG, "setUserInfo: " + text1 + " " + text2);

        if(text1 == null || text2 == null)  return;
        ParseUser userInfo = ParseUser.getCurrentUser();

        switch(text1){
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
            default:
                userInfo.put(text1, text2);
                break;
        }

        if(store)   update();
    }

    public static void logInByFb(String username) {
        Log.d(TAG, "logInByFb: " + username);
        if(logIn){
            ParseUser.logOutInBackground();
            logIn = false;
        }
        ParseUser.logInInBackground(username, "focus", new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null && e == null) {
                    logIn = true;
                }
            }
        });
        if(logIn)   return;

        ParseUser userInfo = new ParseUser();
        userInfo.setUsername(username);
        userInfo.setPassword("focus");

        userInfo.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    logIn = true;
                }
            }
        });
        update();
    }

    public static void update(){
        Log.d(TAG, "update");
        ParseUser.getCurrentUser().saveEventually();
    }
}

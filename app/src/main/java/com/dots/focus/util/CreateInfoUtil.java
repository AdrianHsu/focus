package com.dots.focus.util;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * Created by Harvey on 2015/10/8.
 */

// Username == Email
// Gender: boolean, Male: true; Female: false
// Birth: int, Year Of Birth, ex: 1995
// Occupation: String, ex: Student

public class CreateInfoUtil {
    static boolean logIn;
    static {
        ParseUser.getCurrentUser().put("installationId",
                ParseInstallation.getCurrentInstallation().get("installationId")
        );
    }

    public static void setUserInfo(String text1, String text2){
        ParseUser userInfo = ParseUser.getCurrentUser();
        if(text1.equals("Gender")){
            if(text2.equals("Male"))    userInfo.put("Gender", true);
            else if(text2.equals("Female")) userInfo.put("Gender", false);
        }
        else if(text1.equals("Birth")) {
            userInfo.put("Birth", Integer.valueOf(text2));
        }
        else    userInfo.put(text1, text2);
    }

    public static void logInByFb(String email) {
        if(logIn){
            ParseUser.logOutInBackground();
            logIn = false;
        }
        ParseUser.logInInBackground(email, "focus", new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null && e == null) {
                    logIn = true;
                }
            }
        });
        if(logIn)   return;

        ParseUser userInfo = new ParseUser();
        userInfo.setUsername(email);
        userInfo.setPassword("focus");
        userInfo.setEmail(email);

        userInfo.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    logIn = true;
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                }
            }
        });

    }
}

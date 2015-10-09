package com.dots.focus.util;

import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by Harvey on 2015/10/8.
 */
public class CreateInfoUtil {
    private static ParseUser userInfo = new ParseUser();
    static {
        userInfo.put("installationId",
            ParseInstallation.getCurrentInstallation().get("installationId")
        );
    }

    public static void setUserInfo(String text1, String text2){
        if(text1.equals("Gender")){
            if(text2.equals("Male"))    userInfo.put("Gender", true);
            else if(text2.equals("Female")) userInfo.put("Gender", false);
        }
        else if(text1.equals("Birth")){
            userInfo.put("Birth", Integer.valueOf(text2));
        }

        else    userInfo.put(text1, text2);
    }
    public static void update(){

    }
}

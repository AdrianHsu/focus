package com.dots.focus.config;

/**
 * Created by AdrianHsu on 15/8/14.
 */

public class Config {

  public static final String FOCUS_APPLICATION_ID = "FdDNL7MnpJPyz6Ebybdklu7YUM1XyMhG4e0arQuI";
  public static final String FOCUS_CLIENT_ID = "oVxqOZc7wFBa0L4aDFCCdDiSC2LiHWWHKh18arv9";

  private static int currentDrawerItem = 0;

  public static void setCurrentDrawerItem(int currentDrawerItem) {
    Config.currentDrawerItem = currentDrawerItem;
  }

  public static int getCurrentDrawerItem() {
    return currentDrawerItem;
  }

  public enum DrawerItem {
    DASHBOARD, INBOX, TIMELINE, OVERVIEW, PROFILE, ADD_FRIEND, LOGOUT, SETTINGS
  }
  public static String[] Title = {"Dashboard", "Inbox", "Timeline", "Overview",
                           "Profile", "Add Friend","Log Out", "Settings"};
}
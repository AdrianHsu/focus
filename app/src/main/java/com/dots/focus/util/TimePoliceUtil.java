package com.dots.focus.util;

import android.util.Log;

import com.dots.focus.config.TimePoliceState;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TimePoliceUtil {
    private static String TAG = "TimePoliceUtil";
    public static int policeNum = 0;
    private static final int policeNumLimit = 2;
    public static ArrayList<Long> invitingIdList = new ArrayList<>();
    public static ArrayList<Long> invitedIdList = new ArrayList<>();

    static {
        JSONArray friends = ParseUser.getCurrentUser().getJSONArray("Friends");
        for (int i = 0, length = friends.length(); i < length; ++i) {
            try {
                if (friends.getJSONObject(i).getBoolean("timeLocked"))
                    ++policeNum;
            } catch (JSONException e) { Log.d(TAG, e.getMessage()); }
        }
        if (policeNum > policeNumLimit)
            Log.d(TAG, "policeNum " + policeNum + " exceeds limit: " + policeNumLimit);
    }

    public static void timePoliceInvite(Long id, String name, int lock_time) {
        invitedIdList.add(id);

        ParseUser currentUser = ParseUser.getCurrentUser();

        ParseObject invite = new ParseObject("TimePoliceInvitation");
        invite.put("user_id_inviting", currentUser.getLong("user_id"));
        invite.put("user_name_inviting", currentUser.getString("user_name"));
        invite.put("user_id_invited", id);
        invite.put("user_name_invited", name);
        invite.put("time", System.currentTimeMillis());
        invite.put("lock_time", lock_time);
        invite.put("state", TimePoliceState.INVITE_NOT_DOWNLOADED.getValue());

        invite.saveEventually();
    }
    public static void timePoliceReply(final boolean reply, String objectId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("TimePoliceInvitation");
        query.fromLocalDatastore();
        query.getInBackground(objectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject invite, ParseException e) {
                if (e == null && invite != null) {
                    Long id = invite.getLong("user_id_inviting");
                    invitedIdList.remove(id);

                    invite.put("reply", reply);
                    invite.put("state", TimePoliceState.REPLY_NOT_DOWNLOADED.getValue());
                    invite.saveEventually();

                    if (reply) {
                        ParseUser currentUser = ParseUser.getCurrentUser();
                        JSONArray friends = currentUser.getJSONArray("Friends");
                        boolean found = false;
                        for (int i = 0, length = friends.length(); i < length; ++i) {
                            try {
                                JSONObject object = friends.getJSONObject(i);
                                if (object.getLong("id") == id) {
                                    found = true;
                                    object.put("timeLock", true);
                                    friends.put(i, object);
                                    currentUser.put("Friends", friends);
                                    break;
                                }
                            } catch (JSONException e1) {
                                Log.d(TAG, e1.getMessage());
                            }
                        }
                        if (!found)
                            Log.d(TAG, "reply == true while cannot find the friend: " +
                                    invite.getString("user_name_inviting"));
                    }
                } else if (e != null) {
                    Log.d(TAG, e.getMessage());
                }
            }
        });
    }
    public static void timePoliceConfirm(Long id, String name) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        JSONArray friends = currentUser.getJSONArray("Friends");
        boolean found = false;
        for (int i = 0, length = friends.length(); i < length; ++i) {
            try {
                JSONObject object = friends.getJSONObject(i);
                if (object.getLong("id") == id) {
                    found = true;
                    object.put("timeLocked", true);
                    friends.put(i, object);
                    currentUser.put("Friends", friends);
                    break;
                }
            } catch (JSONException e1) { Log.d(TAG, e1.getMessage()); }
        }
        if (!found)
            Log.d(TAG, "timePoliceInvitation confirmed while cannot find the friend: " + name);
    }
}

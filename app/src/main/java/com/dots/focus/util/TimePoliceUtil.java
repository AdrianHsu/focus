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
    public static ArrayList<JSONObject> invitingList = new ArrayList<>();

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

    public static boolean timePoliceInvite(Long id, String name) {
        if (policeNum >= policeNumLimit)    return false;

        invitingIdList.add(id);

        ParseUser currentUser = ParseUser.getCurrentUser();
        Long time = System.currentTimeMillis();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", id);
            jsonObject.put("name", name);
            jsonObject.put("time", time);
            jsonObject.put("lock_time", 0);

            invitingList.add(jsonObject);
        } catch (JSONException e) { Log.d(TAG, e.getMessage()); }
        ParseObject invite = new ParseObject("TimePoliceInvitation");
        invite.put("user_id_inviting", currentUser.getLong("user_id"));
        invite.put("user_name_inviting", currentUser.getString("user_name"));
        invite.put("user_id_invited", id);
        invite.put("user_name_invited", name);
        invite.put("time", time);
        invite.put("lock_time", 0);
        invite.put("state", TimePoliceState.INVITE_NOT_DOWNLOADED.getValue());

        invite.saveEventually();

        return true;
    }
    public static void timePoliceReply(final boolean reply, final int lock_time, String objectId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("TimePoliceInvitation");
        query.fromLocalDatastore();
        query.getInBackground(objectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject invite, ParseException e) {
                if (e == null && invite != null) {
                    Long id = invite.getLong("user_id_inviting");
                    invitedIdList.remove(id);

                    invite.put("reply", reply);
                    invite.put("lock_time", lock_time);
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
                                    currentUser.saveEventually();
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
                if (object.getLong("user_id") == id) {
                    found = true;
                    object.put("timeLocked", true);
                    friends.put(i, object);
                    currentUser.put("Friends", friends);
                    currentUser.saveEventually();
                    break;
                }
            } catch (JSONException e1) { Log.d(TAG, e1.getMessage()); }
        }
        if (!found)
            Log.d(TAG, "timePoliceInvitation confirmed while cannot find the friend: " + name);
    }
    public static void timePoliceCancel(Long id) { // The friend is my time police
        ParseUser currentUser = ParseUser.getCurrentUser();
        JSONArray friends = currentUser.getJSONArray("Friends");
        for (int i = 0, length = friends.length(); i < length; ++i) {
            try {
                JSONObject object = friends.getJSONObject(i);
                if (object.getLong("user_id") == id) {
                    if (!object.getBoolean("timeLocked"))
                        Log.d(TAG, "timePoliceCancel, id: " + id + ", name: " +
                                object.getString("user_name") + " was not my time police...");
                    else {
                        object.put("timeLocked", false);
                        friends.put(i, object);
                        currentUser.put("Friends", friends);
                        currentUser.saveEventually();

                        ParseObject cancellation = new ParseObject("TimePoliceCancellation");
                        cancellation.put("user_id_cancelling", currentUser.getLong("user_id"));
                        cancellation.put("user_name_cancelling", currentUser.getString("user_name"));
                        cancellation.put("user_id_cancelled", id);
                        cancellation.put("user_name_cancelled", object.getString("user_name"));
                        cancellation.put("time", System.currentTimeMillis());
                        cancellation.put("state", TimePoliceState.INVITE_NOT_DOWNLOADED.getValue());

                        cancellation.saveEventually();
                    }
                }
            } catch (JSONException e) { Log.d(TAG, e.getMessage()); }
        }
    }

    public static void getTimePoliceCancel(Long id) {

    }

    public static void timePoliceDelete(Long id) { // I'm his/her time police
        ParseUser currentUser = ParseUser.getCurrentUser();
        JSONArray friends = currentUser.getJSONArray("Friends");
        for (int i = 0, length = friends.length(); i < length; ++i) {
            try {
                JSONObject object = friends.getJSONObject(i);
                if (object.getLong("user_id") == id) {
                    if (!object.getBoolean("timeLock"))
                        Log.d(TAG, "timePoliceDelete, I'm not id: " + id + ", name: " +
                                object.getString("user_name") + " 's time police...");
                    else {
                        object.put("timeLock", false);
                        friends.put(i, object);
                        currentUser.put("Friends", friends);
                        currentUser.saveEventually();

                        ParseObject deletion = new ParseObject("TimePoliceDeletion");
                        deletion.put("user_id_deleting", currentUser.getLong("user_id"));
                        deletion.put("user_name_deleting", currentUser.getString("user_name"));
                        deletion.put("user_id_deleted", id);
                        deletion.put("user_name_deleted", object.getString("user_name"));
                        deletion.put("time", System.currentTimeMillis());
                        deletion.put("state", TimePoliceState.INVITE_NOT_DOWNLOADED.getValue());

                        deletion.saveEventually();
                    }
                }
            } catch (JSONException e) { Log.d(TAG, e.getMessage()); }
        }
    }

    public static void getReply(Long id) {
        invitingIdList.remove(id);
        for (int i = 0, length = invitingList.size(); i < length; ++i) {
            try {
                if (invitingList.get(i).getLong("id") == id) {
                    invitingList.remove(i);
                    break;
                }
            } catch (JSONException e) { Log.d(TAG, e.getMessage()); }
        }
    }

    public static boolean isInvitng(Long id) {
        return invitedIdList.contains(id);
    }

}

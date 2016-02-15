package com.dots.focus.util;

import android.util.Log;

import com.dots.focus.config.TimePoliceState;
import com.dots.focus.service.GetTimePoliceInviteService;
import com.dots.focus.service.GetTimePoliceReplyService;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TimePoliceUtil {
    private static String TAG = "TimePoliceUtil";
    public static int policeNum = 0;
    private static final int policeNumLimit = 2;
    public static ArrayList<Long> invitingIdList = new ArrayList<>();
    public static ArrayList<Long> invitedIdList = new ArrayList<>();
    public static ArrayList<JSONObject> timePoliceInvitingList = new ArrayList<>();
    public static int timePoliceStateOffset = 100;

    public static void afterLoginInitialize() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        final Long id = currentUser.getLong("user_id");

        ParseQuery<ParseObject> query = ParseQuery.getQuery("TimePoliceInvitation");
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null && list != null && !list.isEmpty()) {
                    for (int i = 0, length = list.size(); i < length; ++i) {
                        ParseObject invite = list.get(i);

                        if (invite.getLong("user_id_inviting") == id) {
                            try {
                                int state = invite.getInt("state");

                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("id", id);
                                jsonObject.put("name", invite.getString("user_name_inviting"));
                                jsonObject.put("time", invite.getLong("time"));
                                jsonObject.put("lock_time", invite.getInt("lock_time"));
                                jsonObject.put("state", state + timePoliceStateOffset);

                                if (state == TimePoliceState.INVITING.getValue())
                                    timePoliceInvitingList.add(jsonObject);
                                else if (state == TimePoliceState.REPLY_DOWNLOADED.getValue())
                                    GetTimePoliceReplyService.timePoliceReplyList.add(jsonObject);
                                else
                                    Log.d(TAG, "Weird state: " + state);
                            } catch (JSONException e1) {
                                Log.d(TAG, e1.getMessage());
                            }
                        }
                        else if (invite.getLong("user_id_invited") == id) {
                            try {
                                int state = invite.getInt("state");

                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("id", id);
                                jsonObject.put("name", invite.getString("user_name_invited"));
                                jsonObject.put("time", invite.getLong("time"));
                                jsonObject.put("lock_time", invite.getInt("lock_time"));
                                jsonObject.put("state", state + timePoliceStateOffset);

                                if (state == TimePoliceState.INVITE_DOWNLOADED.getValue())
                                    GetTimePoliceInviteService.timePoliceInviteList.add(jsonObject);
                                else
                                    Log.d(TAG, "Weird state: " + state);
                            } catch (JSONException e1) {
                                Log.d(TAG, e1.getMessage());
                            }
                        }
                    }
                }
                else if (e != null) {
                    Log.d(TAG, e.getMessage());
                }
            }
        });
        JSONArray friends = currentUser.getJSONArray("Friends");
        if (friends == null)    return;
        for (int i = 0, length = friends.length(); i < length; ++i) {
            try {
                if (friends.getJSONObject(i).getBoolean("timeLocked"))
                    ++policeNum;
            } catch (JSONException e) { Log.d(TAG, e.getMessage()); }
        }
        if (policeNum > policeNumLimit)
            Log.d(TAG, "policeNum " + policeNum + " exceeds limit: " + policeNumLimit);
    }

    public static boolean timePoliceInvite(Long id, String name, int lock_time) {
        if (policeNum >= policeNumLimit)    return false;

        invitingIdList.add(id);

        ParseUser currentUser = ParseUser.getCurrentUser();
        Long time = System.currentTimeMillis();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", id);
            jsonObject.put("name", name);
            jsonObject.put("time", time);
            jsonObject.put("lock_time", lock_time);
            jsonObject.put("state", TimePoliceState.INVITING.getValue() + timePoliceStateOffset);

            timePoliceInvitingList.add(jsonObject);
        } catch (JSONException e) { Log.d(TAG, e.getMessage()); }
        ParseObject invite = new ParseObject("TimePoliceInvitation");
        invite.put("user_id_inviting", currentUser.getLong("user_id"));
        invite.put("user_name_inviting", currentUser.getString("user_name"));
        invite.put("user_id_invited", id);
        invite.put("user_name_invited", name);
        invite.put("time", time);
        invite.put("lock_time", lock_time);
        invite.put("state", TimePoliceState.INVITE_NOT_DOWNLOADED.getValue());

        invite.saveEventually();
        invite.pinInBackground();

        return true;
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
                    GetTimePoliceInviteService.removeInviteList(id);

                    invite.put("reply", reply);
                    invite.put("state", TimePoliceState.REPLY_NOT_DOWNLOADED.getValue());
                    invite.saveEventually();
                    invite.unpinInBackground();

                    if (reply) {
                        ParseUser currentUser = ParseUser.getCurrentUser();
                        JSONArray friends = currentUser.getJSONArray("Friends");
                        boolean found = false;
                        for (int i = 0, length = friends.length(); i < length; ++i) {
                            try {
                                JSONObject object = friends.getJSONObject(i);
                                if (object.getLong("user_id") == id) {
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
    public static void timePoliceConfirm(Long id, String name, int lock_time) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        JSONArray friends = currentUser.getJSONArray("Friends");
        for (int i = 0, length = friends.length(); i < length; ++i) {
            try {
                JSONObject object = friends.getJSONObject(i);
                if (object.getLong("user_id") == id) {
                    object.put("timeLocked", true);
                    object.put("timeLockedPeriod", lock_time);
                    friends.put(i, object);
                    currentUser.put("Friends", friends);
                    currentUser.saveEventually();
                    return;
                }
            } catch (JSONException e1) { Log.d(TAG, e1.getMessage()); }
        }
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

                        cancellation.saveEventually();
                    }
                    return;
                }
            } catch (JSONException e) { Log.d(TAG, e.getMessage()); }
        }
        Log.d(TAG, "timePoliceCancel: cannot find the friend whose id is " + id);
    }

    public static void getTimePoliceCancel(Long id) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        JSONArray friends = currentUser.getJSONArray("Friends");
        for (int i = 0, length = friends.length(); i < length; ++i) {
            try {
                JSONObject object = friends.getJSONObject(i);
                if (object.getLong("user_id") == id) {
                    if (!object.getBoolean("timeLock"))
                        Log.d(TAG, "getTimePoliceCancel, I'm not id: " + id + ", name: " +
                                object.getString("user_name") + " 's time police...");
                    else {
                        object.put("timeLock", false);
                        friends.put(i, object);
                        currentUser.put("Friends", friends);
                        currentUser.saveEventually();
                    }
                    return;
                }
            } catch (JSONException e) { Log.d(TAG, e.getMessage()); }
        }
        Log.d(TAG, "getTimePoliceCancel: cannot find the friend whose id is " + id);
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

                        ParseObject deletion = new ParseObject("TimePoliceCancellation");
                        deletion.put("user_id_deleting", currentUser.getLong("user_id"));
                        deletion.put("user_name_deleting", currentUser.getString("user_name"));
                        deletion.put("user_id_deleted", id);
                        deletion.put("user_name_deleted", object.getString("user_name"));
                        deletion.put("time", System.currentTimeMillis());

                        deletion.saveEventually();
                    }
                    return;
                }
            } catch (JSONException e) { Log.d(TAG, e.getMessage()); }
        }
        Log.d(TAG, "timePoliceDelete: cannot find the friend whose id is " + id);
    }

    public static void getTimePoliceDelete(Long id) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        JSONArray friends = currentUser.getJSONArray("Friends");
        for (int i = 0, length = friends.length(); i < length; ++i) {
            try {
                JSONObject object = friends.getJSONObject(i);
                if (object.getLong("user_id") == id) {
                    if (!object.getBoolean("timeLocked"))
                        Log.d(TAG, "getTimePoliceCancel, id: " + id + ", name: " +
                                object.getString("user_name") + " is not my time police...");
                    else {
                        object.put("timeLocked", false);
                        friends.put(i, object);
                        currentUser.put("Friends", friends);
                        currentUser.saveEventually();
                    }
                    return;
                }
            } catch (JSONException e) { Log.d(TAG, e.getMessage()); }
        }
        Log.d(TAG, "getTimePoliceDelete: cannot find the friend whose id is " + id);
    }

    public static void getReply(Long id) {
        invitingIdList.remove(id);
        for (int i = 0, length = timePoliceInvitingList.size(); i < length; ++i) {
            try {
                if (timePoliceInvitingList.get(i).getLong("id") == id) {
                    timePoliceInvitingList.remove(i);
                    break;
                }
            } catch (JSONException e) { Log.d(TAG, e.getMessage()); }
        }
    }

    public static boolean isInvitng(Long id) {
        return invitedIdList.contains(id);
    }

}

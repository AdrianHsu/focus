package com.dots.focus.util;

import android.util.Log;

import com.dots.focus.config.FriendRelationship;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FetchFriendUtil {
    private static String TAG = "FetchFriendUtil";
    public static ArrayList<JSONObject> mFriendList = new ArrayList<>();
    public static ArrayList<Long> mConfirmingFriendList = new ArrayList<>();
    public static ArrayList<JSONObject> mConfirmedFriendList = new ArrayList<>();

    public static int checkFriend(Long id) throws JSONException {
        JSONArray friends = ParseUser.getCurrentUser().getJSONArray("Friends");
        if (friends == null) return -1;
        for (int i = 0, length = friends.length(); i < length; ++i)
            if (id.equals(friends.getJSONObject(i).getLong("user_id")))
                return i;
        return -1;
    }

    public static void refresh() {
        GraphRequestBatch batch = new GraphRequestBatch(
                GraphRequest.newMyFriendsRequest(
                        AccessToken.getCurrentAccessToken(),
                        new GraphRequest.GraphJSONArrayCallback() {
                            @Override
                            public void onCompleted(JSONArray jsonArray, GraphResponse response) {
                                // Application code for users friends
                                if (jsonArray != null && response != null) {

                                    mFriendList.clear();
                                    mConfirmedFriendList.clear();
                                    for (int i = 0, length = jsonArray.length(); i < length; ++i) {
                                        try {
                                            Long id = jsonArray.getJSONObject(i).getLong("id");
                                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                                            if (mConfirmingFriendList.contains(id))
                                                continue;
                                            if (checkFriend(id) == -1) {
                                                jsonObject.put("state",
                                                        FriendRelationship.NOT_FRIEND.getValue());
                                                mFriendList.add(jsonObject);
                                            } else {
                                                jsonObject.put("state",
                                                        FriendRelationship.IS_FRIEND.getValue());
                                                mConfirmedFriendList.add(jsonObject);
                                            }
                                        } catch (JSONException e) {
                                            Log.d(TAG, e.getMessage());
                                        }
                                    }
                                }
                            }
                        })
        );
        batch.addCallback(new GraphRequestBatch.Callback() {
            @Override
            public void onBatchCompleted(GraphRequestBatch graphRequests) {
                // Application code for when the batch finishes
            }
        });
        batch.executeAsync();

    }

    public static void friendInvite(Long id, String name) {
        if (!mConfirmingFriendList.contains(id))
            mConfirmingFriendList.add(id);

        final ParseObject invite = new ParseObject("FriendInvitation");

        invite.put("user_id_invited", id);
        invite.put("user_name_invited", name);
        invite.put("user_id_inviting", ParseUser.getCurrentUser().getLong("user_id"));
        invite.put("user_name_inviting", ParseUser.getCurrentUser().getString("user_name"));
        invite.put("time", System.currentTimeMillis());
        invite.put("downloaded", false);
        invite.saveEventually(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("FriendInvitation");
                query.getInBackground(invite.getObjectId(), new GetCallback<ParseObject>() {
                    public void done(ParseObject object, ParseException e) {
                        if (e == null && object != null)
                            Log.d(TAG, "Succeed update FriendInvitation: " + object.toString());
                        else {
                            if (e != null)
                                Log.d(TAG, "Fail update FriendInvitation: " + e.getMessage());
                            if (object == null)
                                Log.d(TAG, "Fail update FriendInvitation: object == null");
                        }
                    }
                });
            }
        });
    }

    public static void friendConfirm(Long id, String name) throws JSONException {
        Log.d(TAG, "friendConfirm...");

        mConfirmingFriendList.remove(id);

        ParseUser currentUser = ParseUser.getCurrentUser();
        JSONArray friends = currentUser.getJSONArray("Friends");
        if (friends == null) friends = new JSONArray();
        JSONObject newFriend = new JSONObject();
        newFriend.put("user_id", id);
        newFriend.put("user_name", name);
        newFriend.put("pop-up", false);
        newFriend.put("timeLock", false);
        newFriend.put("timeLocked", false);
        newFriend.put("numKick", 0);

        friends.put(newFriend);
        currentUser.put("Friends", friends);
        currentUser.saveEventually();

        ParseObject friendConfirm = new ParseObject("FriendConfirmation");
        friendConfirm.put("user_id_inviting", id);
        friendConfirm.put("user_name_inviting", name);
        friendConfirm.put("user_id_invited", currentUser.getLong("user_id"));
        friendConfirm.put("user_name_invited", currentUser.getString("user_name"));
        friendConfirm.put("downloaded", false);

        friendConfirm.saveEventually();

        clearInvitation(id);
    }

    public static void getFriendConfirm(Long id, String name) throws JSONException {
        Log.d(TAG, "getFriendConfirm...");

        mConfirmingFriendList.remove(id);

        ParseUser currentUser = ParseUser.getCurrentUser();
        JSONArray friends = currentUser.getJSONArray("Friends");
        if (friends == null)    friends = new JSONArray();

        JSONObject newFriend = new JSONObject();
        newFriend.put("user_id", id);
        newFriend.put("user_name", name);
        newFriend.put("pop-up", false);
        newFriend.put("timeLock", false);
        newFriend.put("timeLocked", false);
        newFriend.put("numKick", 0);

        friends.put(newFriend);
        currentUser.put("Friends", friends);
        currentUser.saveEventually();

        clearInvitation(id);

        Log.d(TAG, "finish getFriendConfirm...");
    }

    public static void clearInvitation(Long id) {
        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("FriendInvitation"),
                query2 = ParseQuery.getQuery("FriendInvitation");
        query1.whereEqualTo("user_id_invited", id);
        query1.fromLocalDatastore();
        query2.whereEqualTo("user_id_inviting", id);
        query2.fromLocalDatastore();
        List<ParseQuery<ParseObject>> queries = new ArrayList<>();
        queries.add(query1);
        queries.add(query2);

        ParseQuery<ParseObject> query = ParseQuery.or(queries);
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> inviteList, ParseException e) {
                if (e == null && inviteList != null) {
                    Log.d(TAG, "Delete FriendInvitation, size: " + inviteList.size());
                    ParseObject.deleteAllInBackground(inviteList);
                }

            }
        });
    }

    public static void checkRemoveMFL(Long id) throws JSONException {
        for (int i = 0, size = mFriendList.size(); i < size; ++i)
            if (mFriendList.get(i).getLong("id") == id) {
                mFriendList.remove(i);
                return;
            }
    }

//    public static void waitFriendConfirm() {
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("FriendInvitation");
//        query.whereEqualTo("user_id_inviting", ParseUser.getCurrentUser().getLong("id"));
//        query.fromLocalDatastore();
//        query.findInBackground(new FindCallback<ParseObject>() {
//            @Override
//            public void done(List<ParseObject> list, ParseException e) {
//                if (e == null && list != null) {
//                    for (int i = 0, size = list.size(); i < size; ++i) {
//                        Log.d(TAG, "invited: " + list.get(i).getLong("user_id_invited"));
//                    }
//                }
//            }
//        });
//    }
}


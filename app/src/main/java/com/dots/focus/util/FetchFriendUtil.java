package com.dots.focus.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
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

public class FetchFriendUtil {
    private static String TAG = "FetchFriendUtil";
    public static ArrayList<JSONObject> mFriendList = new ArrayList<JSONObject>();

    public static int checkFriend(Long id) throws JSONException {
        JSONArray friends = ParseUser.getCurrentUser().getJSONArray("Friends");
        if (friends == null)    return -1;
        for (int i = 0, length = friends.length(); i < length; ++i)
            if (id.equals(friends.getJSONObject(i).getLong("id")))
                return i;
        return -1;
    }

    public static void getFriendsInfo() {
        GraphRequestBatch batch = new GraphRequestBatch(
                GraphRequest.newMyFriendsRequest(
                        AccessToken.getCurrentAccessToken(),
                        new GraphRequest.GraphJSONArrayCallback() {
                            @Override
                            public void onCompleted(JSONArray jsonArray, GraphResponse response) {
                                // Application code for users friends
                                if(!mFriendList.isEmpty())
                                  mFriendList.clear();
                                for (int i = 0, length = jsonArray.length(); i < length; ++i) {
                                    try {
                                        Long id = jsonArray.getJSONObject(i).getLong("id");

                                        if (checkFriend(id) == -1) {

                                            mFriendList.add(jsonArray.getJSONObject(i));
                                            // showFriend(id ,jsonArray.getJSONObject(i)
                                            // .getString("name"), getProfile(id));
                                        }


                                    } catch (JSONException e) { e.getMessage(); }

                                }
                                /*
                                Log.d("FBUser", "getFriendsData onCompleted : response " + response);
                                try {
                                    JSONObject jsonObject = response.getJSONObject();
                                    Log.d("FBUser", "getFriendsData onCompleted : jsonObject " + jsonObject);
                                    JSONObject summary = jsonObject.getJSONObject("summary");
                                    Log.d("FBUser", "getFriendsData onCompleted : summary total_count - " + summary.getString("total_count"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                */
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
    public static void friendInvite(Long id) {
        final ParseObject invite = new ParseObject("FriendInvitation");

        invite.put("user_id_invited", id);
        invite.put("user_id_inviting", ParseUser.getCurrentUser().getLong("id"));
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
}

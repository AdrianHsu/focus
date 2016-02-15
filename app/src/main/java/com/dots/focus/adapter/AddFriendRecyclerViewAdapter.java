package com.dots.focus.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dots.focus.R;
import com.dots.focus.config.FriendRelationship;
import com.dots.focus.config.TimePoliceState;
import com.dots.focus.service.GetFriendConfirmService;
import com.dots.focus.service.GetTimePoliceReplyService;
import com.dots.focus.ui.KickRequestActivity;
import com.dots.focus.ui.ModifyPermissionActivity;
import com.dots.focus.ui.ReplyPermissionActivity;
import com.dots.focus.util.FetchFriendUtil;
import com.dots.focus.util.TimePoliceUtil;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AddFriendRecyclerViewAdapter extends
  UltimateViewAdapter<UltimateRecyclerviewViewHolder> {

  private ArrayList<JSONObject> friendProfileList;
  private Context mContext;
  private static final int NOT_FRIEND_ITEM = FriendRelationship.NOT_FRIEND.getValue();
  private static final int FRIEND_INVITED_ITEM = FriendRelationship.FRIEND_INVITED.getValue();
  private static final int FRIEND_CONFIRMED_ITEM = FriendRelationship.FRIEND_CONFIRMED.getValue();
//  private static final int FRIEND_ITEM = FriendRelationship.IS_FRIEND.getValue();
  private static final int FRIEND_INVITING_ITEM = FriendRelationship.FRIEND_INVITING.getValue();
  private static final int TP_INVITE_ITEM = TimePoliceState.INVITE_DOWNLOADED.getValue() +
                          TimePoliceUtil.timePoliceStateOffset;
  private static final int TP_REPLY_ITEM = TimePoliceState.REPLY_DOWNLOADED.getValue() +
                          TimePoliceUtil.timePoliceStateOffset;
  private static final int TP_INVITING_ITEM = TimePoliceState.INVITING.getValue() +
                          TimePoliceUtil.timePoliceStateOffset;

  private static final String TAG = "AddFriend";

  public AddFriendRecyclerViewAdapter(ArrayList<JSONObject> friendProfileList, Context context) {
    this.friendProfileList = friendProfileList;
    mContext = context;
  }

  /**
   * Bind Viewholder with data
   *
   * @param holder
   * @param position data source index
   */
  @Override
  public void onBindViewHolder(final UltimateRecyclerviewViewHolder holder, final int position) {
    if (position < getItemCount() && (customHeaderView != null ? position <= friendProfileList.size() : position
      < friendProfileList.size()) && (customHeaderView == null || position > 0)) {

      JSONObject jsonObject = friendProfileList.get(customHeaderView != null ? position - 1 :
        position);

      if(holder instanceof FriendInviteAdapterViewHolder) {
        friendInviteBindItem(jsonObject, (FriendInviteAdapterViewHolder) holder);
      } else if (holder instanceof FriendConfirmAdapterViewHolder){
        friendConfirmBindItem(jsonObject, (FriendConfirmAdapterViewHolder) holder);
      } else if (holder instanceof FriendConfirmedAdapterViewHolder){
        friendConfirmedBindItem(jsonObject, (FriendConfirmedAdapterViewHolder) holder);
      } else if (holder instanceof TimePoliceInviteAdapterViewHolder){
        TPInviteBindItem(jsonObject, (TimePoliceInviteAdapterViewHolder) holder);
      } else if (holder instanceof TimePoliceReplyAdapterViewHolder){
        TPReplyBindItem(jsonObject, (TimePoliceReplyAdapterViewHolder) holder);
      } else if (holder instanceof TimePoliceInvitingAdapterViewHolder){
        TPInvitingBindItem(jsonObject, (TimePoliceInvitingAdapterViewHolder) holder);
      }
    }
  }

  public void friendInviteBindItem(final JSONObject jsonObject, FriendInviteAdapterViewHolder
          holder) {
    try {
      final long id = jsonObject.getLong("id");
      final String name = jsonObject.getString("name");
      holder.mProfileNameTextView.setText(name);

      String url = "https://graph.facebook.com/" + String.valueOf(id) +
        "/picture?type=large";
      Picasso.with(mContext).load(url).into(holder.mProfileImageView);

      Log.d(TAG, jsonObject.toString());
      for (int i = 0; i < friendProfileList.size(); ++i)
        Log.d(TAG, friendProfileList.get(i).toString());

      if(jsonObject.getInt("state") == FRIEND_INVITING_ITEM) {
        holder.mButton.setEnabled(false);
        String str = mContext.getResources().getString(R.string.wait_for_reply);
        holder.mButton.setText(str);
      } else {
        holder.mButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            FetchFriendUtil.friendInvite(id, name);
            int index = indexOf(jsonObject);
            if (index != -1)
              remove(index);
          }
        });
      }

    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  public void friendConfirmBindItem(final JSONObject jsonObject, FriendConfirmAdapterViewHolder
          holder) {
    try {

      final long id = jsonObject.getLong("id");
      final String name = jsonObject.getString("name");
      holder.mProfileNameTextView.setText(name);

      String url = "https://graph.facebook.com/" + String.valueOf(id) +
        "/picture?type=large";
      Picasso.with(mContext).load(url).into(holder.mProfileImageView);
      holder.mButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            FetchFriendUtil.friendInvite(id, name);
          try {
            FetchFriendUtil.friendConfirm(id, name);
          } catch (JSONException e) {
            e.printStackTrace();
          }
          int index = indexOf(jsonObject);
          if (index != -1)
            remove(index);
        }
      });

    } catch (JSONException e) {
      e.printStackTrace();
    }

    if (mDragStartListener != null) {

      holder.item_view.setOnTouchListener(new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
          return false;
        }
      });
    }
  }
  public void friendConfirmedBindItem(final JSONObject jsonObject, FriendConfirmedAdapterViewHolder
    holder) {
    try {

      final long id = jsonObject.getLong("id");
      final String name = jsonObject.getString("name"),
                   objectId = jsonObject.getString("objectId");

      holder.mProfileNameTextView.setText(name);

      String url = "https://graph.facebook.com/" + String.valueOf(id) +
        "/picture?type=large";
      Picasso.with(mContext).load(url).into(holder.mProfileImageView);
      holder.mButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Log.d(TAG, "I've known clicked.");

          GetFriendConfirmService.removeRepliedList(objectId);
          int index = indexOf(jsonObject);
          if (index != -1)
            remove(index);
        }
      });

    } catch (JSONException e) {
      e.printStackTrace();
    }

    if (mDragStartListener != null) {

      holder.item_view.setOnTouchListener(new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
          return false;
        }
      });
    }
  }
  public void TPInviteBindItem(final JSONObject jsonObject, TimePoliceInviteAdapterViewHolder
                          holder) {
    try {

      final long id = jsonObject.getLong("id");
      final String name = jsonObject.getString("name");
      final String objectId = jsonObject.getString("objectId");

      holder.mProfileNameTextView.setText(name);

      String url = "https://graph.facebook.com/" + String.valueOf(id) +
                              "/picture?type=large";
      Picasso.with(mContext).load(url).into(holder.mProfileImageView);
      holder.mButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          TimePoliceUtil.timePoliceReply(true, objectId);
          int index = indexOf(jsonObject);
          if (index != -1)
            remove(index);
        }
      });
      holder.item_view.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent intent;
          intent = new Intent(mContext, ReplyPermissionActivity.class);

          intent.putExtra("user_name", name);
          intent.putExtra("objectId", objectId);
          intent.putExtra("user_id", id);
          mContext.startActivity(intent);

        }
      });

    } catch (JSONException e) {
      e.printStackTrace();
    }
  }
  public void TPReplyBindItem(final JSONObject jsonObject, TimePoliceReplyAdapterViewHolder
                          holder) {
    try {

      final long id = jsonObject.getLong("id");
      final String name = jsonObject.getString("name");
      final String objectId = jsonObject.getString("objectId");

      holder.mProfileNameTextView.setText(name);

      String url = "https://graph.facebook.com/" + String.valueOf(id) +
                              "/picture?type=large";
      Picasso.with(mContext).load(url).into(holder.mProfileImageView);
      holder.mButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Log.d(TAG, "time police reply done");
          GetTimePoliceReplyService.removeReplyList(id, objectId);
          int index = indexOf(jsonObject);
          if (index != -1)
            remove(index);
        }
      });
      holder.item_view.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent intent;
          intent = new Intent(mContext, ModifyPermissionActivity.class);

          intent.putExtra("user_name", name);
          intent.putExtra("user_id", id);
          mContext.startActivity(intent);

        }
      });

    } catch (JSONException e) {
      e.printStackTrace();
    }
  }
  public void TPInvitingBindItem(final JSONObject jsonObject, TimePoliceInvitingAdapterViewHolder
                          holder) {
    try {

      final long id = jsonObject.getLong("id");
      final String name = jsonObject.getString("name");

      holder.mProfileNameTextView.setText(name);

      String url = "https://graph.facebook.com/" + String.valueOf(id) +
                              "/picture?type=large";
      Picasso.with(mContext).load(url).into(holder.mProfileImageView);
      holder.mButton.setEnabled(false);
//      holder.mButton.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//          Log.d(TAG, "time police inviting");
//          int index = indexOf(jsonObject);
//          if (index != -1)
//            remove(index);
//        }
//      });
      holder.item_view.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent intent;
          intent = new Intent(mContext, ModifyPermissionActivity.class);

          intent.putExtra("user_name", name);
          intent.putExtra("user_id", id);
          mContext.startActivity(intent);
        }
      });

    } catch (JSONException e) {
      e.printStackTrace();
    }
  }
  @Override
  public int getAdapterItemCount() {
    if (friendProfileList.isEmpty())
      return 0;
    return friendProfileList.size();
  }


  @Override
  public UltimateRecyclerviewViewHolder getViewHolder(View view) {

    return new UltimateRecyclerviewViewHolder(view);
  }


  @Override
  public int getItemViewType(int position) {

    int type = -1;
    try {
      type = friendProfileList.get(position).getInt("state");
    } catch (JSONException e) {
      Log.v(TAG, e.getMessage());
    }
    Log.v(TAG, "type = " + type);
    return type;
  }

  @Override
  public UltimateRecyclerviewViewHolder onCreateViewHolder(ViewGroup parent) {
    return null;
  }

  @Override
  public UltimateRecyclerviewViewHolder onCreateViewHolder(ViewGroup parent, int i) {

    View v = null;

    if (i == NOT_FRIEND_ITEM || i == FRIEND_INVITING_ITEM) {
      v = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.friend_invite_recycler_view_adapter, parent, false);
      final FriendInviteAdapterViewHolder vh = new FriendInviteAdapterViewHolder(v, true);

      if (v != null) {
        v.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Toast.makeText(v.getContext(), "inside viewholder position = " + vh.getAdapterPosition(), Toast
              .LENGTH_SHORT)
              .show();
          }
        });
      }
      return vh;

    } else if (i == FRIEND_INVITED_ITEM) {
      v = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.friend_confirm_recycler_view_adapter, parent, false);
      final FriendConfirmAdapterViewHolder vh = new FriendConfirmAdapterViewHolder(v, true);
      if (v != null) {
        v.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Toast.makeText(v.getContext(), "inside viewholder position = " + vh.getAdapterPosition(), Toast
              .LENGTH_SHORT)
              .show();
          }
        });
      }
      return vh;
    } else if (i == FRIEND_CONFIRMED_ITEM) {
      v = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.friend_confirmed_recycler_view_adapter, parent, false);
      final FriendConfirmedAdapterViewHolder vh = new FriendConfirmedAdapterViewHolder(v, true);
      if (v != null) {
        v.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Toast.makeText(v.getContext(), "inside viewholder position = " + vh.getAdapterPosition(), Toast
              .LENGTH_SHORT)
              .show();
          }
        });
      }
      return vh;
    } else if (i == TP_INVITE_ITEM) {
      v = LayoutInflater.from(parent.getContext())
                              .inflate(R.layout.time_police_invite_recycler_view_adapter, parent, false);
      final TimePoliceInviteAdapterViewHolder vh = new TimePoliceInviteAdapterViewHolder(v, true);
      if (v != null) {
//        v.setOnClickListener(new View.OnClickListener() {
//          @Override
//          public void onClick(View v) {
//            Toast.makeText(v.getContext(), "inside viewholder position = " + vh.getAdapterPosition(), Toast
//                                    .LENGTH_SHORT)
//                                    .show();
//          }
//        });
      }
      return vh;
    } else if (i == TP_REPLY_ITEM) {
      v = LayoutInflater.from(parent.getContext())
                              .inflate(R.layout.time_police_reply_recycler_view_adapter, parent,
                                                      false);
      final TimePoliceReplyAdapterViewHolder vh = new TimePoliceReplyAdapterViewHolder(v, true);
      if (v != null) {
//        v.setOnClickListener(new View.OnClickListener() {
//          @Override
//          public void onClick(View v) {
//            Toast.makeText(v.getContext(), "inside viewholder position = " + vh.getAdapterPosition(), Toast
//                                    .LENGTH_SHORT)
//                                    .show();
//          }
//        });
      }
      return vh;
    }else if (i == TP_INVITING_ITEM) {
      v = LayoutInflater.from(parent.getContext())
                              .inflate(R.layout.time_police_inviting_recycler_view_adapter, parent,
                                                      false);
      final TimePoliceInvitingAdapterViewHolder vh = new TimePoliceInvitingAdapterViewHolder(v,
                              true);
      if (v != null) {
//        v.setOnClickListener(new View.OnClickListener() {
//          @Override
//          public void onClick(View v) {
//            Toast.makeText(v.getContext(), "inside viewholder position = " + vh.getAdapterPosition(), Toast
//                                    .LENGTH_SHORT)
//                                    .show();
//          }
//        });
      }
      return vh;
    }
    return null;
  }

  public void insert(JSONObject jsonObject, int position) {
    insert(friendProfileList, jsonObject, position);
  }

  public void remove(int position) {
    remove(friendProfileList, position);
  }

  public int indexOf(JSONObject jsonObject) {
    return friendProfileList.indexOf(jsonObject);
  }

  public void clear() {
    clear(friendProfileList);
  }

  @Override
  public void toggleSelection(int pos) {
    super.toggleSelection(pos);
  }

  @Override
  public void setSelected(int pos) {
    super.setSelected(pos);
  }

  @Override
  public void clearSelection(int pos) {
    super.clearSelection(pos);
  }


  public void swapPositions(int from, int to) {
    swapPositions(friendProfileList, from, to);
  }


  @Override
  public long generateHeaderId(int position) {
    // URLogs.d("position--" + position + "   " + getItem(position));
    if (getItem(position).length() > 0) {
//      return getItem(position).charAt(0);
      return 0;
    } else return -1;
  }

  @Override

  public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup viewGroup) {

    return null;
  }

  @Override
  public void onBindHeaderViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

  }

  @Override
  public void onItemMove(int fromPosition, int toPosition) {
    swapPositions(fromPosition, toPosition);
//        notifyItemMoved(fromPosition, toPosition);
    super.onItemMove(fromPosition, toPosition);
  }

  @Override
  public void onItemDismiss(int position) {
    remove(position);
    // notifyItemRemoved(position);
//        notifyDataSetChanged();
    super.onItemDismiss(position);
  }
//
//    private int getRandomColor() {
//        SecureRandom rgen = new SecureRandom();
//        return Color.HSVToColor(150, new float[]{
//                rgen.nextInt(359), 1, 1
//        });
//    }

//  public void setOnDragStartListener(OnStartDragListener dragStartListener) {
//    mDragStartListener = dragStartListener;
//  }


  public class FriendInviteAdapterViewHolder extends UltimateRecyclerviewViewHolder {

    TextView mProfileNameTextView;
    ImageView mProfileImageView;
    Button mButton;
    View item_view;


    public FriendInviteAdapterViewHolder(View itemView, boolean isItem) {
      super(itemView);
      if (isItem) {
        mProfileNameTextView = (TextView) itemView.findViewById(
          R.id.profile_name);
        mProfileImageView = (ImageView) itemView.findViewById(R.id.profile_image);
        mButton = (Button) itemView.findViewById(R.id.button);

        item_view = itemView.findViewById(R.id.itemview);
      }
    }

    @Override
    public void onItemSelected() {
//      itemView.setBackgroundColor(Color.DKGRAY);
    }

    @Override
    public void onItemClear() {
//      itemView.setBackgroundColor(0);
    }
  }

  public JSONObject getItem(int position) {
    if (customHeaderView != null) position--;
    if (position < friendProfileList.size())
      return friendProfileList.get(position);
    else return null;
  }


  public class FriendConfirmAdapterViewHolder extends UltimateRecyclerviewViewHolder {

    TextView mProfileNameTextView;
    ImageView mProfileImageView;
    Button mButton;
    View item_view;


    public FriendConfirmAdapterViewHolder(View itemView, boolean isItem) {
      super(itemView);
      if (isItem) {
        Log.v(TAG, "ConfirmViewHolder Created");
        mProfileNameTextView = (TextView) itemView.findViewById(
          R.id.profile_name);
        mProfileImageView = (ImageView) itemView.findViewById(R.id.profile_image);
        mButton = (Button) itemView.findViewById(R.id.button);

        item_view = itemView.findViewById(R.id.itemview);
      }
    }

    @Override
    public void onItemSelected() {
//      itemView.setBackgroundColor(Color.DKGRAY);
    }

    @Override
    public void onItemClear() {
//      itemView.setBackgroundColor(0);
    }
  }
  public class FriendConfirmedAdapterViewHolder extends UltimateRecyclerviewViewHolder {

    TextView mProfileNameTextView;
    ImageView mProfileImageView;
    Button mButton;
    View item_view;


    public FriendConfirmedAdapterViewHolder(View itemView, boolean isItem) {
      super(itemView);
      if (isItem) {
        Log.v(TAG, "ConfirmedViewHolder Created");
        mProfileNameTextView = (TextView) itemView.findViewById(
          R.id.profile_name);
        mProfileImageView = (ImageView) itemView.findViewById(R.id.profile_image);
        mButton = (Button) itemView.findViewById(R.id.button);


        item_view = itemView.findViewById(R.id.itemview);
      }
    }

    @Override
    public void onItemSelected() {
//      itemView.setBackgroundColor(Color.DKGRAY);
    }

    @Override
    public void onItemClear() {
//      itemView.setBackgroundColor(0);
    }
  }
  public class TimePoliceInviteAdapterViewHolder extends UltimateRecyclerviewViewHolder {

    TextView mProfileNameTextView;
    ImageView mProfileImageView;
    Button mButton;
    View item_view;


    public TimePoliceInviteAdapterViewHolder(View itemView, boolean isItem) {
      super(itemView);
      if (isItem) {
        Log.v(TAG, "TimePoliceInviteAdapterViewHolder Created");
        mProfileNameTextView = (TextView) itemView.findViewById(
                                R.id.profile_name);
        mProfileImageView = (ImageView) itemView.findViewById(R.id.profile_image);
        mButton = (Button) itemView.findViewById(R.id.button);


        item_view = itemView.findViewById(R.id.itemview);
      }
    }

    @Override
    public void onItemSelected() {
//      itemView.setBackgroundColor(Color.DKGRAY);
    }

    @Override
    public void onItemClear() {
//      itemView.setBackgroundColor(0);
    }
  }
  public class TimePoliceReplyAdapterViewHolder extends UltimateRecyclerviewViewHolder {

    TextView mProfileNameTextView;
    ImageView mProfileImageView;
    Button mButton;
    View item_view;


    public TimePoliceReplyAdapterViewHolder(View itemView, boolean isItem) {
      super(itemView);
      if (isItem) {
        Log.v(TAG, "TimePoliceReplyAdapterViewHolder Created");
        mProfileNameTextView = (TextView) itemView.findViewById(
                                R.id.profile_name);
        mProfileImageView = (ImageView) itemView.findViewById(R.id.profile_image);
        mButton = (Button) itemView.findViewById(R.id.button);


        item_view = itemView.findViewById(R.id.itemview);
      }
    }

    @Override
    public void onItemSelected() {
//      itemView.setBackgroundColor(Color.DKGRAY);
    }

    @Override
    public void onItemClear() {
//      itemView.setBackgroundColor(0);
    }
  }
  public class TimePoliceInvitingAdapterViewHolder extends UltimateRecyclerviewViewHolder {

    TextView mProfileNameTextView;
    ImageView mProfileImageView;
    Button mButton;
    View item_view;


    public TimePoliceInvitingAdapterViewHolder(View itemView, boolean isItem) {
      super(itemView);
      if (isItem) {
        Log.v(TAG, "TimePoliceInvitingAdapterViewHolder Created");
        mProfileNameTextView = (TextView) itemView.findViewById(
                                R.id.profile_name);
        mProfileImageView = (ImageView) itemView.findViewById(R.id.profile_image);
        mButton = (Button) itemView.findViewById(R.id.button);

        item_view = itemView.findViewById(R.id.itemview);
      }
    }

    @Override
    public void onItemSelected() {
//      itemView.setBackgroundColor(Color.DKGRAY);
    }

    @Override
    public void onItemClear() {
//      itemView.setBackgroundColor(0);
    }
  }
}
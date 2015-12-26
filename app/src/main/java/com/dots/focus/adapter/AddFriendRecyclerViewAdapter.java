package com.dots.focus.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dots.focus.R;
import com.dots.focus.util.FetchFriendUtil;
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
  private static final int FRIEND_INVITE_ITEM = 0;
  private static final int FRIEND_CONFIRM_ITEM = 1;

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
        friendInviteBindItem(jsonObject, (FriendInviteAdapterViewHolder) holder, position);
      } else if (holder instanceof FriendConfirmAdapterViewHolder){
        friendConfirmBindItem(jsonObject, (FriendInviteAdapterViewHolder) holder, position);
      }
    }
  }

  public void friendInviteBindItem(JSONObject jsonObject, FriendInviteAdapterViewHolder holder,
                                   final int position) {
    try {
      holder.textViewSample.setText(jsonObject.getString("name"));

      final long id = jsonObject.getLong("id");
      final String name = jsonObject.getString("name");
      String url = "https://graph.facebook.com/" + String.valueOf(id) +
        "/picture?type=large";
      Picasso.with(mContext).load(url).into(holder.imageViewSample);
      holder.buttonSample.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            FetchFriendUtil.friendInvite(id, name);

          FetchFriendUtil.friendInvite(id, name);

          remove(position);
        }
      });

    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  public void friendConfirmBindItem(JSONObject jsonObject, FriendInviteAdapterViewHolder holder,
                                   final int position) {
    try {
      holder.textViewSample.setText(jsonObject.getString("name"));

      final long id = jsonObject.getLong("id");
      final String name = jsonObject.getString("name");
      String url = "https://graph.facebook.com/" + String.valueOf(id) +
        "/picture?type=large";
      Picasso.with(mContext).load(url).into(holder.imageViewSample);
      holder.buttonSample.setText("接受");
      holder.buttonSample.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            FetchFriendUtil.friendInvite(id, name);
          try {
            FetchFriendUtil.friendConfirm(id, name);
          } catch (JSONException e) {
            e.printStackTrace();
          }
          remove(position);
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

    Boolean isInvite = null;
    try {
      isInvite = friendProfileList.get(position).getBoolean("invite");
    } catch (JSONException e) {
      e.printStackTrace();
    }

    return isInvite ? FRIEND_CONFIRM_ITEM : FRIEND_INVITE_ITEM;
  }


  @Override
  public FriendInviteAdapterViewHolder onCreateViewHolder(ViewGroup parent) {
    return null;
  }

  @Override
  public FriendInviteAdapterViewHolder onCreateViewHolder(ViewGroup parent, int i) {

    View v = null;

    if (i == FRIEND_INVITE_ITEM) {
      v = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.friend_invite_recycler_view_adapter, parent, false);
    } else if (i == FRIEND_CONFIRM_ITEM) {
      v = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.friend_confirm_recycler_view_adapter, parent, false);
    }

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
  }

  public void insert(JSONObject jsonObject, int position) {
    insert(friendProfileList, jsonObject, position);
  }

  public void remove(int position) {
    remove(friendProfileList, position);
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

    TextView textViewSample;
    ImageView imageViewSample;
    Button buttonSample;
    View item_view;


    public FriendInviteAdapterViewHolder(View itemView, boolean isItem) {
      super(itemView);
      if (isItem) {
        textViewSample = (TextView) itemView.findViewById(
          R.id.textview);
        imageViewSample = (ImageView) itemView.findViewById(R.id.imageview);
        buttonSample = (Button) itemView.findViewById(R.id.button_invite_friend);

        buttonSample.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            // call function
          }
        });

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
    if (customHeaderView != null)
      position--;
    if (position < friendProfileList.size())
      return friendProfileList.get(position);
    else return null;
  }


  public class FriendConfirmAdapterViewHolder extends UltimateRecyclerviewViewHolder {

    TextView textViewSample;
    ImageView imageViewSample;
    Button buttonSample;
    View item_view;


    public FriendConfirmAdapterViewHolder(View itemView, boolean isItem) {
      super(itemView);
      if (isItem) {
        textViewSample = (TextView) itemView.findViewById(
          R.id.textview);
        imageViewSample = (ImageView) itemView.findViewById(R.id.imageview);
        buttonSample = (Button) itemView.findViewById(R.id.button_invite_friend);

        buttonSample.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            // call function
          }
        });

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
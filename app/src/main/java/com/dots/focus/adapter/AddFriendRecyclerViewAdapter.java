package com.dots.focus.adapter;

/**
 * Created by AdrianHsu on 2015/12/12.
 */

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
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class AddFriendRecyclerViewAdapter extends UltimateViewAdapter<AddFriendRecyclerViewAdapter.SimpleAdapterViewHolder> {

  private ArrayList<JSONObject> friendProfileList;
  private Context mContext;

  public AddFriendRecyclerViewAdapter(ArrayList<JSONObject> friendProfileList, Context context) {
    this.friendProfileList = friendProfileList;
    mContext = context;
  }


  @Override
  public void onBindViewHolder(final SimpleAdapterViewHolder holder, int position) {
    if (position < getItemCount() && (customHeaderView != null ? position <= friendProfileList.size() : position
      < friendProfileList.size()) && (customHeaderView != null ? position > 0 : true)) {

      JSONObject jsonObject = friendProfileList.get(customHeaderView != null ? position - 1 :
        position);
      try {
        holder.textViewSample.setText(jsonObject.getString("name"));

        String url ="https://graph.facebook.com/" + String.valueOf( jsonObject.getLong
          ("id") )+
          "/picture?type=large";
        Picasso.with(mContext).load(url).into(holder.imageViewSample);

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
  }

  @Override
  public int getAdapterItemCount() {
    if(friendProfileList.isEmpty())
      return 0;
    return friendProfileList.size();
  }

  @Override
  public SimpleAdapterViewHolder getViewHolder(View view) {
    return new SimpleAdapterViewHolder(view, false);
  }

  @Override
  public SimpleAdapterViewHolder onCreateViewHolder(ViewGroup parent) {
    View v = LayoutInflater.from(parent.getContext())
      .inflate(R.layout.add_friend_recycler_view_adapter, parent, false);
    final SimpleAdapterViewHolder vh = new SimpleAdapterViewHolder(v, true);

    v.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Toast.makeText(v.getContext(), "inside viewholder position = " + vh.getAdapterPosition(), Toast
          .LENGTH_SHORT)
          .show();
      }
    });
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
    }
    else return -1;
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


  public class SimpleAdapterViewHolder extends UltimateRecyclerviewViewHolder {

    TextView textViewSample;
    ImageView imageViewSample;
    Button buttonSample;
    View item_view;

    public  SimpleAdapterViewHolder(View itemView, boolean isItem) {
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

}
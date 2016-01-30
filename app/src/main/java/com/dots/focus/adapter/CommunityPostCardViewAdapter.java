package com.dots.focus.adapter;

/**
 * Created by AdrianHsu on 2015/12/12.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dots.focus.R;
import com.dots.focus.ui.FocusCommunityCommentsActivity;
import com.dots.focus.ui.KickMessagesActivity;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class CommunityPostCardViewAdapter extends UltimateViewAdapter<CommunityPostCardViewAdapter
  .SimpleAdapterViewHolder> {

  private List<JSONObject> jsonObjectList;
  private Context mContext;
  private boolean pushBtnBoolean = true;

  public CommunityPostCardViewAdapter(List<JSONObject> jsonObjectList, Context context) {
    this.jsonObjectList = jsonObjectList;
    this.mContext = context;
  }


  @Override
  public void onBindViewHolder(final SimpleAdapterViewHolder holder, final int position) {
    if (position < getItemCount() && (customHeaderView != null ? position <= jsonObjectList.size() : position
                            < jsonObjectList.size()) && (customHeaderView != null ? position > 0 : true)) {
      
      String content = null;
      try {
        content = jsonObjectList.get(position).getString("content");
      } catch(JSONException e) {
        e.printStackTrace();
      }
      holder.contentTv.setText(content);
    }
  }

  @Override
  public int getAdapterItemCount() {
    return jsonObjectList.size();
  }

  @Override
  public SimpleAdapterViewHolder getViewHolder(View view) {
    return new SimpleAdapterViewHolder(view, false);
  }

  @Override
  public SimpleAdapterViewHolder onCreateViewHolder(ViewGroup parent) {
    View v = LayoutInflater.from(parent.getContext())
      .inflate(R.layout.focus_community_recycler_view_adapter, parent, false);
    final SimpleAdapterViewHolder vh = new SimpleAdapterViewHolder(v, true);

    final ImageView pushView = (ImageView) v.findViewById(R.id.push_up);
    v.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Toast.makeText(v.getContext(), "clicked" + vh.getAdapterPosition(), Toast
                                .LENGTH_SHORT)
          .show();
        Intent intent;
        intent = new Intent(mContext, FocusCommunityCommentsActivity.class);
        mContext.startActivity(intent);

      }
    });
    pushView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Toast.makeText(v.getContext(), "clicked push on adapter #" + vh.getAdapterPosition(),
                                Toast
                                .LENGTH_SHORT)
                                .show();

        Drawable res;
        if(pushBtnBoolean)
          res = mContext.getResources().getDrawable(R.drawable.community_up_button_white, null);
        else
          res = mContext.getResources().getDrawable(R.drawable.community_up_button_yellow, null);
        pushView.setImageDrawable(res);
        pushBtnBoolean = !pushBtnBoolean;
      }
    });


    return vh;
  }


  public void insert(JSONObject jsonObject, int position) {
    insert(jsonObjectList, jsonObject, position);
  }

  public void remove(int position) {
    remove(jsonObjectList, position);
  }

  public void clear() {
    clear(jsonObjectList);
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
    swapPositions(jsonObjectList, from, to);
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

    TextView userNameTv;
    TextView timeTv;
    TextView contentTv;
    TextView pointTv;
    TextView commentTv;
    ImageView profileIv;
    ImageView pushIv;
    View item_view;

    public  SimpleAdapterViewHolder(View itemView, boolean isItem) {
      super(itemView);
      if (isItem) {
        userNameTv = (TextView) itemView.findViewById(
          R.id.user_name);
        timeTv = (TextView) itemView.findViewById(R.id.time);
        contentTv = (TextView) itemView.findViewById(R.id.content);
        pointTv = (TextView) itemView.findViewById(R.id.points);
        commentTv = (TextView) itemView.findViewById(R.id.comment);
        profileIv = (ImageView) itemView.findViewById(R.id.profile_image);
        pushIv = (ImageView) itemView.findViewById(R.id.push_up);

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
    if (position < jsonObjectList.size())
      return jsonObjectList.get(position);
    else return null;
  }

}
package com.dots.focus.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dots.focus.R;
import com.dots.focus.service.GetAppsService;
import com.dots.focus.service.GetFriendConfirmService;
import com.dots.focus.service.GetFriendInviteService;
import com.dots.focus.service.GetKickRequestService;
import com.dots.focus.service.GetKickResponseService;
import com.dots.focus.service.GetKickedService;
import com.dots.focus.service.GetTimePoliceCancelOrDeleteService;
import com.dots.focus.service.GetTimePoliceInviteService;
import com.dots.focus.service.GetTimePoliceReplyService;
import com.dots.focus.service.TrackAccessibilityService;
import com.dots.focus.ui.AdvancedSettingsActivity;
import com.dots.focus.ui.FocusModeHistoryActivity;
import com.dots.focus.ui.GoalSettingsActivity;
import com.dots.focus.ui.IdleSettingsActivity;
import com.dots.focus.ui.IntroActivity;
import com.dots.focus.ui.LockSettingsActivity;
import com.dots.focus.ui.LoginActivity;
import com.dots.focus.ui.NotificationSettingsActivity;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;


public class MoreRecyclerViewAdapter extends UltimateViewAdapter<MoreRecyclerViewAdapter.SimpleAdapterViewHolder> {

  private List<String> stringList;
  private Context mContext = null;

  public MoreRecyclerViewAdapter(List<String> stringList) {
    this.stringList = stringList;
  }


  @Override
  public void onBindViewHolder(final SimpleAdapterViewHolder holder, int position) {
    if (position < getItemCount() && (customHeaderView != null ? position <= stringList.size() : position < stringList.size()) && (customHeaderView != null ? position > 0 : true)) {

      holder.textViewSample.setText(stringList.get(customHeaderView != null ? position - 1 : position));
      switch(position) {
        case 0:
          Picasso.with(mContext).load(R.drawable.more_focus_mode).into(holder.imageViewSample);
          break;
        case 1:
          Picasso.with(mContext).load(R.drawable.more_goal_settings).into(holder.imageViewSample);
          break;
        case 2:
          Picasso.with(mContext).load(R.drawable.more_kick_settings).into(holder.imageViewSample);
          break;
        case 3:
          Picasso.with(mContext).load(R.drawable.more_lock_settings).into(holder.imageViewSample);
          break;
        case 4:
          Picasso.with(mContext).load(R.drawable.more_noti_settings).into(holder.imageViewSample);
          break;
//        case 5:
//          Picasso.with(mContext).load(R.drawable.more_focus_community).into(holder.imageViewSample);
//          break;
//        case 6:
//          Picasso.with(mContext).load(R.drawable.more_parental_control).into(holder.imageViewSample);
//          break;
        case 5:
          Picasso.with(mContext).load(R.drawable.more_advanced_settings).into(holder.imageViewSample);
          break;
        case 6:
          Picasso.with(mContext).load(R.drawable.more_logout).into(holder.imageViewSample);
          break;

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
    return stringList.size();
  }

  @Override
  public SimpleAdapterViewHolder getViewHolder(View view) {
    return new SimpleAdapterViewHolder(view, false);
  }

  @Override
  public SimpleAdapterViewHolder onCreateViewHolder(ViewGroup parent) {
    View v = LayoutInflater.from(parent.getContext())
      .inflate(R.layout.more_recycler_view_adapter, parent, false);
    final SimpleAdapterViewHolder vh = new SimpleAdapterViewHolder(v, true);

    mContext = parent.getContext();

    v.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
//        Toast.makeText(v.getContext(), "inside viewholder position = " + vh.getAdapterPosition(), Toast
//                .LENGTH_SHORT)
//                .show();
        Intent intent = null;
        switch (vh.getAdapterPosition()) {
          case 0:
            intent = new Intent(mContext, FocusModeHistoryActivity.class);
            break;
          case 1:
            intent = new Intent(mContext, GoalSettingsActivity.class);
            break;
          case 2:
            intent = new Intent(mContext, IdleSettingsActivity.class);
            break;
          case 3:
            intent = new Intent(mContext, LockSettingsActivity.class);
            break;
          case 4:
            intent = new Intent(mContext, NotificationSettingsActivity.class);
            break;
//          case 5:
//            intent = new Intent(mContext, FocusCommunityActivity.class);
//            break;
          case 5: // 7
            intent = new Intent(mContext, AdvancedSettingsActivity.class);
            break;
          case 6:
            logOut();
            break;
        }
        if (intent != null)
          mContext.startActivity(intent);
      }
    });
    return vh;
  }
  private void logOut() {
    // delete the data...?
    stopServices();
    ParseUser.logOut();
    TrackAccessibilityService.logOut();

    if (!IntroActivity.checkLogin(mContext)) {
      mContext.startActivity(new Intent(mContext, LoginActivity.class));
    }
  }
  private void stopServices() {
//    mContext.stopService(new Intent(mContext, TrackAccessibilityService.class));

    mContext.stopService(new Intent(mContext, GetAppsService.class));

    mContext.stopService(new Intent(mContext, GetFriendInviteService.class));
    mContext.stopService(new Intent(mContext, GetFriendConfirmService.class));

    mContext.stopService(new Intent(mContext, GetKickRequestService.class));
    mContext.stopService(new Intent(mContext, GetKickedService.class));
    mContext.stopService(new Intent(mContext, GetKickResponseService.class));

    mContext.stopService(new Intent(mContext, GetTimePoliceInviteService.class));
    mContext.stopService(new Intent(mContext, GetTimePoliceReplyService.class));
    mContext.stopService(new Intent(mContext, GetTimePoliceCancelOrDeleteService.class));
  }

  public void insert(String string, int position) {
    insert(stringList, string, position);
  }

  public void remove(int position) {
    remove(stringList, position);
  }

  public void clear() {
    clear(stringList);
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
    swapPositions(stringList, from, to);
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
    View item_view;

    public  SimpleAdapterViewHolder(View itemView, boolean isItem) {
      super(itemView);
      if (isItem) {
        textViewSample = (TextView) itemView.findViewById(
          R.id.textview);
        imageViewSample = (ImageView) itemView.findViewById(R.id.imageview);
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

  public String getItem(int position) {
    if (customHeaderView != null)
      position--;
    if (position < stringList.size())
      return stringList.get(position);
    else return "";
  }

}
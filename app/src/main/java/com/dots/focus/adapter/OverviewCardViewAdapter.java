package com.dots.focus.adapter;

/**
 * Created by AdrianHsu on 2015/12/12.
 */

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dots.focus.R;
import com.dots.focus.ui.AppLeaderBoardChartActivity;
import com.dots.focus.ui.DailyAppUsageChartActivity;
import com.dots.focus.ui.RadarChartActivity;
import com.dots.focus.ui.TopThreeAppUsageChartActivity;
import com.dots.focus.ui.WeeklyAddictionIndexChartActivity;
import com.dots.focus.ui.WeeklyAppUsageChartActivity;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;

import java.util.List;


public class OverviewCardViewAdapter extends UltimateViewAdapter<OverviewCardViewAdapter
  .SimpleAdapterViewHolder> {

  private List<String> stringList;

  public OverviewCardViewAdapter(List<String> stringList) {
    this.stringList = stringList;
  }


  @Override
  public void onBindViewHolder(final SimpleAdapterViewHolder holder, int position) {
    if (position < getItemCount() && (customHeaderView != null ? position <= stringList.size() : position < stringList.size()) && (customHeaderView != null ? position > 0 : true)) {

      holder.textViewSample.setText(stringList.get(customHeaderView != null ? position - 1 : position));

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
      .inflate(R.layout.overview_recycler_view_adapter, parent, false);
    final SimpleAdapterViewHolder vh = new SimpleAdapterViewHolder(v, true);

    v.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Toast.makeText(v.getContext(), "inside viewholder position = " + vh.getAdapterPosition(), Toast
          .LENGTH_SHORT)
          .show();
        Intent intent = null;
        if(vh.getAdapterPosition() == 0)
          intent = new Intent(v.getContext(), WeeklyAppUsageChartActivity.class);
        else if (vh.getAdapterPosition() == 1)
          intent = new Intent(v.getContext(), DailyAppUsageChartActivity.class);
        else if (vh.getAdapterPosition() == 2)
          intent = new Intent(v.getContext(), WeeklyAddictionIndexChartActivity.class);
        else if (vh.getAdapterPosition() == 3)
          intent = new Intent(v.getContext(), TopThreeAppUsageChartActivity.class);
        else if (vh.getAdapterPosition() == 4)
          intent = new Intent(v.getContext(), AppLeaderBoardChartActivity.class);
        else if (vh.getAdapterPosition() == 6)
          intent = new Intent(v.getContext(), RadarChartActivity.class);
        else {
          intent = null;
        }

        v.getContext().startActivity(intent);
      }
    });
    return vh;
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
    View item_view;

    public  SimpleAdapterViewHolder(View itemView, boolean isItem) {
      super(itemView);
      if (isItem) {
        textViewSample = (TextView) itemView.findViewById(
          R.id.textview);
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
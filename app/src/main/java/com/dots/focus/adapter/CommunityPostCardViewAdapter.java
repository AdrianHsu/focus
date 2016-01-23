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
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dots.focus.R;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;

import java.util.List;


public class CommunityPostCardViewAdapter extends UltimateViewAdapter<CommunityPostCardViewAdapter
  .SimpleAdapterViewHolder> {

  private List<String> stringList;
  private Context mContext;

  public CommunityPostCardViewAdapter(List<String> stringList, Context context) {
    this.stringList = stringList;
    this.mContext = context;
  }


  @Override
  public void onBindViewHolder(final SimpleAdapterViewHolder holder, final int position) {
    if (position < getItemCount() && (customHeaderView != null ? position <= stringList.size() : position < stringList.size()) && (customHeaderView != null ? position > 0 : true)) {

      holder.textViewSample.setText(stringList.get(customHeaderView != null ? position - 1 : position));

      if(position == 4)
        holder.textViewContent.setText("出生年份");
      else if (position == 5)
        holder.textViewContent.setText("好友總數");
      else if (position == 6)
        holder.textViewContent.setText("FOCUS省下總時數");

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
      .inflate(R.layout.focus_community_recycler_view_adapter, parent, false);
    final SimpleAdapterViewHolder vh = new SimpleAdapterViewHolder(v, true);

    final TextView textview = (TextView) v.findViewById(R.id.textview);
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
    TextView textViewContent;
    View item_view;

    public  SimpleAdapterViewHolder(View itemView, boolean isItem) {
      super(itemView);
      if (isItem) {
        textViewSample = (TextView) itemView.findViewById(
          R.id.textview);
        textViewContent = (TextView) itemView.findViewById(R.id.content);
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
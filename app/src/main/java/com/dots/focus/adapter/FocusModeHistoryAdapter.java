package com.dots.focus.adapter;

/**
 * Created by AdrianHsu on 2015/12/12.
 */

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dots.focus.R;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class FocusModeHistoryAdapter extends UltimateViewAdapter<FocusModeHistoryAdapter.FocusModeAdapterViewHolder> {

  private List<JSONObject> jsonObjectList;
  private Context mContext;

  public FocusModeHistoryAdapter(List<JSONObject> jsonObjectList, Context context) {
    this.jsonObjectList = jsonObjectList;
    this.mContext = context;
  }


  @Override
  public void onBindViewHolder(final FocusModeAdapterViewHolder holder, final int position) {
    if (position < getItemCount() && (customHeaderView != null ? position <= jsonObjectList.size() : position
                            < jsonObjectList.size()) && (customHeaderView != null ? position > 0 : true)) {

      JSONObject jsonObject = jsonObjectList.get(customHeaderView != null ? position - 1 :
                              position);

      FocusModeBindItem(jsonObject, holder, position);
    }
  }

  @Override
  public int getAdapterItemCount() {
    return jsonObjectList.size();
  }

  public void FocusModeBindItem(JSONObject jsonObject, FocusModeAdapterViewHolder holder,
                           final int position) {
    String content = null;
    try {
      content = jsonObject.getString("content");
    } catch(JSONException e) {
      e.printStackTrace();
    }
    holder.contentTv.setText(content);
  }


  @Override
  public FocusModeAdapterViewHolder getViewHolder(View view) {
    return new FocusModeAdapterViewHolder(view, false);
  }

  @Override
  public FocusModeAdapterViewHolder onCreateViewHolder(ViewGroup parent) {
    View v;

    v = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.focus_mode_recycler_view_adapter, parent, false);
    final FocusModeAdapterViewHolder vh = new FocusModeAdapterViewHolder(v, true);

    v.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Toast.makeText(v.getContext(), "clicked" + vh.getAdapterPosition(), Toast
                                .LENGTH_SHORT)
                                .show();

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

  public class FocusModeAdapterViewHolder extends UltimateRecyclerviewViewHolder {

    TextView userNameTv;
    TextView timeTv;
    TextView contentTv;
    ImageView profileIv;
    View item_view;

    public FocusModeAdapterViewHolder(View itemView, boolean isItem) {
      super(itemView);
      if (isItem) {
        userNameTv = (TextView) itemView.findViewById(
                                R.id.user_name);
        timeTv = (TextView) itemView.findViewById(R.id.time);
        contentTv = (TextView) itemView.findViewById(R.id.content);
        profileIv = (ImageView) itemView.findViewById(R.id.profile_image);

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
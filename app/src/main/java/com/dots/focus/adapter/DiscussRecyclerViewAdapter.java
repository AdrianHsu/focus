package com.dots.focus.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dots.focus.R;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DiscussRecyclerViewAdapter extends
  UltimateViewAdapter<DiscussRecyclerViewAdapter.SimpleAdapterViewHolder> {

  private ArrayList<JSONObject> messagesList;
  private Context mContext;
  private LinearLayout wrapper;

  private static final String TAG = "messages";

  public DiscussRecyclerViewAdapter(ArrayList<JSONObject> _messagesList, Context context) {
    messagesList = _messagesList;
    mContext = context;
  }


  @Override
  public void onBindViewHolder(final SimpleAdapterViewHolder holder, int position) {
    if (position < getItemCount() && (customHeaderView != null ? position <= messagesList.size() :
                            position < messagesList.size()) && (customHeaderView != null ? position
                            > 0
                            : true)) {
//      try {
//        holder.textViewSample.setText(tmp.getString("duration"));
//      } catch (JSONException e) {
//        e.printStackTrace();
//      }

      wrapper = (LinearLayout) holder.item_view;

      int tmp = position % 2;

      holder.textViewSample.setBackgroundResource(tmp == 0 ? R.drawable.bubble_yellow : R.drawable
                              .bubble_green);

      String text = null;
      try {
        text = messagesList.get(position).getString("text");
      } catch (JSONException e) {
        e.printStackTrace();
      }

      holder.textViewSample.setText(text);
      wrapper.setGravity(tmp == 0 ? Gravity.LEFT : Gravity.RIGHT);

    }
  }

  @Override
  public int getAdapterItemCount() {
    return messagesList.size();
  }

  @Override
  public SimpleAdapterViewHolder getViewHolder(View view) {
    return new SimpleAdapterViewHolder(view, false);
  }

  @Override
  public SimpleAdapterViewHolder onCreateViewHolder(ViewGroup parent) {
    View v = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.messages_recycler_view_adapter, parent,
                                                    false);
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


  public void insert(JSONObject messages, int position) {
    insert(messagesList, messages, position);
  }

  public void remove(int position) {
    remove(messagesList, position);
  }

  public void clear() {
    clear(messagesList);
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
    swapPositions(messagesList, from, to);
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
                                R.id.comment);
        item_view = itemView.findViewById(R.id.wrapper);
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
    if (position < messagesList.size())
      return messagesList.get(position);
    else return null;
  }

}
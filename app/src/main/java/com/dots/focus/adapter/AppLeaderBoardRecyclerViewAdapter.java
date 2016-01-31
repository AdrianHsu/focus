package com.dots.focus.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dots.focus.R;
import com.dots.focus.model.AppInfo;
import com.dots.focus.ui.AppLeaderBoardChartActivity;
import com.dots.focus.util.FetchAppUtil;
import com.github.mikephil.charting.data.Entry;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class AppLeaderBoardRecyclerViewAdapter extends
        UltimateViewAdapter<AppLeaderBoardRecyclerViewAdapter.SimpleAdapterViewHolder> {

    private ArrayList<Entry> indexList;
    private Context mContext;

    private static final String TAG = "index";

    public AppLeaderBoardRecyclerViewAdapter(ArrayList<Entry> _indexList, Context context) {
        indexList = _indexList;
        mContext = context;
    }


    @Override
    public void onBindViewHolder(final SimpleAdapterViewHolder holder, int position) {
        if (position < getItemCount() && (customHeaderView != null ? position <= indexList.size() :
                position < indexList.size()) && (customHeaderView == null || position > 0)) {

          Entry e = indexList.get(position);
          int index = e.getXIndex();
          AppInfo mAppInfo = FetchAppUtil.getApp(index);
          Drawable mIcon = mAppInfo.getIcon();

          holder.appTimeTv.setText(timeToString((int) e.getVal()));
          holder.appIconIv.setImageDrawable(mIcon);
          String rank = String.valueOf(position + 1);
          holder.rankingTv.setText(rank);


          if(position == 0)
              holder.appTimeTv.setTextColor(ContextCompat.getColor(mContext, R.color
                                      .top_three_first));
          else if(position == 1)
              holder.appTimeTv.setTextColor(ContextCompat.getColor(mContext, R.color
                                      .top_three_second));
          else if(position == 2)
              holder.appTimeTv.setTextColor(ContextCompat.getColor(mContext, R.color
                                      .top_three_third));
          else
          holder.appTimeTv.setTextColor(ContextCompat.getColor(mContext, R.color
                                    .top_three_others));

        }
    }

    @Override
    public int getAdapterItemCount() {
        return indexList.size();
    }

    @Override
    public SimpleAdapterViewHolder getViewHolder(View view) {
        return new SimpleAdapterViewHolder(view, false);
    }

    @Override
    public SimpleAdapterViewHolder onCreateViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.app_leader_board_recycler_view_adapter, parent,
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


    public void insert(Entry index, int position) {
        insert(indexList, index, position);
    }

    public void remove(int position) {
        remove(indexList, position);
    }

    public void clear() {
        clear(indexList);
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
        swapPositions(indexList, from, to);
    }


    @Override
    public long generateHeaderId(int position) {
        // URLogs.d("position--" + position + "   " + getItem(position));
//        if (getItem(position).length() > 0) {
//      return getItem(position).charAt(0);
          return 0;
//        }
//        else return -1;
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

        TextView appNameTv;
        TextView appTimeTv;
        TextView rankingTv;
        ImageView appIconIv;
        View item_view;

        public  SimpleAdapterViewHolder(View itemView, boolean isItem) {
            super(itemView);
            if (isItem) {
                appNameTv = (TextView) itemView.findViewById(
                        R.id.textview);
                appTimeTv = (TextView) itemView.findViewById(R.id.app_time);
                rankingTv = (TextView) itemView.findViewById(R.id.ranking_text_view);
                appIconIv = (ImageView) itemView.findViewById(R.id.imageview);
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

    public Entry getItem(int position) {
        if (customHeaderView != null)
            position--;
        if (position < indexList.size())
            return indexList.get(position);
        else return null;
    }
    private String timeToString(int seconds) {
      int day = (int) TimeUnit.SECONDS.toDays(seconds);
      long hours = TimeUnit.SECONDS.toHours(seconds) - (day * 24);
      long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds)* 60);
      long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) *60);
      return String.format("%02d:%02d:%02d", hours, minute, second);
    }

}
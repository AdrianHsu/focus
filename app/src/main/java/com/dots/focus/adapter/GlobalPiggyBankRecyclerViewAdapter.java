package com.dots.focus.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dots.focus.R;
import com.dots.focus.ui.fragment.GlobalPiggyBankFragment;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class GlobalPiggyBankRecyclerViewAdapter extends
  UltimateViewAdapter<GlobalPiggyBankRecyclerViewAdapter.SimpleAdapterViewHolder> {

  private ArrayList<JSONObject> convertList;
  private Context mContext;

  private static final String TAG = "convert";

  public GlobalPiggyBankRecyclerViewAdapter(ArrayList<JSONObject> _convertList, Context context) {
    convertList = _convertList;
    mContext = context;
  }


  @Override
  public void onBindViewHolder(final SimpleAdapterViewHolder holder, int position) {
    if (position < getItemCount() && (customHeaderView != null ? position <= convertList.size() :
                            position < convertList.size()) && (customHeaderView != null ? position
                            > 0
                            : true)) {
      try {
        JSONObject jsonObject = convertList.get(position);
        holder.textTv.setText(jsonObject.getString("text"));
        int id = jsonObject.getInt("id");
        holder.image.setImageResource(id);
        Double convert = convertList.get(position).getDouble("convert");
        Double time = (GlobalPiggyBankFragment.total / convert);

        DecimalFormat df = new DecimalFormat("#.#");
        String s = df.format(time);
        holder.timeTv.setText(s);
      } catch(JSONException e) {
        Log.v("Piggy", e.getMessage());
      }
    }
  }

  @Override
  public int getAdapterItemCount() {
    return convertList.size();
  }

  @Override
  public SimpleAdapterViewHolder getViewHolder(View view) {
    return new SimpleAdapterViewHolder(view, false);
  }

  @Override
  public SimpleAdapterViewHolder onCreateViewHolder(ViewGroup parent) {
    View v = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.global_piggy_bank_recycler_view_adapter, parent,
                                                    false);
    final SimpleAdapterViewHolder vh = new SimpleAdapterViewHolder(v, true);

    v.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
//        Toast.makeText(v.getContext(), "inside viewholder position = " + vh.getAdapterPosition(), Toast
//                                .LENGTH_SHORT)
//                                .show();
      }
    });
    return vh;
  }


  public void insert(JSONObject convert, int position) {
    insert(convertList, convert, position);
  }

  public void remove(int position) {
    remove(convertList, position);
  }

  public void clear() {
    clear(convertList);
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
    swapPositions(convertList, from, to);
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

    TextView textTv;
    TextView timeTv;
    ImageView image;
    View item_view;

    public  SimpleAdapterViewHolder(View itemView, boolean isItem) {
      super(itemView);
      if (isItem) {
        textTv = (TextView) itemView.findViewById(
                                R.id.textview);
        timeTv = (TextView) itemView.findViewById(
                                R.id.time_textview);
        image = (ImageView) itemView.findViewById(R.id.imageview);
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
    if (position < convertList.size())
      return convertList.get(position);
    else return null;
  }

}
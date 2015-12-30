package com.dots.focus.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dots.focus.R;
import com.dots.focus.service.GetCurrentAppsService;
import com.dots.focus.util.KickUtil;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;
import com.parse.ParseObject;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MessagesRecyclerViewAdapter extends
  UltimateViewAdapter<UltimateRecyclerviewViewHolder> {

  private ArrayList<JSONObject> messagesList;
  private Context mContext;
  private static final int KICK_REQUEST_ITEM = -1;
  private static final int KICK_HISTORY_ITEM = 1;

  private static final String TAG = "Messages";

  public MessagesRecyclerViewAdapter(ArrayList<JSONObject> _messagesList, Context context) {
    messagesList = _messagesList;
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
    if (position < getItemCount() && (customHeaderView != null ? position <= messagesList.size() : position
      < messagesList.size()) && (customHeaderView == null || position > 0)) {

      JSONObject jsonObject = messagesList.get(customHeaderView != null ? position - 1 :
        position);

      if (holder instanceof KickRequestAdapterViewHolder) {
        KickRequestBindItem(jsonObject, (KickRequestAdapterViewHolder) holder, position);
      } else if (holder instanceof KickHistoryAdapterViewHolder) {
        KickHistoryBindItem(jsonObject, (KickHistoryAdapterViewHolder) holder, position);
      }
    }
  }

  public void KickRequestBindItem(JSONObject jsonObject, KickRequestAdapterViewHolder holder,
                                   final int position) {
    try {
      holder.textViewSample.setText(jsonObject.getString("name"));
      final long time =  (System.currentTimeMillis() - jsonObject.getLong("time")) / 1000;
      final String appName = jsonObject.getString("AppName");
      final String appPackageName = jsonObject.getString("AppPackageName");
      String currentAppInfo = "App Name: "+ appName + " ," +
        "" + " 共使用: " + time + "秒";
      holder.currentAppTextViewSample.setText(currentAppInfo);

      final long id = jsonObject.getLong("id");
      final String name = jsonObject.getString("name");
      String url = "https://graph.facebook.com/" + String.valueOf(id) +
        "/picture?type=large";
      Picasso.with(mContext).load(url).into(holder.imageViewSample);
      holder.buttonSample.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

          KickUtil.kick(id, name, "你被踢了！這是測試訊息。", time, appName, appPackageName);
          remove(position);
        }
      });

    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  public void KickHistoryBindItem(final JSONObject jsonObject, KickHistoryAdapterViewHolder holder,
                                  final int position) {
    try {
      holder.textViewSample.setText(jsonObject.getString("name"));

      final long id = jsonObject.getLong("id");
      final String name = jsonObject.getString("name");
      String content = jsonObject.getString("content");
      final String mContent = "謝謝你踢我！";

      holder.textViewSample.setText(name);
      holder.kickHistoryContentTextView.setText(content);
      String url = "https://graph.facebook.com/" + String.valueOf(id) +
        "/picture?type=large";
      Picasso.with(mContext).load(url).into(holder.imageViewSample);

      final String objectId = jsonObject.getString("objectId");
      Log.v(TAG, "ready to setOnClickListener, with objectId: " + objectId );

      holder.buttonSample.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

          KickUtil.kickResponse(objectId, mContent);
          Log.v(TAG, "called kickResponse with objectId:" + objectId );
          remove(position);
        }
      });

    } catch (JSONException e) {
      Log.v(TAG, e.getMessage());
    }
  }
  @Override
  public int getAdapterItemCount() {
    if (messagesList.isEmpty())
      return 0;
    return messagesList.size();
  }


  @Override
  public UltimateRecyclerviewViewHolder getViewHolder(View view) {

    return new UltimateRecyclerviewViewHolder(view);
  }


  @Override
  public int getItemViewType(int position) {

    int type = -2;
    try {
      type = messagesList.get(position).getInt("state");
    } catch (JSONException e) {
      Log.v(TAG, e.getMessage());
    }

    Log.v(TAG, "itemViewType = " + type);
//    return type;
    return type;
  }

  @Override
  public UltimateRecyclerviewViewHolder onCreateViewHolder(ViewGroup parent) {
    return null;
  }

  @Override
  public UltimateRecyclerviewViewHolder onCreateViewHolder(ViewGroup parent, int i) {

    View v = null;

    if (i == KICK_REQUEST_ITEM) {
      v = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.kick_request_recycler_view_adapter, parent, false);
      final KickRequestAdapterViewHolder vh = new KickRequestAdapterViewHolder(v, true);

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

    } else if (i == KICK_HISTORY_ITEM) {
      v = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.kick_history_recycler_view_adapter, parent, false);
      final KickHistoryAdapterViewHolder vh = new KickHistoryAdapterViewHolder(v, true);

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
    return null;
  }

  public void insert(JSONObject jsonObject, int position) {
    insert(messagesList, jsonObject, position);
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


  public class KickRequestAdapterViewHolder extends UltimateRecyclerviewViewHolder {

    TextView textViewSample;
    TextView currentAppTextViewSample;
    ImageView imageViewSample;
    Button buttonSample;
    View item_view;


    public KickRequestAdapterViewHolder(View itemView, boolean isItem) {
      super(itemView);
      if (isItem) {
        textViewSample = (TextView) itemView.findViewById(
          R.id.textview);
        currentAppTextViewSample = (TextView) itemView.findViewById(R.id.current_app_textview);
        imageViewSample = (ImageView) itemView.findViewById(R.id.imageview);
        buttonSample = (Button) itemView.findViewById(R.id.button_kick);

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

  public class KickHistoryAdapterViewHolder extends UltimateRecyclerviewViewHolder {

    TextView textViewSample;
    TextView kickHistoryContentTextView;
    ImageView imageViewSample;
    Button buttonSample;
    View item_view;


    public KickHistoryAdapterViewHolder(View itemView, boolean isItem) {
      super(itemView);
      if (isItem) {
        textViewSample = (TextView) itemView.findViewById(
          R.id.textview);
        kickHistoryContentTextView = (TextView) itemView.findViewById(R.id
                                .kick_history_content_textview);
        imageViewSample = (ImageView) itemView.findViewById(R.id.imageview);
        buttonSample = (Button) itemView.findViewById(R.id.button_reply);
        buttonSample.setText("回覆");

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
    if (customHeaderView != null) position--;
    if (position < messagesList.size())
      return messagesList.get(position);
    else return null;
  }
}
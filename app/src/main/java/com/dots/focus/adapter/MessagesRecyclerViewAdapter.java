package com.dots.focus.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.dots.focus.config.KickState;
import com.dots.focus.ui.KickMessagesActivity;
import com.dots.focus.util.KickUtil;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MessagesRecyclerViewAdapter extends
  UltimateViewAdapter<UltimateRecyclerviewViewHolder> {

  private ArrayList<JSONObject> messagesList;
  private Context mContext;

  private static final int KICK_REQUEST_ITEM = KickState.REQUEST_DOWNLOADED.getValue();
  private static final int KICK_HISTORY_ITEM = KickState.REQUEST_DOWNLOADED.getValue();
  private static final int KICK_RESPONSE_ITEM = KickState.REQUEST_DOWNLOADED.getValue();
  //

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
        KickRequestBindItem(jsonObject, (KickRequestAdapterViewHolder) holder);
      } else if (holder instanceof KickHistoryAdapterViewHolder) {
        KickHistoryBindItem(jsonObject, (KickHistoryAdapterViewHolder) holder);
      } else if (holder instanceof KickResponseAdapterViewHolder) {
        KickResponseBindItem(jsonObject, (KickResponseAdapterViewHolder) holder);
      }
    }
  }

  public void KickRequestBindItem(final JSONObject jsonObject, final KickRequestAdapterViewHolder
          holder) {
    Log.v(TAG, "KickRequestBindItem~~~");

    try {

//      final long time =  (System.currentTimeMillis() - jsonObject.getLong("time1")) / 1000;
      // final String appName = jsonObject.getString("AppName");
//      String currentAppInfo =  ""; // need to be redefined

              /*"App Name: "+ appName + " ," +
        "" + " 共使用: " + time + "秒"; */
//      holder.currentAppTextViewSample.setText(currentAppInfo);
      final String name = jsonObject.getString("user_name");
      final String objectId = jsonObject.getString("objectId");
      final long id = jsonObject.getLong("user_id");
      final int period = jsonObject.getInt("period");
      final long time = jsonObject.getLong("time");
      final String content = jsonObject.getString("content");

      holder.textViewSample.setText(name);
      String url = "https://graph.facebook.com/" + String.valueOf(id) +
        "/picture?process=large";
      Picasso.with(mContext).load(url).into(holder.imageViewSample);
      holder.buttonSample.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          KickUtil.kick("你被踢了！這是測試訊息。", objectId);
          int index = indexOf(jsonObject);
          if (index != -1)
            remove(index);
        }
      });
      holder.item_view.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Toast.makeText(v.getContext(), "inside viewholder position = " + holder.getAdapterPosition
                                  (), Toast
                                  .LENGTH_SHORT)
                                  .show();
          Intent intent;
          intent = new Intent(mContext, KickMessagesActivity.class);
          intent.putExtra("user_name", name);
          intent.putExtra("objectId", objectId);
          intent.putExtra("user_id", id);
//          intent.putExtra("LimitType", limitType);
          intent.putExtra("period", period);
          intent.putExtra("time", time);
          intent.putExtra("content", content);
          mContext.startActivity(intent);
        }
      });

    } catch (JSONException e) {
      e.printStackTrace();
    }

    }

  public void KickHistoryBindItem(final JSONObject jsonObject, final KickHistoryAdapterViewHolder
          holder) {
    try {

      final long id = jsonObject.getLong("user_id");
      final String name = jsonObject.getString("user_name");
      String content = jsonObject.getString("content2");
      final String mContent = "謝謝你踢我！";

      holder.textViewSample.setText(name);
      holder.kickHistoryContentTextView.setText(content);
      String url = "https://graph.facebook.com/" + String.valueOf(id) +
        "/picture?process=large";
      Picasso.with(mContext).load(url).into(holder.imageViewSample);

      final String objectId = jsonObject.getString("objectId");
      Log.v(TAG, "ready to setOnClickListener, with objectId: " + objectId);

      holder.buttonSample.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

          KickUtil.kickResponse(mContent, objectId);
          Log.v(TAG, "called kickResponse with objectId:" + objectId);
          int index = indexOf(jsonObject);
          if (index != -1)
            remove(index);
        }
      });

    } catch (JSONException e) {
      Log.v(TAG, e.getMessage());
    }
    if (holder.item_view != null) {
      holder.item_view.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Toast.makeText(v.getContext(), "inside viewholder position = " + holder.getAdapterPosition()
                                  , Toast
                                  .LENGTH_SHORT)
                                  .show();
//          Intent intent;
//          intent = new Intent(mContext, KickMessagesActivity.class);
//          mContext.startActivity(intent);
        }
      });
    }
  }

  public void KickResponseBindItem(final JSONObject jsonObject, final KickResponseAdapterViewHolder
                          holder) {
    try {
      holder.textViewSample.setText(jsonObject.getString("user_name"));

      final long id = jsonObject.getLong("user_id");
      final String name = jsonObject.getString("user_name");
      String content = jsonObject.getString("content");

      holder.textViewSample.setText(name);
      holder.kickResponseContentTextView.setText(content);
      String url = "https://graph.facebook.com/" + String.valueOf(id) +
                              "/picture?process=large";
      Picasso.with(mContext).load(url).into(holder.imageViewSample);

      final String objectId = jsonObject.getString("objectId");
      Log.v(TAG, "ready to setOnClickListener, with objectId: " + objectId );

      holder.buttonSample.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          ParseQuery<ParseObject> query = ParseQuery.getQuery("KickHistory");
          query.fromLocalDatastore();
          query.getInBackground(objectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject kickHistory, ParseException e) {
              if (e == null && kickHistory != null) {
                kickHistory.put("state", 4);
                Log.d(TAG, "kickResponse known");
              } else if (e != null) {
                Log.d(TAG, e.getMessage());
              }
            }
          });
          int index = indexOf(jsonObject);
          if (index != -1)
            remove(index);
        }
      });

    } catch (JSONException e) {
      Log.v(TAG, e.getMessage());
    }

    if (holder.item_view != null) {
      holder.item_view.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Toast.makeText(v.getContext(), "inside viewholder position = " +
                                  holder.getAdapterPosition()
                                  , Toast
                                  .LENGTH_SHORT)
                                  .show();
//          Intent intent;
//          intent = new Intent(mContext, KickMessagesActivity.class);
//          mContext.startActivity(intent);
        }
      });
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

    int process = -1;
    try {
      process = messagesList.get(position).getInt("state");
    } catch (JSONException e) {
      Log.v(TAG, e.getMessage());
    }

    Log.v(TAG, "process = " + process);
    if(process <= KICK_REQUEST_ITEM && process >= 0)
      return KICK_REQUEST_ITEM;
    else if(process <= KICK_HISTORY_ITEM && process > KICK_REQUEST_ITEM)
      return KICK_HISTORY_ITEM;
    else if(process <= KICK_RESPONSE_ITEM && process > KICK_HISTORY_ITEM)
      return KICK_RESPONSE_ITEM;

    return process; // error
  }

  @Override
  public UltimateRecyclerviewViewHolder onCreateViewHolder(ViewGroup parent) {
    return null;
  }

  @Override
  public UltimateRecyclerviewViewHolder onCreateViewHolder(ViewGroup parent, int i) {

    View v;
    Log.v(TAG, "onCreateViewHolder~~~" + i);
    if (i == KICK_REQUEST_ITEM) {
      v = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.kick_request_recycler_view_adapter, parent, false);
      final KickRequestAdapterViewHolder vh = new KickRequestAdapterViewHolder(v, true);

      return vh;
    } else if (i == KICK_HISTORY_ITEM) {
      v = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.kick_history_recycler_view_adapter, parent, false);
      final KickHistoryAdapterViewHolder vh = new KickHistoryAdapterViewHolder(v, true);

      return vh;

    } else if (i == KICK_RESPONSE_ITEM) {
      v = LayoutInflater.from(parent.getContext())
                              .inflate(R.layout.kick_response_recycler_view_adapter, parent, false);
      final KickResponseAdapterViewHolder vh = new KickResponseAdapterViewHolder(v, true);
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

  public int indexOf(JSONObject jsonObject) {
    return messagesList.indexOf(jsonObject);
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
    ImageView imageViewSample;
    Button buttonSample;
    View item_view;


    public KickRequestAdapterViewHolder(View itemView, boolean isItem) {
      super(itemView);
      if (isItem) {
        textViewSample = (TextView) itemView.findViewById(
          R.id.textview);
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
  public class KickResponseAdapterViewHolder extends UltimateRecyclerviewViewHolder {

    TextView textViewSample;
    TextView kickResponseContentTextView;
    ImageView imageViewSample;
    Button buttonSample;
    View item_view;


    public KickResponseAdapterViewHolder(View itemView, boolean isItem) {
      super(itemView);
      if (isItem) {
        textViewSample = (TextView) itemView.findViewById(
                                R.id.textview);
        kickResponseContentTextView = (TextView) itemView.findViewById(R.id
                                .kick_response_content_textview);
        imageViewSample = (ImageView) itemView.findViewById(R.id.imageview);
        buttonSample = (Button) itemView.findViewById(R.id.button_end);
        buttonSample.setText("知道了");

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
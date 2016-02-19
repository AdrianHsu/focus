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
import com.dots.focus.service.GetKickRequestService;
import com.dots.focus.ui.KickHistoryActivity;
import com.dots.focus.ui.KickRequestActivity;
import com.dots.focus.ui.KickResponseActivity;
import com.dots.focus.util.KickUtil;
import com.dots.focus.util.SettingsUtil;
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
  private static final int KICK_HISTORY_ITEM = KickState.KICK_DOWNLOADED.getValue();
  private static final int KICK_RESPONSE_ITEM = KickState.RESPONSE_DOWNLOADED.getValue();
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
      Log.v(TAG, "id in adapter: " + id);
      Log.v(TAG, "name in adapter: " + name);
      final int period = jsonObject.getInt("period");
      final long time = jsonObject.getLong("time1");
      final String content = jsonObject.getString("content1");
      final String mContent = SettingsUtil.getString("kickHistory");


      final long expire_time = System.currentTimeMillis() - KickUtil.expire_period;
      final Boolean expire = (time < expire_time);

      holder.textViewSample.setText(name);
      holder.contentTv.setText(content);
      if(!expire) {
        holder.expireTv.setText("ONLINE");
        holder.expireTv.setTextColor(mContext.getResources().getColor(R.color.red));

        holder.buttonSample.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            KickUtil.kick(mContent, objectId);
            int index = indexOf(jsonObject);
            if (index != -1)
              remove(index);
          }
        });
        holder.buttonSample.setText("戳");
      } else {
        holder.expireTv.setText("EXPIRED");
        holder.expireTv.setTextColor(mContext.getResources().getColor(R.color.semi_black));

        holder.buttonSample.setText("移除");
        holder.buttonSample.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            GetKickRequestService.deleteExpired(objectId);
            int index = indexOf(jsonObject);
            if (index != -1)
              remove(index);
          }
        });
      }
      String url = "https://graph.facebook.com/" + String.valueOf(id) +
        "/picture?process=large";
      Picasso.with(mContext).load(url).into(holder.imageViewSample);
      holder.item_view.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Toast.makeText(v.getContext(), "inside viewholder position = " + holder.getAdapterPosition
                                  (), Toast
                                  .LENGTH_SHORT)
                                  .show();
          Intent intent;
          intent = new Intent(mContext, KickRequestActivity.class);
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
      Log.v(TAG, e.getMessage());
    }

    }

  public void KickHistoryBindItem(final JSONObject jsonObject, final KickHistoryAdapterViewHolder
          holder) {
    try {

      final String name = jsonObject.getString("user_name");
      final String objectId = jsonObject.getString("objectId");
      final long id = jsonObject.getLong("user_id");
      Log.v(TAG, "id in adapter: " + id);
      Log.v(TAG, "name in adapter: " + name);
      final int period = jsonObject.getInt("period");
      final long time1 = jsonObject.getLong("time1");
      final long time2 = jsonObject.getLong("time2");
      final String content1 = jsonObject.getString("content1");
      final String content2 = jsonObject.getString("content2");
      final String mContent = SettingsUtil.getString("kickResponse");

      holder.textViewSample.setText(name);

      holder.kickHistoryContentTextView.setText(content2);
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

      String url = "https://graph.facebook.com/" + String.valueOf(id) +
        "/picture?process=large";
      Picasso.with(mContext).load(url).into(holder.imageViewSample);

      Log.v(TAG, "ready to setOnClickListener, with objectId: " + objectId);


      if (holder.item_view != null) {
        holder.item_view.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Toast.makeText(v.getContext(), "inside viewholder position = " + holder.getAdapterPosition()
                                    , Toast.LENGTH_SHORT).show();
            Intent intent;
            intent = new Intent(mContext, KickHistoryActivity.class);
            intent.putExtra("user_name", name);
            intent.putExtra("objectId", objectId);
            intent.putExtra("user_id", id);
//          intent.putExtra("LimitType", limitType);
            intent.putExtra("period", period);
            intent.putExtra("time1", time1);
            intent.putExtra("content1", content1);
            intent.putExtra("time2", time2);
            intent.putExtra("content2", content2);
            mContext.startActivity(intent);
          }
        });
      }
    } catch (JSONException e) {
      Log.v(TAG, e.getMessage());
    }
  }

  public void KickResponseBindItem(final JSONObject jsonObject, final KickResponseAdapterViewHolder
                          holder) {
    try {

      final String name = jsonObject.getString("user_name");
      final String objectId = jsonObject.getString("objectId");
      final long id = jsonObject.getLong("user_id");
      Log.v(TAG, "id in adapter: " + id);
      Log.v(TAG, "name in adapter: " + name);
      final int period = jsonObject.getInt("period");
      final long time1 = jsonObject.getLong("time1");
      final long time2 = jsonObject.getLong("time2");
      final long time3 = jsonObject.getLong("time3");
      final String content1 = jsonObject.getString("content1");
      final String content2 = jsonObject.getString("content2");
      final String content3 = jsonObject.getString("content3");
      final Boolean is_me = jsonObject.getBoolean("is_me");


      holder.textViewSample.setText(name);
      String url = "https://graph.facebook.com/" + String.valueOf(id) +
                              "/picture?process=large";
      Picasso.with(mContext).load(url).into(holder.imageViewSample);

      Log.v(TAG, "ready to setOnClickListener, with objectId: " + objectId);

      if(is_me) {
        holder.kickResponseContentTextView.setText("你：" + content3);
        holder.buttonSample.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("KickHistory");
            query.fromLocalDatastore();
            query.getInBackground(objectId, new GetCallback<ParseObject>() {
              @Override
              public void done(ParseObject kickHistory, ParseException e) {
                if (e == null && kickHistory != null) {
                  kickHistory.put("state", KickState.READED.getValue());
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
      } else {
        holder.kickResponseContentTextView.setText(content3);
        holder.buttonSample.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("KickHistory");
            query.fromLocalDatastore();
            query.getInBackground(objectId, new GetCallback<ParseObject>() {
              @Override
              public void done(ParseObject kickHistory, ParseException e) {
                if (e == null && kickHistory != null) {
                  kickHistory.put("state", KickState.READED.getValue());
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
      }

      if (holder.item_view != null) {
        holder.item_view.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Toast.makeText(v.getContext(), "inside viewholder position = " + holder.getAdapterPosition()
                                    , Toast
                                    .LENGTH_SHORT)
                                    .show();
            Intent intent;
            intent = new Intent(mContext, KickResponseActivity.class);
            intent.putExtra("user_name", name);
            intent.putExtra("objectId", objectId);
            intent.putExtra("user_id", id);
//          intent.putExtra("LimitType", limitType);
            intent.putExtra("period", period);
            intent.putExtra("time1", time1);
            intent.putExtra("content1", content1);
            intent.putExtra("time2", time2);
            intent.putExtra("content2", content2);
            intent.putExtra("time3", time3);
            intent.putExtra("content3", content3);
            intent.putExtra("is_me", is_me);
            mContext.startActivity(intent);
          }
        });
      }
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
    else if(process == KickState.READED.getValue())
      return -1; // error

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
    TextView contentTv;
    TextView expireTv;
    ImageView imageViewSample;
    Button buttonSample;
    View item_view;


    public KickRequestAdapterViewHolder(View itemView, boolean isItem) {
      super(itemView);
      if (isItem) {
        textViewSample = (TextView) itemView.findViewById(
          R.id.textview);
        contentTv = (TextView) itemView.findViewById(R.id.kick_request_content1);
        expireTv = (TextView) itemView.findViewById(R.id.expire);
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
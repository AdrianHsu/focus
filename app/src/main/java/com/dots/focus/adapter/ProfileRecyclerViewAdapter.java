package com.dots.focus.adapter;

/**
 * Created by AdrianHsu on 2015/12/12.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dots.focus.R;
import com.dots.focus.ui.ProfileActivity;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;

import java.util.List;


public class ProfileRecyclerViewAdapter extends UltimateViewAdapter<ProfileRecyclerViewAdapter
  .SimpleAdapterViewHolder> {

  private List<String> stringList;
  private Context mContext;

  public ProfileRecyclerViewAdapter(List<String> stringList, Context context) {
    this.stringList = stringList;
    this.mContext = context;
  }


  @Override
  public void onBindViewHolder(final SimpleAdapterViewHolder holder, final int position) {
    if (position < getItemCount() && (customHeaderView != null ? position <= stringList.size() : position < stringList.size()) && (customHeaderView != null ? position > 0 : true)) {

      holder.textViewSample.setText(stringList.get(customHeaderView != null ? position - 1 : position));

      String str = "";
      if(position == 0 ) {
        String modify = mContext.getResources().getString(R.string.profile_click_modify);
        holder.modifyTv.setText(modify);
      } else if(position == 2) {
        holder.textViewSample.setTextSize(16);
        String modify = mContext.getResources().getString(R.string.profile_click_modify);
        holder.modifyTv.setText(modify);
      } else if(position == 4) {
        str = mContext.getResources().getString(R.string.profile_birth_text);
        String modify = mContext.getResources().getString(R.string.profile_click_modify);
        holder.modifyTv.setText(modify);
      }
      else if (position == 5) {
        str = mContext.getResources().getString(R.string.profile_friend_num_text);
      }
      else if (position == 6) {
        str = mContext.getResources().getString(R.string.profile_total_time_saved_text);
      }
      holder.textViewContent.setText(str);

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

  private void createGenderDialog(final TextView textView) {
    new MaterialDialog.Builder(mContext)
                            .title("重設性別")
                            .items(R.array.gender)
                            .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                              @Override
                              public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                /**
                                 * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                                 * returning false here won't allow the newly selected radio button to actually be selected.
                                 **/
                                if (textView != null && text != null) {
                                  String temp = text.toString();
                                  textView.setText(temp);
                                }
                                ProfileActivity.gender = (which == 0);

                                return true;
                              }
                            })
                            .positiveText("完成")
                            .show();
  }
  private void createOccupationDialog(final TextView textview) {
    new MaterialDialog.Builder(mContext)
                            .title("重設職業")
                            .items(R.array.occupation)
                            .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                              @Override
                              public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                /**
                                 * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                                 * returning false here won't allow the newly selected radio button to actually be selected.
                                 **/
                                if (textview != null && text != null) {
                                  String temp = text.toString();
                                  textview.setText(temp);

                                  ProfileActivity.occupation = temp;
                                }
                                return true;
                              }
                            })
                            .positiveText("完成")
                            .show();
  }
  private void createBirthDialog(final TextView textview) {
    new MaterialDialog.Builder(mContext)
                            .title("重設出生年份")
                            .items(R.array.birth)
                            .itemsCallback(new MaterialDialog.ListCallback() {
                              @Override
                              public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                if (textview != null && text != null) {
                                  String temp = text.toString();
                                  textview.setText(temp);
                                  int year = Integer.parseInt(temp);
                                  ProfileActivity.yearOfBirth = year;
                                }
                              }
                            })
                            .positiveText("完成")
                            .show();
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
      .inflate(R.layout.profile_recycler_view_adapter, parent, false);
    final SimpleAdapterViewHolder vh = new SimpleAdapterViewHolder(v, true);

    final TextView textview = (TextView) v.findViewById(R.id.textview);
    v.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
//        Toast.makeText(v.getContext(), "inside viewholder position = " + vh.getAdapterPosition(), Toast
//                                .LENGTH_SHORT)
//          .show();

        switch(vh.getAdapterPosition()) {
          case 0:
            createGenderDialog(textview);
            break;
          case 1:
            break;
          case 2:
            createOccupationDialog(textview);
            break;
          case 3:
            break;
          case 4:
            createBirthDialog(textview);
            break;
          case 5:
            break;
          case 6:
            break;

        }

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
    TextView modifyTv;
    View item_view;

    public  SimpleAdapterViewHolder(View itemView, boolean isItem) {
      super(itemView);
      if (isItem) {
        textViewSample = (TextView) itemView.findViewById(
          R.id.textview);
        textViewContent = (TextView) itemView.findViewById(R.id.content);
        modifyTv = (TextView) itemView.findViewById(R.id.modify);
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
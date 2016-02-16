package com.dots.focus.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dots.focus.R;
import com.dots.focus.util.TrackAccessibilityUtil;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

public class DashboardFragment extends Fragment {

  private Context mContext;
  private DashboardDonutFragment mSampleFitFragment;
  public static View topThreeCardView;
  private TextView daySwitchTv;
  private TextView addictionIndexTv;
  private Button daySwitchLeftBtn;
  private Button daySwitchRightBtn;
  public static int CURRENT_DAY;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    mContext = getActivity();
    final View v = inflater.inflate(R.layout.fragment_dashboard, container, false);
    topThreeCardView = v.findViewById(R.id.top_three_card);
    mSampleFitFragment = new DashboardDonutFragment();

    if (savedInstanceState == null){

      //add child fragment
      getChildFragmentManager()
        .beginTransaction()
        .add(R.id.frameDashboardDonut, mSampleFitFragment, "tag")
        .commit();

    }
    daySwitchTv = (TextView) v.findViewById(R.id.day_switch_textview);
    String day = TrackAccessibilityUtil.dayString(CURRENT_DAY);
    daySwitchTv.setText(day);
    addictionIndexTv = (TextView) v.findViewById(R.id.textAddictIndex);
    setAddictionIndex(CURRENT_DAY);

    // DEBUG:
    // java.lang.IllegalStateException: Failure saving state:
    // active DashboardDonutFragment{21d40e50} has cleared index: -1
    daySwitchLeftBtn = (Button) v.findViewById(R.id.day_switch_left_btn);
    daySwitchLeftBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        CURRENT_DAY++;
        daySwitchTv = (TextView) v.findViewById(R.id.day_switch_textview);
        String day = TrackAccessibilityUtil.dayString(CURRENT_DAY);
        daySwitchTv.setText(day);
        daySwitchRightBtn.setEnabled(true);
//        daySwitchLeftBtn.setEnabled(false);
        getChildFragmentManager()
                                .beginTransaction()
                                .detach(mSampleFitFragment)
                                .attach(mSampleFitFragment)
                                .commit();
      }
    });
    daySwitchRightBtn = (Button) v.findViewById(R.id.day_switch_right_btn);
    daySwitchRightBtn.setEnabled(false);
    daySwitchRightBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        CURRENT_DAY--;
        daySwitchTv = (TextView) v.findViewById(R.id.day_switch_textview);
        String day = TrackAccessibilityUtil.dayString(CURRENT_DAY);
        daySwitchTv.setText(day);
        if (CURRENT_DAY == 0)
          daySwitchRightBtn.setEnabled(false);
        daySwitchLeftBtn.setEnabled(true);

        getChildFragmentManager()
                                .beginTransaction()
                                .detach(mSampleFitFragment)
                                .attach(mSampleFitFragment)
                                .commit();
      }
    });

    return v;
  }

  private void setAddictionIndex(int mDay) {

    int state = 0 = TrackAccessibilityUtil.dayCategoryClicks(mDay);
    if(state == 0) {

      addictionIndexTv.setText(getResources().getString(R.string.addict_0));
      addictionIndexTv.setTextColor(getResources().getColor(R.color.addict_0));
    } else if (state == 1) {
      addictionIndexTv.setText(getResources().getString(R.string.addict_1));
      addictionIndexTv.setTextColor(getResources().getColor(R.color.addict_1));

    } else if (state == 2) {
      addictionIndexTv.setText(getResources().getString(R.string.addict_2));
      addictionIndexTv.setTextColor(getResources().getColor(R.color.addict_2));

    } else if (state == 3) {
      addictionIndexTv.setText(getResources().getString(R.string.addict_3));
      addictionIndexTv.setTextColor(getResources().getColor(R.color.addict_3));

    }
  }
}
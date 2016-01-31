package com.dots.focus.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dots.focus.R;
import com.dots.focus.model.AppInfo;
import com.dots.focus.util.FetchAppUtil;
import com.dots.focus.util.TrackAccessibilityUtil;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.Utils;

import org.w3c.dom.Text;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Custom implementation of the MarkerView.
 *
 * @author Philipp Jahoda
 */
public class DailyChartMarkerView extends MarkerView {

  private TextView tvContent;

  public DailyChartMarkerView(Context context, int layoutResource) {
    super(context, layoutResource);

    tvContent = (TextView) findViewById(R.id.tvContent);
  }

  // callbacks everytime the MarkerView is redrawn, can be used to update the
  // content (user-interface)
  @Override
  public void refreshContent(Entry e, Highlight highlight) {

    if (e instanceof CandleEntry) {

      CandleEntry ce = (CandleEntry) e;

      tvContent.setText("" + Utils.formatNumber(ce.getHigh(), 0, true));
    } else {

      tvContent.setText("" + Utils.formatNumber(e.getVal(), 0, true));
    }
    refreshTopThree(e);
  }

  private void refreshTopThree(Entry e) {

    int pickedHour = e.getXIndex();
    long t = System.currentTimeMillis(),
                            offset = TrackAccessibilityUtil.getTimeOffset() * TrackAccessibilityUtil.anHour;
    t = 86400000 * ((t + offset) / 86400000 - DailyAppUsageChartActivity.day) - offset;

    List<List<Integer>> hourAppLength = TrackAccessibilityUtil.hourAppLength(t);
    int [] topThreeAppIndex;
    topThreeAppIndex = TrackAccessibilityUtil.getFirstThree(hourAppLength.get(pickedHour));

    View v = DailyAppUsageChartActivity.topThreeCardHourlyView;
    TextView pickedHourIntervalTv = (TextView) v.findViewById(R.id.picked_hour_interval);
    String interval = String.valueOf(pickedHour) + ":00 - " + String.valueOf
                            (pickedHour + 1) + ":00";
    pickedHourIntervalTv.setText(interval);

    View [] itemViewArray = new View [4];
    itemViewArray[0] = v.findViewById(R.id.first_adapter);
    itemViewArray[1] = v.findViewById(R.id.second_adapter);
    itemViewArray[2] = v.findViewById(R.id.third_adapter);
    itemViewArray[3] = v.findViewById(R.id.others_adapter);

    for(int i = 0; i < 3; i++) {


      TextView appNameTv = (TextView) itemViewArray[i].findViewById(R.id.app_name);
      TextView appTimeTv = (TextView) itemViewArray[i].findViewById(R.id.app_time);
      ImageView appIconIv = (ImageView) itemViewArray[i].findViewById(R.id.imageview);

      if(topThreeAppIndex[i] == -1) {

        appIconIv.setImageDrawable(null);
        appNameTv.setText("");
        appTimeTv.setText("");
        continue;
      }
      int index = topThreeAppIndex[i];
      AppInfo mAppInfo = FetchAppUtil.getApp(index);
      Drawable mIcon = mAppInfo.getIcon();

      int time = hourAppLength.get(pickedHour).get(topThreeAppIndex[i]);

      if(mIcon != null) {
        if(i != 3) {
          appNameTv.setText(mAppInfo.getName());
          appIconIv.setImageDrawable(mIcon);
        } else {
          appNameTv.setText("其他應用軟體");
        }
      }
      switch(i) {
        case 0:
          appTimeTv.setText(timeToString(time));
          appTimeTv.setTextColor(ContextCompat.getColor(getContext(), R.color.top_three_first));
          break;
        case 1:
          appTimeTv.setText(timeToString(time));
          appTimeTv.setTextColor(ContextCompat.getColor(getContext(), R.color.top_three_second));
          break;
        case 2:
          appTimeTv.setText(timeToString(time));
          appTimeTv.setTextColor(ContextCompat.getColor(getContext(), R.color.top_three_third));
          break;

      }
    }
  }
  private String timeToString(int seconds) {
    int day = (int) TimeUnit.SECONDS.toDays(seconds);
    long hours = TimeUnit.SECONDS.toHours(seconds) - (day * 24);
    long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds)* 60);
    long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) *60);
    return String.format("%02d:%02d:%02d", hours, minute, second);
  }
  @Override
  public int getXOffset(float xpos) {
    // this will center the marker-view horizontally
    return -(getWidth() / 2);
  }

  @Override
  public int getYOffset(float ypos) {
    // this will cause the marker-view to be above the selected value
    return -getHeight();
  }
}

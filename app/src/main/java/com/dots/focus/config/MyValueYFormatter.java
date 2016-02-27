package com.dots.focus.config;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

/**
 * Created by AdrianHsu on 2016/2/27.
 */
public class MyValueYFormatter implements YAxisValueFormatter {

  private DecimalFormat mFormat;

  public MyValueYFormatter() {
    mFormat = new DecimalFormat("#.#"); // use one decimal
  }

  @Override
  public String getFormattedValue(float value, YAxis yAxis) {
    return mFormat.format(value);
  }
}
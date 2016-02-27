package com.dots.focus.config;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

/**
 * Created by AdrianHsu on 2016/2/27.
 */
public class MyValueFormatter implements ValueFormatter {

  private DecimalFormat mFormat;

  public MyValueFormatter() {
    mFormat = new DecimalFormat("#.#"); // use one decimal
  }

  @Override
  public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
    // write your logic here
    return mFormat.format(value);
  }
}
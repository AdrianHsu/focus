package com.dots.focus.util;

/**
 * Created by AdrianHsu on 2015/10/9.
 */
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.dots.focus.R;

public class AvenirNextTextView extends TextView {

  public AvenirNextTextView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init(attrs);
  }

  public AvenirNextTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(attrs);

  }

  public AvenirNextTextView(Context context) {
    super(context);
    init(null);
  }

  private void init(AttributeSet attrs) {
    if (attrs!=null) {
      TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.AvenirNextTextView);
      String fontName = a.getString(R.styleable.AvenirNextTextView_AvenirNext_Regular);
      if (fontName!=null) {
        Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/"+fontName);
        setTypeface(myTypeface);
      }
      a.recycle();
    }
  }

}

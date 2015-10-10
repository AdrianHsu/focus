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

public class AvenirNextItalicTextView extends TextView {

  public AvenirNextItalicTextView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init(attrs);
  }

  public AvenirNextItalicTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(attrs);

  }

  public AvenirNextItalicTextView(Context context) {
    super(context);
    init(null);
  }

  private void init(AttributeSet attrs) {
    if (attrs!=null) {
      TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable
        .AvenirNextItalicTextView);
      String fontName = a.getString(R.styleable.AvenirNextItalicTextView_AvenirNext_Italic);
      if (fontName!=null) {
        Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/"+fontName);
        setTypeface(myTypeface);
      }
      a.recycle();
    }
  }

}

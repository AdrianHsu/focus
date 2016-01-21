package com.dots.focus.ui;

/**
 * Created by AdrianHsu on 2016/1/21.
 */

import java.util.Random;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.ListView;
import android.support.v7.widget.Toolbar;

import com.dots.focus.R;
import com.dots.focus.adapter.DiscussArrayAdapter;
import com.dots.focus.model.OneComment;

public class KickMessagesActivity extends BaseActivity {
  private DiscussArrayAdapter adapter;
  private ListView lv;
  private EditText editText1;
  private static Random random;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_discuss);


    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    toolbar.bringToFront();

    random = new Random();

    lv = (ListView) findViewById(R.id.listView1);

    adapter = new DiscussArrayAdapter(getApplicationContext(), R.layout.listitem_discuss);

    lv.setAdapter(adapter);

    editText1 = (EditText) findViewById(R.id.editText1);
    editText1.setOnKeyListener(new OnKeyListener() {
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        // If the event is a key-down event on the "enter" button
        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
          // Perform action on key press
          adapter.add(new OneComment(false, editText1.getText().toString()));
          editText1.setText("");
          return true;
        }
        return false;
      }
    });

    addItems();
  }

  private void addItems() {
    adapter.add(new OneComment(true, "這是測試文字"));

    for (int i = 0; i < 4; i++) {
      boolean left = getRandomInteger(0, 1) == 0 ? true : false;
      String words = "愛上一匹野馬、可我的家裡沒有草原：" + String.valueOf(i);

      adapter.add(new OneComment(left, words));
    }
  }

  private static int getRandomInteger(int aStart, int aEnd) {
    if (aStart > aEnd) {
      throw new IllegalArgumentException("Start cannot exceed End.");
    }
    long range = (long) aEnd - (long) aStart + 1;
    long fraction = (long) (range * random.nextDouble());
    int randomNumber = (int) (fraction + aStart);
    return randomNumber;
  }

}
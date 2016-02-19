package com.dots.focus.ui;



/**
 * Created by AdrianHsu on 2015/12/13.
 */

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.method.KeyListener;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dots.focus.R;
import com.dots.focus.util.SettingsUtil;


public class CannedMessagesSettingsActivity extends BaseActivity {

  private EditText kickRequestEditText;
  private EditText kickHistoryEditText;
  private EditText kickResponseEditText;
  private Button doneBtn;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_canned_messages_settings);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle(getResources().getString(R.string.title_canned_message_setting));

    kickRequestEditText = (EditText) findViewById(R.id.kick_request);
    kickHistoryEditText = (EditText) findViewById(R.id.kick_history);
    kickResponseEditText = (EditText) findViewById(R.id.kick_response);
    doneBtn = (Button) findViewById(R.id.done);

    kickRequestEditText.setText(SettingsUtil.getString("kickRequest"));
    kickHistoryEditText.setText(SettingsUtil.getString("kickHistory"));
    kickResponseEditText.setText(SettingsUtil.getString("kickResponse"));
    kickRequestEditText.setTextColor(getResources().getColor(R.color.white));
    kickHistoryEditText.setTextColor(getResources().getColor(R.color.white));
    kickResponseEditText.setTextColor(getResources().getColor(R.color.white));

    kickRequestEditText.setOnKeyListener(new View.OnKeyListener() {
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        // If the event is a key-down event on the "enter" button

        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

          return true;
        }
        return false;
      }
    });
    kickHistoryEditText.setOnKeyListener(new View.OnKeyListener() {
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        // If the event is a key-down event on the "enter" button

        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

          return true;
        }
        return false;
      }
    });
    kickResponseEditText.setOnKeyListener(new View.OnKeyListener() {
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        // If the event is a key-down event on the "enter" button

        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

          return true;
        }
        return false;
      }
    });
    doneBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String text1 = kickRequestEditText.getText().toString();
        String text2 = kickHistoryEditText.getText().toString();
        String text3 = kickResponseEditText.getText().toString();

        SettingsUtil.put("kickRequest", text1);
        SettingsUtil.put("kickHistory", text2);
        SettingsUtil.put("kickResponse", text3);

        onBackPressed();
      }
    });
  }
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    onBackPressed();
    return true;
  }

}
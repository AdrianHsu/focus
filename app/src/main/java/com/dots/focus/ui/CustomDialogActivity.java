package com.dots.focus.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dots.focus.R;

/**
 * Created by AdrianHsu on 2016/2/13.
 */

public class CustomDialogActivity extends Activity {

  Button btn_confirm;
  Button btn_cancel;
  TextView text_content;
  TextView text_title;
  TextView text_close;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_custom_dialog);

    btn_confirm = (Button)findViewById(R.id.btn_confirm);
    btn_cancel = (Button)findViewById(R.id.btn_cancel);
    text_close = (TextView)findViewById(R.id.text_close);
    text_title  = (TextView)findViewById(R.id.text_title);
    text_content = (TextView)findViewById(R.id.text_content);

    btn_confirm.setOnClickListener(new View.OnClickListener() {
      @Override            public void onClick(View v) {
        Toast.makeText(CustomDialogActivity.this, "you click confirm!", Toast.LENGTH_SHORT).show();
        finish();
      }
    });
    btn_cancel.setOnClickListener(new View.OnClickListener() {
      @Override            public void onClick(View v) {
        Toast.makeText(CustomDialogActivity.this,"you click cancel!",Toast.LENGTH_SHORT).show();
        finish();
      }
    });
    text_close.setOnClickListener(new View.OnClickListener() {
      @Override            public void onClick(View v) {
        Toast.makeText(CustomDialogActivity.this,"you click close!",Toast.LENGTH_SHORT).show();
        finish();
      }
    });
  }
}
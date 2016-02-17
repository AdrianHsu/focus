package com.dots.focus.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dots.focus.R;
import com.squareup.picasso.Picasso;

/**
 * Created by AdrianHsu on 2016/2/13.
 */

public class CustomDialogActivity extends Activity {

  Button btn_confirm;
  Button btn_cancel;
  TextView textContentTv;
  TextView textTitleTv;
  TextView textCloseTv;
  ImageView profileImage;
  String title = null;
  String alert = null;
  Long id = null;
  Context mContext = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_custom_dialog);

    btn_confirm = (Button)findViewById(R.id.btn_confirm);
    btn_cancel = (Button)findViewById(R.id.btn_cancel);
    textCloseTv = (TextView)findViewById(R.id.text_close);
    textTitleTv  = (TextView)findViewById(R.id.text_title);
    textContentTv = (TextView)findViewById(R.id.text_content);
    profileImage = (ImageView) findViewById(R.id.profile_image);
    mContext = this;

    btn_confirm.setOnClickListener(new View.OnClickListener() {
      @Override            public void onClick(View v) {
        Toast.makeText(CustomDialogActivity.this, "you click confirm!", Toast.LENGTH_SHORT).show();
//        Intent mIntent = new Intent(CustomDialogActivity.this, MainActivity.class);
//        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        CustomDialogActivity.this.startActivity(mIntent);
        finish();
      }
    });
    btn_cancel.setOnClickListener(new View.OnClickListener() {
      @Override            public void onClick(View v) {
        Toast.makeText(CustomDialogActivity.this,"you click cancel!",Toast.LENGTH_SHORT).show();
        finish();
      }
    });
    textCloseTv.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Toast.makeText(CustomDialogActivity.this, "you click close!", Toast.LENGTH_SHORT).show();
        finish();
      }
    });

    Bundle extras = getIntent().getExtras();
    if(extras != null) {
      title = extras.getString("title");
      alert = extras.getString("alert");
      id = extras.getLong("id");

      textTitleTv.setText(title);
      textContentTv.setText(alert);

      String url ="https://graph.facebook.com/" + String.valueOf(id)+
                              "/picture?type=large";
      Picasso.with(mContext).load(url).into(profileImage);
    }
  }
}
package com.dots.focus.ui;


import android.os.Bundle;

import com.dots.focus.R;
import com.dots.focus.controller.InboxController;
import com.dots.focus.model.InboxModel;
import java.util.ArrayList;


/**
 * Created by AdrianHsu on 2015/10/9.
 */
public class InboxActivity extends BaseActivity {

  static final String TAG = "InboxActivity";
  InboxController mInboxController = new InboxController();
  HorizontalListView mHorizontalListView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_inbox);

    mHorizontalListView = (HorizontalListView) findViewById(R.id.horizontalListView);

    super.createToolbarFragment();
  }
}

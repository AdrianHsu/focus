package com.dots.focus.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.dots.focus.R;
import com.dots.focus.ui.fragment.ToolbarBackArrowFragment;

/**
 * Created by AdrianHsu on 2015/10/11.
 */
public class InboxEditActivity extends AppCompatActivity {

  static final String TAG = "InboxEditActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_inbox_edit);
     createToolbarBackArrowFragment();
  }

  private void createToolbarBackArrowFragment() {
    FragmentManager fm = getSupportFragmentManager();
    Fragment fragment = fm.findFragmentById(R.id.frameToolbar);

    if(fragment == null) {
      fragment = new ToolbarBackArrowFragment();
      fm.beginTransaction()
        .add(R.id.frameToolbar, fragment)
        .commit();
    }
  }
}
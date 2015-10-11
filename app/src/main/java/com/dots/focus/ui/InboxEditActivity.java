package com.dots.focus.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.dots.focus.R;
import com.dots.focus.application.MainApplication;
import com.dots.focus.config.Config;
import com.dots.focus.ui.fragment.ToolbarBackArrowFragment;

/**
 * Created by AdrianHsu on 2015/10/11.
 */
public class InboxEditActivity extends BaseActivity {

  static final String TAG = "InboxEditActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_inbox_edit);
     createToolbarFragment();
  }

  @Override
  public void createToolbarFragment() {
    FragmentManager fm = getSupportFragmentManager();
    Fragment fragment = fm.findFragmentById(R.id.frameToolbar);

    if(fragment == null) {
      fragment = new ToolbarBackArrowFragment();
      fm.beginTransaction()
        .add(R.id.frameToolbar, fragment)
        .commit();
    }
  }
  @Override
  public void onBackPressed() {
//    //handle the back press, close the drawer first and if the drawer is closed close the activity
//    if (MainApplication.result != null && MainApplication.result.isDrawerOpen()) {
//      MainApplication.result.closeDrawer();
//    } else {
//      super.onBackPressed();
//      if(Config.getCurrentDrawerItem() == 0) {
//        Intent a = new Intent(Intent.ACTION_MAIN);
//        a.addCategory(Intent.CATEGORY_HOME);
//        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(a);
//      }
//      else{
        Intent intent = new Intent(this, InboxActivity.class);
        this.startActivity(intent);
//    }
  }
}
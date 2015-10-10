package com.dots.focus.ui;


import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.dots.focus.R;
import com.dots.focus.application.MainApplication;
import com.dots.focus.config.Config;
import com.dots.focus.ui.fragment.ToolbarFragment;

/**
 * Created by AdrianHsu on 15/9/23.
 */
public class BaseActivity extends AppCompatActivity {

  @Override
  public void onBackPressed() {
    //handle the back press, close the drawer first and if the drawer is closed close the activity
    if (MainApplication.result != null && MainApplication.result.isDrawerOpen()) {
      MainApplication.result.closeDrawer();
    } else {
//      super.onBackPressed();
      if(Config.getCurrentDrawerItem() == 0) {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
      }
      else{
        Intent intent = new Intent(this, DashboardActivity.class);
        this.startActivity(intent);

        Config.setCurrentDrawerItem(Config.DrawerItem.DASHBOARD.ordinal());
      }
    }
  }
  protected void createToolbarFragment() {

    FragmentManager fm = getSupportFragmentManager();
    Fragment fragment = fm.findFragmentById(R.id.frameToolbar);

    if(fragment == null) {
      fragment = new ToolbarFragment();
      fm.beginTransaction()
        .add(R.id.frameToolbar, fragment)
        .commit();
    }
  }
}

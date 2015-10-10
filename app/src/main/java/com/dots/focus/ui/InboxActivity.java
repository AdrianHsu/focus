package com.dots.focus.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.dots.focus.R;
import com.dots.focus.adapter.InboxArrayAdapter;
import com.dots.focus.application.MainApplication;
import com.dots.focus.controller.InboxController;
import com.dots.focus.model.InboxModel;
import com.dots.focus.ui.fragment.ToolbarFragment;

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


    ArrayList<InboxModel> arr = new ArrayList<>();
    for(int i = 0; i < 5; i++) {
      InboxModel tmp = new InboxModel("test" + i);
      arr.add(tmp);
    }
//    InboxArrayAdapter mInboxArrayAdapter = new InboxArrayAdapter(this, arr);
//    mHorizontalListView.setAdapter(mInboxArrayAdapter);


    createToolbarFrag();
  }

  @Override
  public void onBackPressed() {
    //handle the back press, close the drawer first and if the drawer is closed close the activity
    if (MainApplication.result != null && MainApplication.result.isDrawerOpen()) {
      MainApplication.result.closeDrawer();
    } else {
//      super.onBackPressed();
      Intent a = new Intent(Intent.ACTION_MAIN);
      a.addCategory(Intent.CATEGORY_HOME);
      a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(a);
    }
  }
  private void createToolbarFrag() {

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

package com.dots.focus.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.dots.focus.R;
import com.dots.focus.ui.fragment.ProfileDragFragment;


/**
 * Created by AdrianHsu on 2015/10/9.
 */
public class ProfileActivity extends BaseActivity {

  static final String TAG = "ProfileActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_profile);

    super.createToolbarFragment();
    initProfileDragTopLayoutFragment();
  }
  private void initProfileDragTopLayoutFragment() {

    FragmentManager fm = getSupportFragmentManager();
    Fragment fragment = fm.findFragmentById(R.id.pFrameDragTopLayout);

    if(fragment == null) {
      fragment = new ProfileDragFragment();
      fm.beginTransaction()
        .add(R.id.pFrameDragTopLayout, fragment)
        .commit();
    }
  }

}

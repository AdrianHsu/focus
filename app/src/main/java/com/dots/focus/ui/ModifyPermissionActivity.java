package com.dots.focus.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.dots.focus.R;
import com.squareup.picasso.Picasso;

/**
 * Created by AdrianHsu on 2016/2/15.
 */
public class ModifyPermissionActivity extends BaseActivity {

  private Toolbar toolbar;
  private String name;
  private Long id;
  private ImageView profileImage;
  private TextView profileNameTv;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_modify_permission);

    toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle(getResources().getString(R.string.modify_permission_text));
    profileImage = (ImageView) findViewById(R.id.profile_image);
    profileNameTv = (TextView) findViewById(R.id.profile_name);

    Bundle extras = getIntent().getExtras();
    if (extras != null) {
      name = extras.getString("user_name");
      id = extras.getLong("user_id");
    }

    String url = "https://graph.facebook.com/" + String.valueOf(id) +
                            "/picture?process=large";
    Picasso.with(this).load(url).into(profileImage);
    profileNameTv.setText(name);


  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    onBackPressed();
    return true;
  }

}

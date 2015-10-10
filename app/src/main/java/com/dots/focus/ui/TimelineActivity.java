package com.dots.focus.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.dots.focus.R;
import com.dots.focus.application.MainApplication;
import com.dots.focus.ui.fragment.ToolbarFragment;
import com.hhl.adapter.BaseAdapterHelper;
import com.hhl.adapter.QuickAdapter;
import com.dots.focus.model.PostModel;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adrian Hsu on 2015/10/10.
 */
public class TimelineActivity extends AppCompatActivity {

    private QuickAdapter<PostModel> mQuickAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        createToolbarFrag();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mQuickAdapter = new QuickAdapter<PostModel>(this, R.layout.post_item) {
            @Override
            protected void convert(BaseAdapterHelper helper, PostModel item) {
                helper.setText(R.id.tv_username, item.getUsername())
                        .setText(R.id.tv_location, item.getLocation())
                        .setText(R.id.tv_time, "9/7 10:35pm")
                        .setText(R.id.tv_post, item.getPost())
                        .setText(R.id.tv_reason, item.getReason())
                        .setText(R.id.btn_likes, item.getLikes() + " Likes")
                        .setText(R.id.btn_comments, item.getComments() + " Comments");
                ImageLoader.getInstance().displayImage(item.getAvatar(), helper
                  .getImageView(R
                    .id.iv_avatar));
            }
        };

        recyclerView.setAdapter(mQuickAdapter);

        initData();
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
    private void initData() {
        List<PostModel> list = new ArrayList<>();
        String userID = "1041982119159627";
        for (int i = 0; i < 20; i++) {
            PostModel post = new PostModel();
            post.setUsername("Harvey Yang");
            post.setLocation("near Neihu, Taipei");
            post.setPost("Deserve a kick on my ass now LOL");
            post.setReason("- Open app frequency exceeds");
            post.setAvatar("http://graph.facebook.com/" + userID + "/picture?type=large");
            post.setLikes(i);
            post.setComments(i);

            list.add(post);
        }
        mQuickAdapter.addAll(list);
    }
}

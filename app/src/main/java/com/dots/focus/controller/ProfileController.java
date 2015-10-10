package com.dots.focus.controller;

import android.content.Context;

import com.dots.focus.R;
import com.dots.focus.model.PostModel;
import com.hhl.adapter.BaseAdapterHelper;
import com.hhl.adapter.QuickAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AdrianHsu on 2015/10/10.
 */
public class ProfileController {

  static final String TAG = "ProfileController";

  public static QuickAdapter<PostModel> mQuickAdapter;

  public static void convertAdapter(Context _this) {
    mQuickAdapter = new QuickAdapter<PostModel>(_this, R.layout.post_item) {
      @Override
      protected void convert(BaseAdapterHelper helper, PostModel item) {
        helper.setText(R.id.tv_username, item.getUsername())
          .setText(R.id.tv_location, item.getLocation())
          .setText(R.id.tv_time, item.getAbsoluteTime())
          .setText(R.id.tv_post, item.getPost())
          .setText(R.id.tv_reason, item.getReason())
          .setText(R.id.btn_likes, item.getLikes() + " Likes")
          .setText(R.id.btn_comments, item.getComments() + " Comments");
        ImageLoader.getInstance().displayImage(item.getAvatar(), helper
          .getImageView(R
            .id.iv_avatar));
      }
    };
  }
  public static void initData() {
    List<PostModel> list = new ArrayList<>();
    String userID = "1041982119159627";
    for (int i = 0; i < 20; i++) {
      PostModel post = new PostModel();
      post.setUsername("Harvey Yang");
      post.setLocation("Neihu, Taipei");
      post.setPost("Deserve a kick on my ass now LOL");
      post.setReason("- Open app frequency exceeds");
      post.setAbsoluteTime("9/7 10:35pm");
      post.setAvatar("http://graph.facebook.com/" + userID + "/picture?type=large");
      post.setLikes(i);
      post.setComments(i);

      list.add(post);
    }
    mQuickAdapter.addAll(list);
  }
}

package com.dots.focus.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.dots.focus.R;
import com.dots.focus.controller.AddFriendController;
import com.dots.focus.controller.TimelineController;

/**
 * Created by Adrian Hsu on 2015/10/10.
 */
public class AddFriendActivity extends BaseActivity {

  static final String TAG = "AddFirendActivity";

  @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        super.createToolbarFragment();


//        RecyclerView respondRecyclerView = (RecyclerView) findViewById(R.id
//          .respond_friend_recyclerview);
//        respondRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager
//          .VERTICAL, false));
//
//        AddFriendController.convertrAdapter(this);
//        respondRecyclerView.setAdapter(AddFriendController.rQuickAdapter);
//        AddFriendController.initRespondFriendData();
//
//        RecyclerView addFriendRecyclerView = (RecyclerView) findViewById(R.id
//          .addFriendRecyclerview);
//        addFriendRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager
//          .VERTICAL, false));
//
//        AddFriendController.convertaAdapter(this);
//        addFriendRecyclerView.setAdapter(AddFriendController.aQuickAdapter);
//        AddFriendController.initAddFriendData();
    }

}

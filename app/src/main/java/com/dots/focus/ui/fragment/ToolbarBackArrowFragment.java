package com.dots.focus.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.dots.focus.R;
import com.dots.focus.application.MainApplication;
import com.dots.focus.config.Config;
import com.dots.focus.ui.AddFriendActivity;
import com.dots.focus.ui.DashboardActivity;
import com.dots.focus.ui.InboxActivity;
import com.dots.focus.ui.LoginActivity;
import com.dots.focus.ui.ProfileActivity;
import com.dots.focus.ui.SettingsActivity;
import com.dots.focus.ui.TimelineActivity;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.parse.ParseUser;


/**
 * Created by AdrianHsu on 2015/10/9.
 */
public class ToolbarBackArrowFragment extends Fragment {

  public static Toolbar toolbar;
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

  }
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_toolbar, parent, false);

    toolbar = (Toolbar) view.findViewById(R.id.tool_bar);
    TextView title = (TextView) toolbar.findViewById(R.id.tool_bar_id);
    title.setText(Config.Title[Config.getCurrentDrawerItem()]);

    ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");

    return view;
  }
}

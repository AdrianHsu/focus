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
import com.dots.focus.ui.DashboardActivity;
import com.dots.focus.ui.InboxActivity;
import com.dots.focus.ui.LoginActivity;
import com.dots.focus.ui.ProfileActivity;
import com.dots.focus.ui.SettingsActivity;
import com.dots.focus.ui.TimelineActivity;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
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
public class ToolbarFragment extends Fragment {

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
    //use textView instead of Title
//    ((AppCompatActivity) getActivity()).getSupportActionBar().setElevation(15); //1dp = 3px for xxhdpi

    createDrawer(savedInstanceState);
    return view;
  }
  // A method to find height of the status bar

  private void createDrawer(Bundle savedInstanceState) {

    buildProfile();
    buildHeader(false, savedInstanceState);
    buildDrawer(ToolbarFragment.toolbar, savedInstanceState);
  }

  private void buildProfile() {
    // dummy account
    MainApplication.profile = new ProfileDrawerItem().withName("Harvey Yang").withEmail
      ("harveyycyang@gmail" + ".com")
      .withIcon(getResources().getDrawable(R.drawable.profile));
  }

  private void buildDrawer(Toolbar toolbar, Bundle savedInstanceState) {

    //Create the drawer
    MainApplication.result = new DrawerBuilder()
      .withActivity(getActivity())
      .withToolbar(toolbar)
      .withAccountHeader(MainApplication.headerResult) //set the AccountHeader we created earlier
        // for the header
      .addDrawerItems(
        new PrimaryDrawerItem().withName(R.string.drawer_item_dashboard).withIcon(FontAwesome
          .Icon.faw_home).withIdentifier(0),
        new PrimaryDrawerItem().withName(R.string.drawer_item_inbox).withIcon(FontAwesome.Icon
          .faw_users).withIdentifier(1),
        new PrimaryDrawerItem().withName(R.string.drawer_item_timeline).withIcon(FontAwesome.Icon
          .faw_clock_o).withIdentifier(2),
        new PrimaryDrawerItem().withName(R.string.drawer_item_overview).withIcon(FontAwesome.Icon
          .faw_eye).withIdentifier(3),
        new PrimaryDrawerItem().withName(R.string.drawer_item_profile).withIcon(FontAwesome.Icon
          .faw_user).withIdentifier(4),
        new PrimaryDrawerItem().withName(R.string.drawer_item_logout).withIcon(FontAwesome.Icon
          .faw_sign_out).withIdentifier(5)
      ) // add the items we want to use with our Drawer
//      .withOnDrawerNavigationListener(new Drawer.OnDrawerNavigationListener() {
//        @Override
//        public boolean onNavigationClickListener(View clickedView) {
//          //this method is only called if the Arrow icon is shown. The hamburger is automatically managed by the MaterialDrawer
//          //if the back arrow is shown. close the activity
//          MainActivity.this.finish();
//          //return true if we have consumed the event
//          return true;
//        }
//      })
      .addStickyDrawerItems(
        new SecondaryDrawerItem().withName(R.string.drawer_item_settings).withIcon(FontAwesome
          .Icon.faw_cog).withIdentifier(6)
      )
      .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
        @Override
        public boolean onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
          //check if the drawerItem is set.
          //there are different reasons for the drawerItem to be null
          //--> click on the header
          //--> click on the footer
          //those items don't contain a drawerItem

          if (drawerItem != null) {
            Intent intent = null;
            if (drawerItem.getIdentifier() == 0) {
              intent = new Intent(getActivity(), DashboardActivity.class);
            } else if (drawerItem.getIdentifier() == 1) {
              intent = new Intent(getActivity(), InboxActivity.class);
            } else if (drawerItem.getIdentifier() == 2) {
              intent = new Intent(getActivity(), TimelineActivity.class);
            } else if (drawerItem.getIdentifier() == 3) {
//              intent = new Intent(DashboardActivity.this, OverviewActivity.class);
            } else if (drawerItem.getIdentifier() == 4) {
              intent = new Intent(getActivity(), ProfileActivity.class);
            } else if (drawerItem.getIdentifier() == 5) {
              logOut();
              intent = new Intent(getActivity(), LoginActivity.class);
              intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
              intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            } else if (drawerItem.getIdentifier() == 6) {
                intent = new Intent(getActivity(), SettingsActivity.class);
            }
            if (intent != null) {
              if(drawerItem.getIdentifier() == 5) {
                updateConfigDrawerItem(Config.DrawerItem.DASHBOARD.ordinal());
              } else {
                updateConfigDrawerItem(drawerItem.getIdentifier());
              }
              getActivity().startActivity(intent);
            }
          }

          return false;
        }
      })
      .withAnimateDrawerItems(true)
      .withSavedInstance(savedInstanceState)
      .withSelectedItem(Config.getCurrentDrawerItem()) //not consider click "back"
      .build();
  }
  /**
   * small helper method to reuse the logic to build the AccountHeader
   * this will be used to replace the header of the drawer with a compact/normal header
   *
   * @param compact
   * @param savedInstanceState
   */
  private void buildHeader(boolean compact, Bundle savedInstanceState) {
    // Create the AccountHeader
    MainApplication.headerResult = new AccountHeaderBuilder()
      .withActivity(getActivity())
      .withHeaderBackground(R.drawable.cover_photo) // not yet sync with facebook
      .withCompactStyle(compact)
      .addProfiles(
        MainApplication.profile // not yet sync with facebook
      )
      .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
        @Override
        public boolean onProfileChanged(View view, IProfile profile, boolean current) {

          //false if you have not consumed the event and it should close the drawer
          return false;
        }
      })
      .withSavedInstance(savedInstanceState)
      .build();
  }
  private void updateConfigDrawerItem(int i) {
    Config.setCurrentDrawerItem(i);
  }
  @Override
  public void onSaveInstanceState(Bundle outState) {
    //add the values which need to be saved from the drawer to the bundle
    outState = MainApplication.result.saveInstanceState(outState);
    //add the values which need to be saved from the accountHeader to the bundle
    outState = MainApplication.headerResult.saveInstanceState(outState);
    super.onSaveInstanceState(outState);
  }

  public void logOut() {
    ParseUser.logOut();
  }
}

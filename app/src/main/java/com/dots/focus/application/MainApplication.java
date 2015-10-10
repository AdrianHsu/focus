package com.dots.focus.application;

/**
 * Created by AdrianHsu on 15/8/14.
 */

import android.app.Application;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import com.dots.focus.config.Config;
import com.facebook.FacebookSdk;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.squareup.picasso.Picasso;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;


public class MainApplication extends Application {

  static final String TAG = "MainApplication";
  //save our header or result
  public static AccountHeader headerResult = null;
  public static Drawer result = null;
  public static IProfile profile;

  @Override
  public void onCreate() {
    super.onCreate();
    FacebookSdk.sdkInitialize(getApplicationContext());

//    Parse.enableLocalDatastore(this);
    Parse.initialize(this,
      Config.FOCUS_APPLICATION_ID,
      Config.FOCUS_CLIENT_ID
    );

    ParseFacebookUtils.initialize(this);
    initDrawerImageLoader();
    initImageLoader(getApplicationContext());

  }

  private void initDrawerImageLoader() {
    //initialize and create the image loader logic
    DrawerImageLoader.init(new DrawerImageLoader.IDrawerImageLoader() {
      @Override
      public void set(ImageView imageView, Uri uri, Drawable placeholder) {
        Picasso.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
      }

      @Override
      public void cancel(ImageView imageView) {
        Picasso.with(imageView.getContext()).cancelRequest(imageView);
      }

      @Override
      public Drawable placeholder(Context ctx) {
        return null;
      }
    });
  }

    private void initImageLoader(Context context) {
      ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
      config.threadPriority(Thread.NORM_PRIORITY - 2);
      config.denyCacheImageMultipleSizesInMemory();
      config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
      config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
      config.tasksProcessingOrder(QueueProcessingType.LIFO);
      config.writeDebugLogs(); // Remove for release app

      // Initialize ImageLoader with configuration.
      ImageLoader.getInstance().init(config.build());
    }

}


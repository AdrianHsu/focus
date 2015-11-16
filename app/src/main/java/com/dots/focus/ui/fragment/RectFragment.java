package com.dots.focus.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.dots.focus.R;

/**
 * Created by AdrianHsu on 2015/11/16.
 */
public class RectFragment extends Fragment {
  // save a reference so custom methods
  // can access views
  private View topLevelView;

  // save a reference to show the pie chart
  private WebView webview;

  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    super.onCreateView(inflater, container,
      savedInstanceState);

    boolean attachToRoot = false;
    topLevelView = inflater.inflate(
      R.layout.fragment_webview_rect,
      container,
      attachToRoot);

    webview = (WebView) topLevelView.findViewById(
      R.id.pie_chart_webview);

    WebSettings webSettings =
      webview.getSettings();

    webSettings.setJavaScriptEnabled(true);


    webview.setWebChromeClient(
      new WebChromeClient());

    webview.setWebViewClient(new WebViewClient() {
      @Override
      public void onPageFinished(
        WebView view,
        String url) {

      }
    });

    // note the mapping
    // from  file:///android_asset
    // to PieChartExample/assets
    webview.loadUrl("file:///android_asset/" +
      "html/rect.html");

    webview.setBackgroundColor(Color.TRANSPARENT);
    webview.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);

    return topLevelView;
  }
}

package com.dots.focus.ui.fragment;

/**
 * Created by AdrianHsu on 15/9/27.
 */
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dots.focus.R;
import com.gc.materialdesign.views.Button;

public class SampleSlide extends Fragment {

  private static final String ARG_LAYOUT_RES_ID = "layoutResId";
  private Button accessBtn;
  private Button genderBtn;

  public static SampleSlide newInstance(int layoutResId) {
    SampleSlide sampleSlide = new SampleSlide();

    Bundle args = new Bundle();
    args.putInt(ARG_LAYOUT_RES_ID, layoutResId);
    sampleSlide.setArguments(args);

    return sampleSlide;
  }

  private int layoutResId;
  public SampleSlide() {}

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if(getArguments() != null && getArguments().containsKey(ARG_LAYOUT_RES_ID))
      layoutResId = getArguments().getInt(ARG_LAYOUT_RES_ID);
  }g

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

    View view = inflater.inflate(layoutResId, container, false);

    if(layoutResId == R.layout.set_info_access) {
      accessBtn = (Button) view.findViewById(R.id.button_setting);
      accessBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
          startActivity(intent);
        }
      });
    } else if (layoutResId == R.layout.set_info_email) {

    } else if (layoutResId == R.layout.set_info_gender) {

//      genderBtn = (Button) view.findViewById(R.id.button_gender).setOnClickListener(new View
//        .OnClickListener() {
//        @Override
//        public void onClick(View v) {
//          new MaterialDialog.Builder(this)
//            .title(R.string.title)
//            .items(R.array.items)
//            .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
//              @Override
//              public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
//                /**
//                 * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
//                 * returning false here won't allow the newly selected radio button to actually be selected.
//                 **/
//                return true;
//              }
//            })
//            .positiveText(R.string.choose)
//            .show();
//        }
//      });

    } else if (layoutResId == R.layout.set_info_birth) {

    } else if (layoutResId == R.layout.set_info_occupation) {

    } else if (layoutResId == R.layout.set_info_welcome) {
      // do nothing
    }

    return view;
  }

}
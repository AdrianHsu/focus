package com.dots.focus.ui.fragment;

/**
 * Created by AdrianHsu on 15/9/27.
 */
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dots.focus.R;
import com.dots.focus.util.CreateInfoUtil;
import com.parse.ParseObject;

public class CreateInfoSlide extends Fragment {

  private static final String TAG = "CreateInfoSlide";
  private static final String ARG_LAYOUT_RES_ID = "layoutResId";
  private Button accessBtn;
  private Button genderBtn;
  private Button birthBtn;
  private Button occupationBtn;
  private EditText emailEdt;

  public static CreateInfoSlide newInstance(int layoutResId) {
    CreateInfoSlide setInfoSlide = new CreateInfoSlide();

    Bundle args = new Bundle();
    args.putInt(ARG_LAYOUT_RES_ID, layoutResId);
    setInfoSlide.setArguments(args);

    return setInfoSlide;
  }

  private int layoutResId;
  public CreateInfoSlide() {}

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if(getArguments() != null && getArguments().containsKey(ARG_LAYOUT_RES_ID))
      layoutResId = getArguments().getInt(ARG_LAYOUT_RES_ID);
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

    View view = inflater.inflate(layoutResId, container, false);

    switch(layoutResId){
      case R.layout.set_info_access:
        accessBtn = (Button) view.findViewById(R.id.button_setting);
        accessBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
          }
        });
        break;

      case R.layout.set_info_email:
        emailEdt = (EditText) view.findViewById(R.id.edt_email);
        emailEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
          @Override
          public void onFocusChange(View v, boolean hasFocus) {
        /* When focus is lost check that the text field
        * has valid values.
        */
            if (!hasFocus && emailEdt != null) {
              String email = emailEdt.getText().toString();
              Log.v(TAG, email);
              CreateInfoUtil.setUserInfo("Email", email, false);
            }
          }
        });
        break;

      case R.layout.set_info_gender:
        genderBtn = (Button) view.findViewById(R.id.button_gender);
        CreateInfoUtil.setUserInfo("Gender", (String)genderBtn.getText(), false);
        genderBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            createGenderDialog();
          }
        });
        break;

      case R.layout.set_info_birth:
        birthBtn = (Button) view.findViewById(R.id.button_birth);
        CreateInfoUtil.setUserInfo("Birth", (String)birthBtn.getText(), false);
        birthBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            createBirthDialog();
          }
        });
        break;

      case R.layout.set_info_occupation:
        occupationBtn = (Button) view.findViewById(R.id.button_occupation);
        CreateInfoUtil.setUserInfo("Occupation", (String)occupationBtn.getText(), false);
        occupationBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            createOccupationDialog();
          }
        });
        break;
      //case R.layout.set_info_welcome:
    }

    return view;
  }
  private void createGenderDialog() {
    new MaterialDialog.Builder(getActivity())
      .title("Gender")
      .items(R.array.gender)
      .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
        @Override
        public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
          /**
           * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
           * returning false here won't allow the newly selected radio button to actually be selected.
           **/
          if (genderBtn != null && text != null) {
            String temp = text.toString();
            genderBtn.setText(temp);
            CreateInfoUtil.setUserInfo("Gender", temp, false);
          }
          return true;
        }
      })
      .positiveText("Done")
      .show();
  }
  private void createBirthDialog() {
    new MaterialDialog.Builder(getActivity())
      .title("Year Of Birth")
      .items(R.array.birth)
      .itemsCallback(new MaterialDialog.ListCallback() {
        @Override
        public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

          if (birthBtn != null && text != null) {
            String temp = text.toString();
            birthBtn.setText(temp);
            CreateInfoUtil.setUserInfo("Birth", temp, false);
          }
        }
      })
      .positiveText("Done")
      .show();
  }
  private void createOccupationDialog() {
    new MaterialDialog.Builder(getActivity())
      .title("Occupation")
      .items(R.array.occupation)
      .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
        @Override
        public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
          /**
           * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
           * returning false here won't allow the newly selected radio button to actually be selected.
           **/
          if (occupationBtn != null && text != null) {
            String temp = text.toString();
            occupationBtn.setText(temp);
            CreateInfoUtil.setUserInfo("Occupation", temp, false);
          }
          return true;
        }
      })
      .positiveText("Done")
      .show();
  }
}
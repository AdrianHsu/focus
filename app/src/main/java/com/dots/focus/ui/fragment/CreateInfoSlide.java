package com.dots.focus.ui.fragment;

/**
 * Created by AdrianHsu on 15/9/27.
 */
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;
import com.rey.material.widget.Slider;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.dots.focus.R;
import com.dots.focus.service.GetAppsService;
import com.dots.focus.ui.IdleSettingsActivity;
import com.dots.focus.util.CreateInfoUtil;
import com.dots.focus.util.FetchAppUtil;
import com.dots.focus.util.SettingsUtil;
import com.github.mikephil.charting.data.Entry;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public class CreateInfoSlide extends Fragment {

  private static final String TAG = "CreateInfoSlide";
  private static final String ARG_LAYOUT_RES_ID = "layoutResId";
  private Button accessBtn;
  private Button genderBtn;
  private Button birthBtn;
  private Button occupationBtn;
  private EditText emailEdt;
  private Slider slider1;
  private TextView textView1;
  private Button doneBtn1;
  private Slider slider2;
  private TextView textView2;
  private TextView appPickedTv;
  private Button doneBtn2;
  private Button pickAppBtn;
  private TextView textView3;
  private TextView typeTextView;
  private Button doneBtn3;
  private Button cancelBtn3;
  private Button lockBtn3;
  private Slider slider3;

  private static Context mContext;
  public static Integer[] defaultMultiChoice = null;
  public static Integer[] pickedMultiChoice = null;
  public static int progressGoal;
  public static int progressIdle;
  public static int progressLock;
  public static boolean friendLock;
  private int layoutResId;


  public static CreateInfoSlide newInstance(int layoutResId, Context _this) {
    CreateInfoSlide setInfoSlide = new CreateInfoSlide();

    Bundle args = new Bundle();
    args.putInt(ARG_LAYOUT_RES_ID, layoutResId);
    setInfoSlide.setArguments(args);
    mContext = _this;

    return setInfoSlide;
  }

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
    progressGoal = SettingsUtil.getInt("goal");
    progressLock = SettingsUtil.getInt("lock");
    progressIdle = SettingsUtil.getInt("idle");
    switch(layoutResId){
      case R.layout.set_info_access:
        accessBtn = (Button) view.findViewById(R.id.acc_button_setting);
        accessBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
          }
        });
        break;

      case R.layout.set_info_basic:
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
        genderBtn = (Button) view.findViewById(R.id.button_gender);
        CreateInfoUtil.setUserInfo("Gender", (String)genderBtn.getText(), false);
        genderBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            createGenderDialog();
          }
        });
        birthBtn = (Button) view.findViewById(R.id.button_birth);
        CreateInfoUtil.setUserInfo("Birth", (String)birthBtn.getText(), false);
        birthBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            createBirthDialog();
          }
        });
        occupationBtn = (Button) view.findViewById(R.id.button_occupation);
        CreateInfoUtil.setUserInfo("Occupation", (String)occupationBtn.getText(), false);
        occupationBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            createOccupationDialog();
          }
        });
        break;
      case R.layout.set_goal:

        slider1 = (Slider) view.findViewById(R.id.slider1);
        textView1 = (TextView) view.findViewById(R.id.textView1);
        slider1.setValue(progressGoal, true);

        // Initialize the textview with '0'.
        textView1.setText(slider1.getValue() + "/" + slider1.getMaxValue() + " (以分鐘計)");

        slider1.setOnPositionChangeListener(new Slider.OnPositionChangeListener() {
          @Override
          public void onPositionChanged(Slider view, boolean fromUser, float oldPos, float newPos, int oldValue, int newValue) {
            progressGoal = newValue;
            textView1.setText(newValue + "/" + slider1.getMaxValue() + " (以分鐘計)");
            Toast.makeText(mContext, "Changing slider's progress", Toast.LENGTH_SHORT).show();

          }
        });

        break;
      case R.layout.set_idle:
        slider2 = (Slider) view.findViewById(R.id.slider1);
        textView2 = (TextView) view.findViewById(R.id.textView1);
        appPickedTv = (TextView) view.findViewById(R.id.app_picked);
        pickAppBtn = (Button) view.findViewById(R.id.pick_app_button);
        progressIdle = SettingsUtil.getInt("idle");
        slider2.setValue(progressIdle, true);
        // Adrian: 連續要改掉..

        pickAppBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Log.d(TAG, "pickAppBtn clicked...");
            createPickAppDialog();
          }
        });
        // Initialize the textview with '0'.
        textView2.setText(slider2.getValue() + "/" + slider2.getMaxValue() + " (以分鐘計)");

        slider2.setOnPositionChangeListener(new Slider.OnPositionChangeListener() {
          @Override
          public void onPositionChanged(Slider view, boolean fromUser, float oldPos, float newPos, int oldValue, int newValue) {
            progressIdle = newValue;
            textView2.setText(newValue + "/" + slider2.getMaxValue() + " (以分鐘計)");
            Toast.makeText(mContext, "Changing slider's progress", Toast.LENGTH_SHORT).show();
          }
        });

        break;

      case R.layout.set_lock:
        slider3 = (Slider) view.findViewById(R.id.slider1);
        ParseUser user = ParseUser.getCurrentUser();

        final int temp = user.getInt("lock_max_period");
        progressLock = temp;
        slider3.setValue(temp, true);
        textView3 = (TextView) view.findViewById(R.id.textView1);
        typeTextView = (TextView) view.findViewById(R.id.type);
        lockBtn3 = (Button) view.findViewById(R.id.lock_condition_button);
        Boolean mFriendLock = SettingsUtil.getBooleen("friendLock");
        if(mFriendLock)
          typeTextView.setText(getResources().getString(R.string.lock_friend_to_self));
        else
          typeTextView.setText(getResources().getString(R.string.lock_never));

        // Initialize the textview with '0'.
        textView3.setText(slider3.getValue() + "/" + slider3.getMaxValue() + " (以分鐘計)");

        slider3.setOnPositionChangeListener(new Slider.OnPositionChangeListener() {
          @Override
          public void onPositionChanged(Slider view, boolean fromUser, float oldPos, float newPos, int oldValue, int newValue) {
            progressLock = newValue;
            textView3.setText(newValue + "/" + slider3.getMaxValue() + " (以分鐘計)");
            Toast.makeText(mContext, "Changing slider's progress", Toast.LENGTH_SHORT).show();
          }
        });
        lockBtn3.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            createLockConditionDialog();
          }
        });
      break;
    }

    return view;
  }
  public static void onDonePressed() {
    SettingsUtil.put("goal", progressGoal);
    SettingsUtil.put("idle", progressIdle);
    SettingsUtil.put("friendLock", friendLock);
    ParseUser user = ParseUser.getCurrentUser();
    user.put("lock_max_period", progressLock);
  }


  private void createLockConditionDialog() {
    new MaterialDialog.Builder(mContext)
                            .title("選擇您欲使用的鎖屏監護")
                            .items(R.array.lockConditionList)
                            .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                              @Override
                              public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                /**
                                 * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                                 * returning false here won't allow the newly selected radio button to actually be selected.
                                 **/
                                if (typeTextView != null && text != null) {
                                  String temp = text.toString();
                                  typeTextView.setText(temp);
                                }
                                if(which == 0)
                                  friendLock = true;
                                else if(which == 1)
                                  friendLock = false;
                                return true;
                              }
                            })
                            .positiveText("完成").show();
  }
  private void createPickAppDialog() {
    Log.d(TAG, "createPickAppDialog state 0...");

    final int length = FetchAppUtil.getSize();
    final String [] appNameList = new String [length];
    for (int i = 0; i < length; ++i)
      appNameList[i] = FetchAppUtil.getApp(i).getName();

    defaultMultiChoice = IdleSettingsActivity.defaultMultiChoice;
    pickedMultiChoice = defaultMultiChoice;

    appPickedTv.setText(getExcludedApps(appNameList, defaultMultiChoice, length));

    new MaterialDialog.Builder(mContext)
            .title("選擇您欲排除的應用軟體")
            .items(appNameList)
            .itemsCallbackMultiChoice(defaultMultiChoice,
                    new MaterialDialog.ListCallbackMultiChoice() {
              @Override
              public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                Integer[] selected = dialog.getSelectedIndices();
                if (selected != null) {
                  for (int i = 0, length = selected.length; i < length; ++i)
                    Log.d(TAG, "selected[i]: " + selected[i]);
                }
                for (int i = 0, length = which.length; i < length; ++i)
                  Log.d(TAG, "which[i]: " + which[i]);
                for (int i = 0, length = text.length; i < length; ++i)
                  Log.d(TAG, "text[i] " + text[i].toString());

                pickedMultiChoice = which;
                return true;
              }
            })
            .dismissListener(new DialogInterface.OnDismissListener() {
              @Override
              public void onDismiss(DialogInterface dialogInterface) {
                Log.v(TAG, "on dismiss");
                defaultMultiChoice = pickedMultiChoice;
                appPickedTv.setText(getExcludedApps(appNameList, defaultMultiChoice, length));

                IdleSettingsActivity.defaultMultiChoice = defaultMultiChoice;
              }
            })

            .cancelListener(new DialogInterface.OnCancelListener() {
              @Override
              public void onCancel(DialogInterface dialogInterface) {
                pickedMultiChoice = defaultMultiChoice;
              }
            })

            .positiveText("完成")
            .alwaysCallMultiChoiceCallback() // the callback will always be
                                    // called, to check if selection is still allowed
            .show();
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
  public static String getExcludedApps(String[] appNameList, Integer[] defaultMultiChoice,
                       int length) {
    boolean flag = false;
    String text = "";
    if (defaultMultiChoice == null) return text;
    for (int i = 0, size = defaultMultiChoice.length; i < size; ++i) {
      Log.d(TAG, "defaultMultiChoice[i]: " + defaultMultiChoice[i]);
      if (defaultMultiChoice[i] < length) {
        if (flag) text += ", ";
        text += appNameList[defaultMultiChoice[i]];
        flag = true;
      }
    }
    return text;
  }
}
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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.dots.focus.R;
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
  private SeekBar seekBar1;
  private TextView textView1;
  private Button doneBtn1;
  private SeekBar seekBar2;
  private TextView textView2;
  private TextView appPickedTv;
  private Button doneBtn2;
  private Button pickAppBtn;
  private TextView textView3;
  private TextView typeTextView;
  private Button doneBtn3;
  private Button cancelBtn3;
  private Button lockBtn3;
  private SeekBar seekBar3;

  private static Context mContext;
  public static Integer[] defaultMultiChoice;
  public static Integer[] pickedMultiChoice;
  private int progressGoal;
  private int progressIdle;
  private int progressLock;
  private int layoutResId;

  private int APP_LENGTH = 0;

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

        seekBar1 = (SeekBar) view.findViewById(R.id.seekBar1);
        textView1 = (TextView) view.findViewById(R.id.textView1);
        doneBtn1 =(Button) view.findViewById(R.id.button);
        seekBar1.setProgress(progressGoal);

        // Initialize the textview with '0'.
        textView1.setText(seekBar1.getProgress() + "/" + seekBar1.getMax() + " (以分鐘計)");

        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

          @Override
          public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
            progressGoal = progresValue;
            textView1.setText(seekBar1.getProgress() + "/" + seekBar1.getMax() + " (以分鐘計)");
            Toast.makeText(mContext, "Changing seekbar's progress", Toast.LENGTH_SHORT).show();
          }

          @Override
          public void onStartTrackingTouch(SeekBar seekBar) {
            Toast.makeText(mContext, "Started tracking seekbar", Toast.LENGTH_SHORT).show();
          }

          @Override
          public void onStopTrackingTouch(SeekBar seekBar) {
            textView1.setText(seekBar1.getProgress() + "/" + seekBar1.getMax() + " (以分鐘計)");
            Toast.makeText(mContext, "Stopped tracking seekbar", Toast.LENGTH_SHORT).show();
          }
        });

        doneBtn1.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            SettingsUtil.put("goal", progressGoal);

          }
        });
        break;
      case R.layout.set_idle:
        seekBar2 = (SeekBar) view.findViewById(R.id.seekBar1);
        textView2 = (TextView) view.findViewById(R.id.textView1);
        appPickedTv = (TextView) view.findViewById(R.id.app_picked);
        doneBtn2 = (Button) view.findViewById(R.id.button);
        pickAppBtn = (Button) view.findViewById(R.id.pick_app_button);
        progressIdle = SettingsUtil.getInt("idle");
        seekBar2.setProgress(progressIdle);
        // Adrian: 連續要改掉..

        pickAppBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Log.d(TAG, "pickAppBtn clicked...");
            createPickAppDialog(appPickedTv);
          }
        });
        // Initialize the textview with '0'.
        textView2.setText(seekBar2.getProgress() + "/" + seekBar2.getMax() + " (以分鐘計)");

        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
          @Override
          public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
            progressIdle = progresValue;
            textView2.setText(seekBar2.getProgress() + "/" + seekBar2.getMax() + " (以分鐘計)");
            Toast.makeText(mContext, "Changing seekbar's progress", Toast.LENGTH_SHORT).show();
          }

          @Override
          public void onStartTrackingTouch(SeekBar seekBar) {
            Toast.makeText(mContext, "Started tracking seekbar", Toast.LENGTH_SHORT).show();
          }

          @Override
          public void onStopTrackingTouch(SeekBar seekBar) {
            textView2.setText(seekBar2.getProgress() + "/" + seekBar2.getMax() + " (以分鐘計)");
            Toast.makeText(mContext, "Stopped tracking seekbar", Toast.LENGTH_SHORT).show();
          }
        });

        doneBtn2.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            SettingsUtil.put("idle", progressIdle);
          }
        });
        break;

      case R.layout.set_lock:
        seekBar3 = (SeekBar) view.findViewById(R.id.seekBar1);
        final int temp = SettingsUtil.getInt("lock");
        progressLock = temp;
        seekBar3.setProgress(temp);
        textView3 = (TextView) view.findViewById(R.id.textView1);
        typeTextView = (TextView) view.findViewById(R.id.type);
        doneBtn3 =(Button) view.findViewById(R.id.button);
        cancelBtn3 = (Button) view.findViewById(R.id.cancel_button);
        lockBtn3 = (Button) view.findViewById(R.id.lock_condition_button);

        // Initialize the textview with '0'.
        textView3.setText(seekBar3.getProgress() + "/" + seekBar3.getMax() + " (以分鐘計)");

        seekBar3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

          @Override
          public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
            progressLock = progresValue;
            textView3.setText(seekBar3.getProgress() + "/" + seekBar3.getMax() + " (以分鐘計)");
            Toast.makeText(mContext, "Changing seekbar's progress", Toast.LENGTH_SHORT).show();
          }

          @Override
          public void onStartTrackingTouch(SeekBar seekBar) {
            Toast.makeText(mContext, "Started tracking seekbar", Toast.LENGTH_SHORT).show();
          }

          @Override
          public void onStopTrackingTouch(SeekBar seekBar) {
            textView3.setText(seekBar3.getProgress() + "/" + seekBar3.getMax() + " (以分鐘計)");
            Toast.makeText(mContext, "Stopped tracking seekbar", Toast.LENGTH_SHORT).show();
          }
        });

        doneBtn3.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            SettingsUtil.put("lock", progressLock);
          }
        });
        cancelBtn3.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
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
                                return true;
                              }
                            })
                            .positiveText("完成").show();
  }
  private void createPickAppDialog(final TextView appPickedTv) {
    Log.d(TAG, "createPickAppDialog state 0...");

    final int length = FetchAppUtil.getSize();
    APP_LENGTH = length;
    final String [] appNameList = new String [length];
    defaultMultiChoice = new Integer[length];
    pickedMultiChoice = new Integer[length];
    for(int i = 0; i < length; i++) {
      appNameList[i] = FetchAppUtil.getApp(i).getName();
      Log.d(TAG, "appName " + i + ": " + appNameList[i]);
    }

    Log.d(TAG, "APP_LENGTH: " + APP_LENGTH);

    new MaterialDialog.Builder(mContext)
                            .title("選擇您欲排除的應用軟體")
                            .items(appNameList)
                            .itemsCallbackMultiChoice(defaultMultiChoice, new MaterialDialog
                                                    .ListCallbackMultiChoice() {
                              @Override
                              public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                for(int i = 0 ; i < APP_LENGTH; i++) {
                                  if(i < which.length) {
                                    Log.v(TAG, "test on Selection: " + i + ", which.length: " +
                                                            which.length);
                                    pickedMultiChoice[i] = which[i];
                                  }
                                  else
                                    pickedMultiChoice[i] = null;
                                }
                                return true;
                              }
                            })
                            .dismissListener(new DialogInterface.OnDismissListener() {
                              @Override
                              public void onDismiss(DialogInterface dialogInterface) {
                                Log.v(TAG, "on dismiss");
                                defaultMultiChoice = pickedMultiChoice;
                                appPickedTv.setText(Arrays.toString(appNameList));

                              }
                            })
                            .cancelListener(new DialogInterface.OnCancelListener() {
                              @Override
                              public void onCancel(DialogInterface dialogInterface) {
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
}
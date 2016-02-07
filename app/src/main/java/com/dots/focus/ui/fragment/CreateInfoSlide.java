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

        final SeekBar seekBar1 = (SeekBar) view.findViewById(R.id.seekBar1);
        seekBar1.setProgress(progressGoal);
        final TextView textView1 = (TextView) view.findViewById(R.id.textView1);
        final Button doneBtn1 =(Button) view.findViewById(R.id.button);

        // Initialize the textview with '0'.
        textView1.setText(seekBar1.getProgress() + "/" + seekBar1.getMax() + " (以分鐘計)");

        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

          @Override
          public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
            progressGoal = progresValue;
            textView1.setText("Covered: " + progressGoal + "/" + seekBar.getMax());
            Toast.makeText(mContext, "Changing seekbar's progress", Toast.LENGTH_SHORT).show();
          }

          @Override
          public void onStartTrackingTouch(SeekBar seekBar) {
            Toast.makeText(mContext, "Started tracking seekbar", Toast.LENGTH_SHORT).show();
          }

          @Override
          public void onStopTrackingTouch(SeekBar seekBar) {
            textView1.setText("Covered: " + progressGoal + "/" + seekBar.getMax());
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
        final SeekBar seekBar2 = (SeekBar) view.findViewById(R.id.seekBar1);
        progressIdle = SettingsUtil.getInt("idle");
        seekBar2.setProgress(progressIdle);
        // Adrian: 連續要改掉..
        final TextView textView2 = (TextView) view.findViewById(R.id.textView1);
        final TextView appPickedTv = (TextView) view.findViewById(R.id.app_picked);
        final Button doneBtn2 = (Button) view.findViewById(R.id.button);
        final Button pickAppBtn = (Button) view.findViewById(R.id.pick_app_button);

        final int length = FetchAppUtil.getSize();
        APP_LENGTH = length;
        final String [] appNameArray = new String [length];
        defaultMultiChoice = new Integer[length];
        pickedMultiChoice = new Integer[length];
        for(int i = 0; i < length; i++) {
          appNameArray[i] = FetchAppUtil.getApp(i).getName();
        }
        pickAppBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            createPickAppDialog(appNameArray, appPickedTv);
          }
        });
        // Initialize the textview with '0'.
        textView2.setText(seekBar2.getProgress() + "/" + seekBar2.getMax() + " (以分鐘計)");

        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
          @Override
          public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
            progressIdle = progresValue;
            textView2.setText("Covered: " + progressIdle + "/" + seekBar.getMax());
            Toast.makeText(mContext, "Changing seekbar's progress", Toast.LENGTH_SHORT).show();
          }

          @Override
          public void onStartTrackingTouch(SeekBar seekBar) {
            Toast.makeText(mContext, "Started tracking seekbar", Toast.LENGTH_SHORT).show();
          }

          @Override
          public void onStopTrackingTouch(SeekBar seekBar) {
            textView2.setText("Covered: " + progressIdle + "/" + seekBar.getMax());
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
        final SeekBar seekBar3 = (SeekBar) view.findViewById(R.id.seekBar1);
        final int temp = SettingsUtil.getInt("lock");
        progressLock = temp;
        seekBar3.setProgress(temp);
        final TextView textView3 = (TextView) view.findViewById(R.id.textView1);
        final TextView typeTextView = (TextView) view.findViewById(R.id.type);
        final Button doneBtn3 =(Button) view.findViewById(R.id.button);
        final Button cancelBtn3 = (Button) view.findViewById(R.id.cancel_button);
        final Button lockBtn3 = (Button) view.findViewById(R.id.lock_condition_button);

        // Initialize the textview with '0'.
        textView3.setText(seekBar3.getProgress() + "/" + seekBar3.getMax() + " (以分鐘計)");

        seekBar3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

          @Override
          public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
            progressLock = progresValue;
            textView3.setText("Covered: " + progressLock + "/" + seekBar.getMax());
            Toast.makeText(mContext, "Changing seekbar's progress", Toast.LENGTH_SHORT).show();
          }

          @Override
          public void onStartTrackingTouch(SeekBar seekBar) {
            Toast.makeText(mContext, "Started tracking seekbar", Toast.LENGTH_SHORT).show();
          }

          @Override
          public void onStopTrackingTouch(SeekBar seekBar) {
            textView3.setText("Covered: " + progressLock + "/" + seekBar.getMax());
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
            createLockConditionDialog(typeTextView);
          }
        });
      break;
    }

    return view;
  }
  private void createLockConditionDialog(final TextView typeTextView) {
    new MaterialDialog.Builder(mContext)
                            .title("選擇您欲使用的鎖屏監護")
                            .items(R.array.lockConditionList)
                            .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                              @Override
                              public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                /**
                                 * If you use alwaysCallMultiChoiceCallback(), which is discussed below,
                                 * returning false here won't allow the newly selected check box to actually be selected.
                                 * See the limited multi choice dialog example in the sample project for details.
                                 **/
                                typeTextView.setText(text.toString());
                                return true;
                              }
                            })
                            .positiveText("完成").show();
  }
  private void createPickAppDialog(final String[] appNameList, final TextView appPickedTv) {
    new MaterialDialog.Builder(mContext)
                            .title("選擇您欲排除的應用軟體")
                            .items(appNameList)
                            .itemsCallbackMultiChoice( defaultMultiChoice, new MaterialDialog
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
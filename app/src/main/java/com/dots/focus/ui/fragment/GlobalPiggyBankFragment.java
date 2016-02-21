package com.dots.focus.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dots.focus.R;
import com.dots.focus.adapter.GlobalPiggyBankRecyclerViewAdapter;
import com.dots.focus.util.TrackAccessibilityUtil;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class GlobalPiggyBankFragment extends Fragment {

  private Context mContext;
  private UltimateRecyclerView mRecyclerView;
  private LinearLayoutManager linearLayoutManager;
  private GlobalPiggyBankRecyclerViewAdapter globalPiggyBankRecyclerViewAdapter;
  private TextView totalTv;
  public static Double total;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    mContext = getActivity();
    View v = inflater.inflate(R.layout.fragment_piggy_bank_global, container, false);

    if (savedInstanceState == null) {

      totalTv = (TextView) v.findViewById(R.id.total);

      long[] data = TrackAccessibilityUtil.getSavedTimeAndRank();
      total = (double)data[3];

      totalTv.setText(String.valueOf(data[3]));

      final ArrayList<JSONObject> convertList = new ArrayList<>();

      JSONObject soccer = new JSONObject();
      JSONObject book = new JSONObject();
      JSONObject basketball = new JSONObject();
      JSONObject workout = new JSONObject();
      JSONObject jogging = new JSONObject();
      JSONObject diet = new JSONObject();
      JSONObject movie = new JSONObject();
      try {
        soccer.put("id", R.drawable.icon_soccer);
        soccer.put("convert", 1.5);
        soccer.put("text", getResources().getString(R.string.unit_soccer));
        book.put("id", R.drawable.icon_book);
        book.put("convert", 3);
        book.put("text", getResources().getString(R.string.unit_book));
        basketball.put("id", R.drawable.icon_basketball);
        basketball.put("convert", 0.66); // 40 mins
        basketball.put("text", getResources().getString(R.string.unit_basketball));
        workout.put("id", R.drawable.icon_workout);
        workout.put("convert", 0.83); // 50 mins
        workout.put("text", getResources().getString(R.string.unit_workout));
        jogging.put("id", R.drawable.icon_jogging);
        jogging.put("convert", 0.5);
        jogging.put("text", getResources().getString(R.string.unit_jogging));
        diet.put("id", R.drawable.icon_diet);
        diet.put("convert", 0.116);
        diet.put("text", getResources().getString(R.string.unit_diet));
        movie.put("id", R.drawable.icon_movie);
        movie.put("convert", 1.66);
        movie.put("text", getResources().getString(R.string.unit_movie));

      } catch (JSONException e) {
        Log.d("Piggy", e.getMessage());
      }

      convertList.add(soccer);
      convertList.add(book);
      convertList.add(basketball);
      convertList.add(workout);
      convertList.add(jogging);
      convertList.add(diet);
      convertList.add(movie);

      mRecyclerView = (UltimateRecyclerView) v.findViewById(R.id
                              .global_piggy_bank_recycler_view);
      globalPiggyBankRecyclerViewAdapter = new GlobalPiggyBankRecyclerViewAdapter( convertList,
                              mContext);
      linearLayoutManager = new LinearLayoutManager(mContext);

      mRecyclerView.setLayoutManager(linearLayoutManager);
      mRecyclerView.setAdapter(globalPiggyBankRecyclerViewAdapter);
    }
    return v;
  }
}
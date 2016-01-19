package com.dots.focus.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dots.focus.R;
import com.dots.focus.adapter.GlobalPiggyBankRecyclerViewAdapter;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GlobalPiggyBankFragment extends Fragment {

  private Context mContext;
  private UltimateRecyclerView mRecyclerView;
  private LinearLayoutManager linearLayoutManager;
  private GlobalPiggyBankRecyclerViewAdapter globalPiggyBankRecyclerViewAdapter;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    mContext = getActivity();
    View v = inflater.inflate(R.layout.fragment_piggy_bank_global, container, false);

    if (savedInstanceState == null) {

      final ArrayList<JSONObject> transformList = new ArrayList<>();

      for(int i = 0; i < 15; i++) {
        JSONObject transform = new JSONObject();
        try {
          transform.put("appName", "Facebook");
//      app.put(icon); // put icon
        } catch (JSONException e) {
          e.printStackTrace();
        }

        transformList.add(transform);
      }

      mRecyclerView = (UltimateRecyclerView) v.findViewById(R.id
                              .global_piggy_bank_recycler_view);
      globalPiggyBankRecyclerViewAdapter = new GlobalPiggyBankRecyclerViewAdapter( transformList,
                              mContext);
      linearLayoutManager = new LinearLayoutManager(mContext);

      mRecyclerView.setLayoutManager(linearLayoutManager);
      mRecyclerView.setAdapter(globalPiggyBankRecyclerViewAdapter);
    }
    return v;
  }
}
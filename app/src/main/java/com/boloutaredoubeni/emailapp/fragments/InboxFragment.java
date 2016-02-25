package com.boloutaredoubeni.emailapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Copyright 2016 Boloutare Doubeni
 *
 * A fragment that shows the aggregation of recent emails
 */
public class InboxFragment extends Fragment {

  private OnEmailClickListener mListener;

  public InboxFragment() {}

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    return super.onCreateView(inflater, container, savedInstanceState);
  }

  @Override
  public void onAttach(Context context) {
    try {
      mListener = (OnEmailClickListener)getActivity();
    } catch (ClassCastException ex) {
      ex.printStackTrace();
    }
  }

  public interface OnEmailClickListener { void setEmail(String emailID); }
}

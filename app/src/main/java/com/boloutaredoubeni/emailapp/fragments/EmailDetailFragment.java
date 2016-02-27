package com.boloutaredoubeni.emailapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.boloutaredoubeni.emailapp.models.Email;
import com.boloutaredoubeni.emailapp.R;

/**
 * Copyright 2016 Boloutare Doubeni
 *
 * A fragment that shows the detail of the email to the user
 */
public class EmailDetailFragment extends Fragment {

  private Email mEmail;

  public EmailDetailFragment() {}

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_email_detail, container, false);

    if (mEmail != null) {
      ((TextView)view.findViewById(R.id.msg_txt)).setText(mEmail.getBody());
    }
    return view;
  }

  public void setEmail(Email email) {
    mEmail = email;
  }
}

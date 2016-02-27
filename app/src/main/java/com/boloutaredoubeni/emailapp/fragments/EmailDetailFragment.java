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

  public static EmailDetailFragment newInstance(int index) {
    EmailDetailFragment fragment = new EmailDetailFragment();
    Bundle args = new Bundle();
    args.putInt(InboxFragment.EMAIL_POSITION, index);
    fragment.setArguments(args);

    return fragment;
  }

  public EmailDetailFragment() {}

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mEmail = (Email) getArguments().getSerializable("Email");
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view =
        inflater.inflate(R.layout.fragment_email_detail, container, false);

    if (mEmail != null) {
      ((TextView)view.findViewById(R.id.msg_txt)).setText(mEmail.getBody());
    }
    return view;
  }

  public void setEmail(Email email) { mEmail = email; }
}

package com.boloutaredoubeni.emailapp.viewmodels;

import android.databinding.ObservableField;
import android.text.Editable;
import android.text.TextWatcher;

import com.boloutaredoubeni.emailapp.models.Email;

/**
 * Copyright 2016 Boloutare Doubeni
 */
public class EmailViewModel {

  public EmailViewModel() {
    to = new ObservableField<>();
    to.set("");

    subject = new ObservableField<>();
    subject.set("");

    body  = new ObservableField<>();
    body.set("");
  }

  public ObservableField<String> to;
  public TextWatcher toFieldWatcher = new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
      if (!to.get().equals(s.toString())) {
        to.set(s.toString());
      }
    }
  };

  public ObservableField<String> subject;
  public TextWatcher subjectWatcher = new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
      if (!subject.get().equals(s.toString())) {
        subject.set(s.toString());
      }
    }
  };

  public ObservableField<String> body;
  public TextWatcher bodyWatcher = new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
      if (!body.get().equals(s.toString())) {
        body.set(s.toString());
      }
    }
  };

  public Email emitEmail() {
    return new Email(null, to.get(), null, null, null, subject.get(), body.get(), null);
  }
}

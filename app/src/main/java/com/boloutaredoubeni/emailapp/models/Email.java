package com.boloutaredoubeni.emailapp.models;

/**
 * Copyright 2016 Boloutare Doubeni
 */
public class Email {

  private String mId;
  private String mText;

  public Email(String id, String body) {
    mId = id;
    mText = body;
  }

  public String getBody() {
    return mText;
  }
}

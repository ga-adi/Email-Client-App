package com.boloutaredoubeni.emailapp.models;

import java.io.Serializable;

/**
 * Copyright 2016 Boloutare Doubeni
 */
public class Email implements Serializable {

  private static final long serialVersionUID = -6099312954099962806L;

  private String mId;
  private String mText;

  public Email(String id, String body) {
    mId = id;
    mText = body;
  }

  public String getBody() { return mText; }
}

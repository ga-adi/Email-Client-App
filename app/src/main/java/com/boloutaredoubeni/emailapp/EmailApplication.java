package com.boloutaredoubeni.emailapp;

import android.app.Application;

import com.google.api.services.gmail.GmailScopes;

/**
 * Copyright 2016 Boloutare Doubeni
 */
public class EmailApplication extends Application {
  public static final String PREF_ACCOUNT_NAME = "accountName";
  private static final String[] SCOPES = {GmailScopes.GMAIL_READONLY};

  public static String[] getScopes() { return SCOPES; }
}

package com.boloutaredoubeni.emailapp.activities;

import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.boloutaredoubeni.emailapp.R;
import com.boloutaredoubeni.emailapp.fragments.InboxFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.gmail.GmailScopes;

import java.util.Arrays;

/**
 * Copyright 2016 Boloutare Doubeni
 */
public class MainActivity extends AppCompatActivity {

  public static final int REQUEST_ACCOUNT_PICKER = 1000;
  public static final int REQUEST_AUTHORIZATION = 1001;
  public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
  private static final String PREF_ACCOUNT_NAME = "accountName";
  private static final String[] SCOPES = {GmailScopes.GMAIL_LABELS};

  private GoogleAccountCredential mCredential;

  public GoogleAccountCredential getCredential() { return mCredential; }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
    mCredential = GoogleAccountCredential.usingOAuth2(getApplicationContext(),
                                                      Arrays.asList(SCOPES))
                      .setBackOff(new ExponentialBackOff())
                      .setSelectedAccountName(
                          settings.getString(PREF_ACCOUNT_NAME, null));

    FragmentManager manager = getSupportFragmentManager();
    FragmentTransaction transaction = manager.beginTransaction();
    InboxFragment inboxFragment = new InboxFragment();
    transaction.add(inboxFragment, "inboxfrag");
    transaction.commit();
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (isGooglePlayServicesAvailable()) {
      refreshResults();
    } else {
      CoordinatorLayout layout =
          (CoordinatorLayout)findViewById(R.id.snackbar_layout);
      Snackbar.make(layout,
                    "Google Play Services required: "
                        + "after installing, close and relaunch this app.",
                    Snackbar.LENGTH_LONG)
          .show();
    }
  }

  /**
   * Called when an activity launched here (specifically, AccountPicker
   * and authorization) exits, giving you the requestCode you started it with,
   * the resultCode it returned, and any additional data from it.
   * @param requestCode code indicating which activity result is incoming.
   * @param resultCode code indicating the result of the incoming
   *     activity result.
   * @param data Intent (containing result data) returned by incoming
   *     activity result.
   */
  @Override
  protected void onActivityResult(int requestCode, int resultCode,
                                  Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
    case REQUEST_GOOGLE_PLAY_SERVICES:
      if (resultCode != RESULT_OK) {
        isGooglePlayServicesAvailable();
      }
      break;
    case REQUEST_ACCOUNT_PICKER:
      if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
        String accountName =
            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        if (accountName != null) {
          mCredential.setSelectedAccountName(accountName);
          SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
          SharedPreferences.Editor editor = settings.edit();
          editor.putString(PREF_ACCOUNT_NAME, accountName);
          editor.apply();
        }
      } else if (resultCode == RESULT_CANCELED) {
        Log.d("Main Activity", "Account unspecified.");
      }
      break;
    case REQUEST_AUTHORIZATION:
      if (resultCode != RESULT_OK) {
        chooseAccount();
      }
      break;
    }

    super.onActivityResult(requestCode, resultCode, data);
  }

  private boolean isGooglePlayServicesAvailable() {
    final int connectionStatusCode =
        GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
    if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
      showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
      return false;
    } else if (connectionStatusCode != ConnectionResult.SUCCESS) {
      return false;
    }
    return true;
  }

  public void showGooglePlayServicesAvailabilityErrorDialog(
      final int connectionStatusCode) {
    Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
        connectionStatusCode, this, MainActivity.REQUEST_GOOGLE_PLAY_SERVICES);
    dialog.show();
  }

  /**
   * Attempt to get a set of data from the Gmail API to display. If the
   * email address isn't known yet, then call chooseAccount() method so the
   * user can pick an account.
   *
   * Prompt the fragment to load the data
   */
  private void refreshResults() {
    if (mCredential.getSelectedAccountName() == null) {
      chooseAccount();
    }
  }

  /**
   * Checks whether the device currently has a network connection.
   * @return true if the device has a network connection, false otherwise.
   */
  public boolean isDeviceOnline() {
    ConnectivityManager connMgr =
        (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
    return (networkInfo != null && networkInfo.isConnected());
  }

  /**
   * Starts an activity in Google Play Services so the user can pick an
   * account.
   */
  private void chooseAccount() {
    startActivityForResult(mCredential.newChooseAccountIntent(),
                           REQUEST_ACCOUNT_PICKER);
  }
}

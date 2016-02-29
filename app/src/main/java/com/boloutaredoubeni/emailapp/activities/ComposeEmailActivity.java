package com.boloutaredoubeni.emailapp.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.boloutaredoubeni.emailapp.R;

/**
 * An activity for composing emails
 * This activity should navigable from the main activity and from a reply action
 */
public class ComposeEmailActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_compose_email);
  }

  /**
   * Send a user email to the correct recipient, for now It should only support
   * starting a new email thread
   */
  public class SendEmailTask extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... params) {
      return null;
    }
  }
}

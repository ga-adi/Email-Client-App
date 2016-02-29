package com.boloutaredoubeni.emailapp.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.boloutaredoubeni.emailapp.EmailApplication;
import com.boloutaredoubeni.emailapp.R;
import com.boloutaredoubeni.emailapp.databinding.ActivityComposeEmailBinding;
import com.boloutaredoubeni.emailapp.models.Email;
import com.boloutaredoubeni.emailapp.viewmodels.EmailViewModel;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.gmail.model.Message;

import java.io.IOException;
import java.util.Arrays;

/**
 * An activity for composing emails
 * This activity should navigable from the main activity and from a reply action
 */
public class ComposeEmailActivity extends AppCompatActivity {

  private GoogleAccountCredential mCredential;
  private EmailViewModel mViewModel;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
    mCredential =
        GoogleAccountCredential.usingOAuth2(
                                   getApplicationContext(),
                                   Arrays.asList(EmailApplication.getScopes()))
            .setBackOff(new ExponentialBackOff())
            .setSelectedAccountName(
                settings.getString(EmailApplication.PREF_ACCOUNT_NAME, null));

    ActivityComposeEmailBinding binding =
        DataBindingUtil.setContentView(this, R.layout.activity_compose_email);
    mViewModel = new EmailViewModel();
    binding.setVm(mViewModel);
    binding.emailEdit.addTextChangedListener(mViewModel.toFieldWatcher);
    binding.subjectEdit.addTextChangedListener(mViewModel.subjectWatcher);
    binding.bodyEdit.addTextChangedListener(mViewModel.bodyWatcher);
  }

  public void onSendEmail(View v) {
    Email email = mViewModel.emitEmail();
    if (email.isValid()) {
      new SendEmailTask(mCredential).execute(email);
      return;
    }
    Toast.makeText(this, "Something ain't right", Toast.LENGTH_SHORT).show();
  }

  /**
   * Send a user email to the correct recipient, for now It should only support
   * starting a new email thread
   */
  public class SendEmailTask extends AsyncTask<Email, Void, Void> {
    private com.google.api.services.gmail.Gmail mService = null;
    private Exception mLastError = null;

    public SendEmailTask(GoogleAccountCredential credential) {
      HttpTransport transport = AndroidHttp.newCompatibleTransport();
      JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
      mService = new com.google.api.services.gmail.Gmail.Builder(transport,
                                                                 jsonFactory,
                                                                 credential)
                     .setApplicationName("Email App")
                     .build();
    }

    @Override
    protected Void doInBackground(Email... params) {
      try {
        sendEmail(params[0]);
      } catch (IOException e) {
        mLastError = e;
        cancel(true);
        mLastError.printStackTrace();
      } finally {
        return null;
      }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
      super.onPostExecute(aVoid);
      Toast.makeText(ComposeEmailActivity.this, "Email sent!!!",
                     Toast.LENGTH_SHORT)
          .show();
    }

    @Override
    protected void onCancelled() {
      // TODO: implement me
    }

    private void sendEmail(Email email) throws IOException {
      String user = "me";
      Message message = Email.createMessageFrom(email);
      message = mService.users().messages().send(user, message).execute();
    }
  }
}

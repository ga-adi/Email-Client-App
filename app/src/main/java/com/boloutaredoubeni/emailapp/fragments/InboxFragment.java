package com.boloutaredoubeni.emailapp.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.boloutaredoubeni.emailapp.activities.MainActivity;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright 2016 Boloutare Doubeni
 *
 * A fragment that shows the aggregation of recent emails
 */
public class InboxFragment extends Fragment {

  private OnEmailClickListener mListener;

  private ListView mListView;

  public InboxFragment() {}

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // TODO: init the list view
    // TODO: set the adapter
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


  private class MessageTask extends AsyncTask<Void, Void, List<Message>> {
    private com.google.api.services.gmail.Gmail mService = null;
    private Exception mLastError = null;

    public MessageTask(GoogleAccountCredential credential) {
      HttpTransport transport = AndroidHttp.newCompatibleTransport();
      JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
      mService = new com.google.api.services.gmail.Gmail.Builder(transport,
                                                                 jsonFactory,
                                                                 credential)
                     .setApplicationName("Email App")
                     .build();
    }

    @Override
    protected List<Message> doInBackground(Void... params) {
      try {
        return getInboxMessages();
      } catch (IOException e) {
        mLastError = e;
        cancel(true);
        return null;
      }
    }

    @Override
    protected void onCancelled() {
      // TODO: update the UI to reflect a cancelled request
      if (mLastError != null) {
        if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
          ((MainActivity)getActivity()).showGooglePlayServicesAvailabilityErrorDialog(
              ((GooglePlayServicesAvailabilityIOException) mLastError)
                  .getConnectionStatusCode());
        } else if (mLastError instanceof UserRecoverableAuthIOException) {
          startActivityForResult(
              ((UserRecoverableAuthIOException)mLastError).getIntent(),
              MainActivity.REQUEST_AUTHORIZATION);
        } else {
          mLastError.printStackTrace();
        }
      } else {
        Log.d("InboxFragment", "getting inbox data cancelled");
      }
    }

    /**
     * Get the email messages for the user's account
     *
     * @throws IOException
     */
    private List<Message> getInboxMessages() throws IOException {
      String user = "me";
      List<Message> messages = new ArrayList<>();

      ListMessagesResponse response =
          mService.users().messages().list(user).execute();

      while (response.getMessages() != null) {
        messages.addAll(response.getMessages());
        // Move to the next page and get the messages
        if (response.getNextPageToken() != null) {
          String pageToken = response.getNextPageToken();
          response = mService.users()
                         .messages()
                         .list(user)
                         .setPageToken(pageToken)
                         .execute();
        } else {
          break;
        }
      }

      return messages;
    }


  }
}

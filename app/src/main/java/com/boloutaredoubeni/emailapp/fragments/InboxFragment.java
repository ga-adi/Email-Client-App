package com.boloutaredoubeni.emailapp.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.boloutaredoubeni.emailapp.R;
import com.boloutaredoubeni.emailapp.activities.MainActivity;
import com.boloutaredoubeni.emailapp.models.Email;
import com.boloutaredoubeni.emailapp.views.adapters.InboxAdapter;
import com.boloutaredoubeni.emailapp.views.listeners.InboxItemClickedListener;
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

/**
 * Copyright 2016 Boloutare Doubeni
 *
 * A fragment that shows the aggregation of recent emails
 */
public class InboxFragment extends Fragment {

  private OnEmailClickListener mListener;

  private RecyclerView mRecyclerView;
  private InboxAdapter mAdapter;
  private ProgressBar mProgressBar;

  public InboxFragment() {}

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view =  inflater.inflate(R.layout.inbox_fragment, container, false);
    mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
    mRecyclerView = (RecyclerView) view.findViewById(R.id.inbox_recycler);
    mRecyclerView.addOnItemTouchListener(new InboxItemClickedListener(getContext(), new InboxItemClickedListener.OnItemClickListener() {
      @Override
      public void onItemClick(View view, int position) {
        // TODO: Move to next fragment
        Toast.makeText(getContext(), "Clicked on Item", Toast.LENGTH_SHORT).show();
      }
    }));
    return view;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    mAdapter = new InboxAdapter(new ArrayList<Email>());
    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
    mRecyclerView.setAdapter(mAdapter);
    mRecyclerView.setLayoutManager(layoutManager);
  }

  @Override
  public void onResume() {
    super.onResume();
    if (((MainActivity)getActivity())
            .getCredential()
            .getSelectedAccountName() != null) {
      if (((MainActivity)getActivity()).isDeviceOnline()) {
        new MessageTask(((MainActivity)getActivity()).getCredential())
            .execute();
      } else {
        Log.e("InboxFragment", "No network connection available.");
      }
    }
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    try {
      mListener = (OnEmailClickListener)getActivity();
    } catch (ClassCastException ex) {
      ex.printStackTrace();
    }
  }

  public interface OnEmailClickListener { void setEmail(String emailID); }

  private class MessageTask extends AsyncTask<Void, Void, ArrayList<Email>> {
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
    protected ArrayList<Email> doInBackground(Void... params) {
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
          ((MainActivity)getActivity())
              .showGooglePlayServicesAvailabilityErrorDialog(
                  ((GooglePlayServicesAvailabilityIOException)mLastError)
                      .getConnectionStatusCode());
        } else if (mLastError instanceof UserRecoverableAuthIOException) {
          startActivityForResult(
              ((UserRecoverableAuthIOException)mLastError).getIntent(),
              MainActivity.REQUEST_AUTHORIZATION);
        } else {
          mLastError.printStackTrace();
        }
      } else {
        Log.e("InboxFragment", "getting inbox data cancelled");
      }
    }

    @Override
    protected void onPostExecute(ArrayList<Email> messages) {
      super.onPostExecute(messages);
      mProgressBar.setVisibility(View.GONE);
      mAdapter.addMessages(messages);
    }

    /**
     * Get the email messages for the user's account
     *
     * @throws IOException
     */
    private ArrayList<Email> getInboxMessages() throws IOException {
      String user = "me";
      ArrayList<Message> messages = new ArrayList<>();
      ArrayList<Email> emails = new ArrayList<>();

      ListMessagesResponse response =
          mService.users().messages().list(user).execute();

//      while (response.getMessages() != null) {
        messages.addAll(response.getMessages());
        // Move to the next page and get the messages
//        if (response.getNextPageToken() != null) {
//          String pageToken = response.getNextPageToken();
//          response = mService.users()
//                         .messages()
//                         .list(user)
//                         .setPageToken(pageToken)
//                         .execute();
//        } else {
//          break;
//        }
//      }

      for (final Message message : messages) {
        String id = message.getId();
        Message msg = mService.users().messages().get(user, message.getId()).execute();
        emails.add(new Email(id, msg.getSnippet()));
      }

      return emails;
    }
  }
}

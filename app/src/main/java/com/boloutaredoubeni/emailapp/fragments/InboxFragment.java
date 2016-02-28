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
  private int mCurrentEmailPosition = 0;
  static final String EMAIL_POSITION = "3m41l";

  private RecyclerView mRecyclerView;
  private InboxAdapter mAdapter;
  private ProgressBar mProgressBar;

  public InboxFragment() {}

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_inbox, container, false);
    mProgressBar = (ProgressBar)view.findViewById(R.id.progress_bar);
    mRecyclerView = (RecyclerView)view.findViewById(R.id.inbox_recycler);
    mRecyclerView.addOnItemTouchListener(new InboxItemClickedListener(
        getContext(), new InboxItemClickedListener.OnItemClickListener() {
          @Override
          public void onItemClick(View view, int position) {
            // TODO: Move to next fragment
            Toast.makeText(getContext(), "Clicked on Item", Toast.LENGTH_SHORT)
                .show();

            mListener.onEmailSelected(mAdapter.getEmailAt(position));
          }
        }));
    return view;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    mAdapter = new InboxAdapter(new ArrayList<Email>(), getContext());
    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
    mRecyclerView.setAdapter(mAdapter);
    mRecyclerView.setLayoutManager(layoutManager);

    if (((MainActivity)getActivity()).isDualPaned()) {
      // TODO: indicate the selected item
      // Something like listView.setChoiceMode()
      // TODO: update the UI to show the last selected Email or the first if
      // there is none or noe if there are no emails
    } else {
    }
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
      mListener = (OnEmailClickListener)context;
    } catch (ClassCastException ex) {
      throw new ClassCastException(context.toString() + " must implement " +
                                   OnEmailClickListener.class.getName());
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putInt(EMAIL_POSITION, mCurrentEmailPosition);
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }

  /**
   * FIXME: Retain emails in a database some how and check that first
   *
   * The activity that implements this interface must send the data to the
   * detail activity
   */
  public interface OnEmailClickListener { void onEmailSelected(Email email); }

  /**
   * An AsyncTask that retrieves the data from the GMail API
   */
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

      ListMessagesResponse response = mService.users()
                                          .messages()
                                          .list(user)
                                          .setIncludeSpamTrash(false)
                                          .execute();
      messages.addAll(response.getMessages());

      for (final Message message : messages) {
        String id = message.getId();
        Message msg =
            mService.users().messages().get(user, message.getId()).execute();

        Email email = Email.createFrom(msg);
        emails.add(email);
      }

      return emails;
    }
  }
}

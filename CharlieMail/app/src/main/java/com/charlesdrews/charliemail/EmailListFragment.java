package com.charlesdrews.charliemail;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

/**
 * Contains a RecyclerView of emails from a specified list: inbox, drafts, or sent
 * Created by charlie on 2/25/16.
 */
public class EmailListFragment extends Fragment {
    private List<String> mLabels;
    private ArrayList<Email> mEmails;
    private EmailRecyclerAdapter mAdapter;
    private GoogleAccountCredential mCredential;
    private ProgressBar mProgressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ListsPagerAdapter.SELECTED_TAB_KEY)) {
            int selectedTab = getArguments().getInt(ListsPagerAdapter.SELECTED_TAB_KEY);

            mLabels = new ArrayList<String>();
            mLabels.add(ListsPagerAdapter.GMAIL_LABELS[selectedTab]);

            mEmails = new ArrayList<>();
            mAdapter = new EmailRecyclerAdapter(mEmails);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCredential = ((MainActivity) getActivity()).getCredential();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mCredential.getSelectedAccountName() == null) {
            ((MainActivity) getActivity()).chooseAccount();
        } else {
            if (isDeviceOnline()) {
                new GetEmailListAsyncTask(mCredential).execute();
            } else {
                Toast.makeText(getActivity(), "No network connection available.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_email_list, container, false);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        recyclerView.setAdapter(mAdapter);

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.email_list_progress_bar);

        return rootView;
    }

    private class GetEmailListAsyncTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.gmail.Gmail mService = null;
        private Exception mLastError = null;

        public GetEmailListAsyncTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.gmail.Gmail.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName(getString(R.string.app_name))
                    .build();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            fadeInProgressBar(1000);
        }

        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                Log.e("EmailListFragment", e.getMessage());
                e.printStackTrace();
                cancel(true);
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<String> emails) {
            super.onPostExecute(emails);

            fadeOutProgressBar(1000);

            mEmails.clear();
            if (emails != null) {
                for (String email : emails) {
                    mEmails.add(new Email(email));
                }
            }

            mAdapter.notifyDataSetChanged();
        }

        private List<String> getDataFromApi() throws IOException {
            String userId = "me"; // special value - indicates authenticated user

            ArrayList<String> emails = new ArrayList<>();
            ListMessagesResponse responses = mService.users().messages()
                    .list(userId).setLabelIds(mLabels).execute();

            if (responses.getMessages() != null) {
                for (Message response : responses.getMessages()) {

                    Message message = mService.users().messages().get(userId, response.getId())
                            .setFormat("raw").execute();
                    byte[] emailBytes = Base64.decodeBase64(message.getRaw());
                    Properties props = new Properties();
                    Session session = Session.getDefaultInstance(props, null);

                    try {
                        MimeMessage email = new MimeMessage(session, new ByteArrayInputStream(emailBytes));
                        emails.add(email.getSubject());
                    } catch (MessagingException e) {
                        Log.e("EmailListFragment", e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
            return emails;
        }

        @Override
        protected void onCancelled() {
            fadeOutProgressBar(1000);

            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    ((MainActivity) getActivity()).showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            MainActivity.REQUEST_AUTHORIZATION);
                } else {
                    Toast.makeText(getContext(),
                            "The following error occurred:\n" + mLastError.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();
                }
            } else {
                Toast.makeText(getContext(), "Request cancelled.", Toast.LENGTH_SHORT).show();
            }
        }

        private void fadeInProgressBar(int durationInMs) {
            mProgressBar.animate()
                    .alpha(1f)
                    .setDuration(durationInMs)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                            mProgressBar.setAlpha(0f);
                            mProgressBar.setVisibility(View.VISIBLE);
                        }
                    });
        }

        private void fadeOutProgressBar(int durationInMs) {
            mProgressBar.animate()
                    .alpha(0f)
                    .setDuration(durationInMs)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mProgressBar.setVisibility(View.GONE);
                        }
                    });
        }
    }
}

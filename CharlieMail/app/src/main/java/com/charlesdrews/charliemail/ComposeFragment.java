package com.charlesdrews.charliemail;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.gmail.model.Draft;

import org.apache.commons.validator.routines.EmailValidator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Allow user to compose a new email
 * Created by charlie on 2/28/16.
 */
public class ComposeFragment extends Fragment {
    private static final String SEND = "send";
    private static final String DRAFT = "draft";

    private GoogleAccountCredential mCredential;
    private EditText mTo, mCc, mSubject, mBody;
    private Button mSend, mDraft;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SharedPreferences settings = getContext().getSharedPreferences(
                MainActivity.SHARED_PREFS_KEY, Context.MODE_PRIVATE);

        mCredential = GoogleAccountCredential.usingOAuth2(
                getContext().getApplicationContext(), Arrays.asList(MainActivity.SCOPES))
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(settings.getString(MainActivity.PREF_ACCOUNT_NAME, null));

        if (mCredential.getSelectedAccountName() == null) {
            startActivity(new Intent(getContext(), MainActivity.class));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_compose, container, false);

        mTo = (EditText) rootView.findViewById(R.id.compose_to);
        mCc = (EditText) rootView.findViewById(R.id.compose_cc);
        mSubject = (EditText) rootView.findViewById(R.id.compose_subject);
        mBody = (EditText) rootView.findViewById(R.id.compose_body);
        mSend = (Button) rootView.findViewById(R.id.send_button);
        mDraft = (Button) rootView.findViewById(R.id.draft_button);

        // if draft exists, populate EditTexts with draft input
        if (getArguments().containsKey(MainActivity.SELECTED_EMAIL_KEY)) {
            Email email = getArguments().getParcelable(MainActivity.SELECTED_EMAIL_KEY);
            if (!email.getTo().isEmpty()) { mTo.setText(email.getTo()); }
            if (!email.getCc().isEmpty()) { mTo.setText(email.getCc()); }
            if (!email.getSubject().isEmpty()) { mSubject.setText(email.getSubject()); }
            if (!email.getBody().isEmpty()) { mBody.setText(email.getBody()); }
        }

        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInputs()) {
                    new CreateEmailAsyncTask(mCredential).execute(SEND);
                }
            }
        });

        mDraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CreateEmailAsyncTask(mCredential).execute(DRAFT);
            }
        });
        return rootView;
    }

    private boolean checkInputs() {
        String emailErrMsg = "Invalid email address";
        String blankSubjectOrBodyErrMsg = "Cannot be blank";
        String emailSeparators = ",|;";
        EmailValidator validator = EmailValidator.getInstance();

        if (mTo.getText().toString().isEmpty()) {
            mTo.setError(emailErrMsg);
            mTo.requestFocus();
            return false;
        } else {
            String[] toEmails = mTo.getText().toString().split(emailSeparators);
            for (String email : toEmails) {
                if (!validator.isValid(email)) {
                    mTo.setError(emailErrMsg);
                    mTo.requestFocus();
                    return false;
                }
            }
        }

        if (!mCc.getText().toString().isEmpty()) {
            String[] ccEmails = mCc.getText().toString().split(emailSeparators);
            for (String email : ccEmails) {
                if (!validator.isValid(email)) {
                    mCc.setError(emailErrMsg);
                    mCc.requestFocus();
                    return false;
                }
            }
        }

        if (mSubject.getText().toString().isEmpty()) {
            mSubject.setError(blankSubjectOrBodyErrMsg);
            mSubject.requestFocus();
            return false;
        }

        if (mBody.getText().toString().isEmpty()) {
            mBody.setError(blankSubjectOrBodyErrMsg);
            mBody.requestFocus();
            return false;
        }

        return true;
    }

    private class CreateEmailAsyncTask extends AsyncTask<String, Void, Boolean> {
        private com.google.api.services.gmail.Gmail mService = null;
        private Exception mLastError = null;
        private String mAction;

        public CreateEmailAsyncTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.gmail.Gmail.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName(getString(R.string.app_name))
                    .build();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            if (params.length == 0) {
                return false;
            }
            mAction = params[0];

            String userId = "me"; // special value - indicates authenticated user

            try {
                MimeMessage mimeMessage = createMimeMessage();
                com.google.api.services.gmail.model.Message message =
                        createMessage(mimeMessage);

                switch (mAction) {
                    case SEND:
                        message = mService.users().messages().send(userId, message).execute();
                        return message.getLabelIds().contains(ListsPagerAdapter.GMAIL_LABELS[2]); //SENT
                    case DRAFT:
                        Draft draft = new Draft();
                        draft.setMessage(message);
                        draft = mService.users().drafts().create(userId, draft).execute();
                        return draft.getId() != null; // null if not successful
                }
            } catch (Exception e) {
                mLastError = e;
                e.printStackTrace();
                return false;
            }
            return false;
        }

        private MimeMessage createMimeMessage() throws AddressException, MessagingException {
            Properties properties = new Properties();
            Session session = Session.getDefaultInstance(properties, null);
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(mCredential.getSelectedAccountName()));
            message.addRecipients(Message.RecipientType.TO, mTo.getText().toString());
            message.addRecipients(Message.RecipientType.CC, mCc.getText().toString());
            message.setSubject(mSubject.getText().toString());
            message.setText(mBody.getText().toString());

            return message;
        }

        private com.google.api.services.gmail.model.Message createMessage(MimeMessage mimeMessage)
                throws IOException, MessagingException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            mimeMessage.writeTo(baos);
            String encodedEmail = Base64.encodeBase64URLSafeString(baos.toByteArray());
            com.google.api.services.gmail.model.Message message =
                    new com.google.api.services.gmail.model.Message();
            message.setRaw(encodedEmail);
            return message;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (aBoolean) {
                // api request was successful
                switch (mAction) {
                    case SEND:
                        Toast.makeText(getContext(), "Email sent", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getContext(), MainActivity.class));
                        return;
                    case DRAFT:
                        Toast.makeText(getContext(), "Draft saved", Toast.LENGTH_SHORT).show();
                        return;
                }
            } else {
                // api request unsuccessful
                switch (mAction) {
                    case SEND:
                        Toast.makeText(getContext(), "Unable to send email", Toast.LENGTH_SHORT).show();
                        return;
                    case DRAFT:
                        Toast.makeText(getContext(), "Unable to save draft", Toast.LENGTH_SHORT).show();
                        return;
                }
            }
        }
    }
}
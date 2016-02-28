package com.charlesdrews.charliemail;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;

import org.apache.commons.validator.routines.EmailValidator;

import java.util.Arrays;

/**
 * Allow user to compose a new email
 * Created by charlie on 2/28/16.
 */
public class ComposeFragment extends Fragment {
    private static final String SEND = "send";
    private static final String DRAFT = "draft";

    private GoogleAccountCredential mCredential;
    private TextView mFrom;
    private EditText mTo, mCc, mSubject, mBody;
    private Button mSend, mDraft;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        mFrom = (TextView) rootView.findViewById(R.id.compose_from);
        mTo = (EditText) rootView.findViewById(R.id.compose_to);
        mCc = (EditText) rootView.findViewById(R.id.compose_cc);
        mSubject = (EditText) rootView.findViewById(R.id.compose_subject);
        mBody = (EditText) rootView.findViewById(R.id.compose_body);
        mSend = (Button) rootView.findViewById(R.id.send_button);
        mDraft = (Button) rootView.findViewById(R.id.draft_button);

        mFrom.setText(String.format(getString(R.string.detail_from), mCredential.getSelectedAccountName()));

        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInputs()) {
                    new CreateEmailAsyncTask().execute(SEND);
                }
            }
        });

        mDraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CreateEmailAsyncTask().execute(DRAFT);
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

        @Override
        protected Boolean doInBackground(String... params) {
            if (params.length == 0) {
                return false;
            }

            switch (params[0]) {
                case SEND:
                    break;
                case DRAFT:
                    break;
            }

            return false;
        }
    }

}
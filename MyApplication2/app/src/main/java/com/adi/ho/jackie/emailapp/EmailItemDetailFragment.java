package com.adi.ho.jackie.emailapp;

import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adi.ho.jackie.emailapp.database.MailDatabaseOpenHelper;

import org.w3c.dom.Text;

import java.util.HashMap;

/**
 * A fragment representing a single EmailItem detail screen.
 * This fragment is either contained in a {@link EmailItemListActivity}
 * in two-pane mode (on tablets) or a {@link EmailItemDetailActivity}
 * on handsets.
 */
public class EmailItemDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    private String mRecipient;
    private String mSender;
    private String mDate;
    private String mBody;
    private String mId;
    private String mSubject;

    public TextView mSenderText;
    public TextView mRecipientText;
    public TextView mDateText;
    public TextView mBodyText;
    public TextView mSubjectText;

    public EmailItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            if (getArguments().containsKey("ID") ){
//            // Load the dummy content specified by the fragment
//            // arguments. In a real-world scenario, use a Loader
//            // to load content from a content provider.
            mId = (getArguments().getString("ID"));

        } else {

            }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.emailitem_detail, container, false);

        mSenderText = (TextView) rootView.findViewById(R.id.email_sender_detail);
        mRecipientText = (TextView)rootView.findViewById(R.id.email_recipient_detail);
        mDateText = (TextView)rootView.findViewById(R.id.email_date_detail);
        mBodyText= (TextView)rootView.findViewById(R.id.email_body_detail);
        mSubjectText = (TextView)rootView.findViewById(R.id.email_subject_detail);

        // Show the dummy content as text in a TextView.

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mId != null) {
            new GetEmailFromDBWithIdAsyncTask().execute(mId);
        }
    }

    private class GetEmailFromDBWithIdAsyncTask extends AsyncTask<String, Void, HashMap<String, String>> {
        MailDatabaseOpenHelper helper;
        HashMap<String, String> emailIdContents;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            helper = MailDatabaseOpenHelper.getInstance(getContext());
            emailIdContents = new HashMap<>();
        }

        @Override
        protected HashMap<String, String> doInBackground(String... params) {
            String id = params[0];
            Cursor cursor = helper.getEmailById(id);
            cursor.moveToFirst();
            emailIdContents.put("RECIPIENT", cursor.getString(cursor.getColumnIndex(MailDatabaseOpenHelper.MAIL_RECIPIENT)));
            emailIdContents.put("SENDER", cursor.getString(cursor.getColumnIndex(MailDatabaseOpenHelper.MAIL_SENDER)));
            emailIdContents.put("BODY", cursor.getString(cursor.getColumnIndex(MailDatabaseOpenHelper.MAIL_BODY)));
            emailIdContents.put("DATE", cursor.getString(cursor.getColumnIndex(MailDatabaseOpenHelper.MAIL_DATE)));
            emailIdContents.put("SUBJECT", cursor.getString(cursor.getColumnIndex(MailDatabaseOpenHelper.MAIL_SUBJECT)));
            cursor.close();
            return emailIdContents;
        }

        @Override
        protected void onPostExecute(HashMap hashMap) {
            mBody = hashMap.get("BODY").toString();
            mDate = hashMap.get("DATE").toString();
            mRecipient = hashMap.get("RECIPIENT").toString();
            mSender = hashMap.get("SENDER").toString();
            mSubject = hashMap.get("SUBJECT").toString();


            mSenderText.setText(mSender);
            mRecipientText.setText(mRecipient);
            mDateText.setText("Date Received: " + mDate);
            mSubjectText.setText(mSubject);
            if (mBody.contains("html")) {
                mBodyText.setText(Html.fromHtml(mBody));
            } else {
                mBodyText.setText(mBody);
            }

        }
    }
}

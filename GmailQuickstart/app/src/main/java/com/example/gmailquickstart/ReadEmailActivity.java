package com.example.gmailquickstart;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;

import java.io.IOException;
import java.util.Arrays;

public class ReadEmailActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ActionBar mActionBar;
    private TextView mTo,mFrom,mCC,mDate,mBody;
    private Email mEmail;
    private String mEmailID,mEmailTo,mEmailFrom,mEmailCC,mEmailDate,mEmailBody;
    private com.google.api.services.gmail.Gmail mService;
    private GoogleAccountCredential mCredential;
    SharedPreferences mSettings;
    private Message mMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_email);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTo = (TextView) findViewById(R.id.xmlReadTo);
        mCC = (TextView) findViewById(R.id.xmlReadCC);
        mFrom = (TextView) findViewById(R.id.xmlReadFrom);
        mDate = (TextView) findViewById(R.id.xmlReadDate);
        mBody = (TextView) findViewById(R.id.xmlReadBody);

        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);

        // Initialize credentials and service object
        mSettings = getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);

        //AsyncTask to populate email data on screen
        RetrieveEmailTask retrieveEmailTask = new RetrieveEmailTask();
        retrieveEmailTask.execute(getIntent().getStringExtra(EmailAdapter.EMAIL_ID));

        //FAB set to send response to email sender
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Send intent to "Compose" message screen with from, to, and subject (add "RE:") pre-populated
            }
        });
    }

    //AsyncTask takes the EmailID sent in the intent to populate the email elements on screen
    private class RetrieveEmailTask extends AsyncTask<String,Void,Email>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mActionBar.setTitle(getIntent().getStringExtra(EmailAdapter.EMAIL_SUBJECT));
        }

        @Override
        protected Email doInBackground(String... ID) {
            mEmailID = ID[0];
            int locationInEmailArray = 0;
            for (Email email : EmailList.getInstance().getAllEmails()) {
                if(email.getmEmailID().equals(mEmailID)){
                    mEmail = email;
                    locationInEmailArray = EmailList.getInstance().getAllEmails().indexOf(email);
                }
            }

            if(mEmailBody == null) {
                mCredential = GoogleAccountCredential.usingOAuth2(getApplicationContext(), Arrays.asList(MainActivity.SCOPES)).setBackOff(new ExponentialBackOff()).setSelectedAccountName(mSettings.getString(MainActivity.PREF_ACCOUNT_NAME, null));
                HttpTransport transport = AndroidHttp.newCompatibleTransport();
                JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
                mService = new com.google.api.services.gmail.Gmail.Builder(transport, jsonFactory, mCredential)
                        .setApplicationName("Gmail API Android Quickstart").build();
                try{
                    mMessage = mService.users().messages().get(mSettings.getString(MainActivity.PREF_ACCOUNT_NAME,"me"), mEmail.getmEmailID()).execute();
                } catch (IOException e){
                    e.printStackTrace();
                }

                //Code to pull email body and decode into readable text
                StringBuilder sb = new StringBuilder();
                if(mMessage.getPayload().getMimeType().contains("multipart")){
                    for(MessagePart part : mMessage.getPayload().getParts()){
                        if(part.getMimeType().contains("multipart")){
                            for(MessagePart partII : part.getParts()){
                                if(partII.getMimeType().equals("text/plain")){
                                    sb.append(new String(com.google.api.client.util.Base64.decodeBase64(
                                            partII.getBody().getData())));
                                }
                            }
                        } else if (part.getMimeType().equals("text/plain")){
                            sb.append(new String (com.google.api.client.util.Base64.decodeBase64(
                                    part.getBody().getData())));
                        }
                    }
                } else {
                    sb.append(new String (com.google.api.client.util.Base64.decodeBase64(
                            mMessage.getPayload().getBody().getData())));
                }
                String body = sb.toString();
                mEmail.setmPayloadPartsBodyData(body);

                //test to confirm JSON location indices for desired Email Header properties
                for (int x = 0; x < mMessage.getPayload().getHeaders().size(); x++) {
                    if(mMessage.getPayload().getHeaders().get(x).getName().equals("Date")){
                        mEmail.setmPayloadHeadersDate(mMessage.getPayload().getHeaders().get(x).getValue());}
                    if(mMessage.getPayload().getHeaders().get(x).getName().equals("From")){
                        mEmail.setmPayloadHeadersFrom(mMessage.getPayload().getHeaders().get(x).getValue());}
                    if(mMessage.getPayload().getHeaders().get(x).getName().equals("To")){
                        mEmail.setmPayloadHeadersTo(mMessage.getPayload().getHeaders().get(x).getValue());}
                    if(mMessage.getPayload().getHeaders().get(x).getName().equals("Cc")){
                        mEmail.setmPayloadHeadersCc(mMessage.getPayload().getHeaders().get(x).getValue());}
                    if(mMessage.getPayload().getHeaders().get(x).getName().equals("Bcc")){
                        mEmail.setmPayloadHeadersBcc(mMessage.getPayload().getHeaders().get(x).getValue());}
                }
                EmailList.getInstance().replaceEmail(locationInEmailArray,mEmail);
            }
            return mEmail;
        }

        @Override
        protected void onPostExecute(Email email) {
            mTo.setText(email.getmPayloadHeadersTo());
            mFrom.setText(email.getmPayloadHeadersFrom());
            mCC.setText(email.getmPayloadHeadersCc());
            mDate.setText(email.getmPayloadHeadersDate());
            mBody.setText(email.getmPayloadPartsBodyData());
            super.onPostExecute(email);
        }
    }
}

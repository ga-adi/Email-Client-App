package com.example.gmailquickstart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
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
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;


public class ComposeEmailActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ActionBar mActionBar;
    private TextView mDate;
    private EditText mTo,mCC,mBcc,mSubject,mBody;
    private com.google.api.services.gmail.Gmail mService;
    private GoogleAccountCredential mCredential;
    private FloatingActionButton mFab;
    SharedPreferences mSettings;
    private Message mMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_email);

        mDate = (TextView) findViewById(R.id.xmlComposeDate);
        mCC = (EditText) findViewById(R.id.xmlComposeCC);
        mBcc = (EditText) findViewById(R.id.xmlComposeBCC);
        mTo = (EditText) findViewById(R.id.xmlComposeTo);
        mSubject = (EditText) findViewById(R.id.xmlComposeSubject);
        mBody = (EditText) findViewById(R.id.xmlComposeBody);
        mFab = (FloatingActionButton) findViewById(R.id.fab);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setTitle("Compose Email");

        mSettings = getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);

        //If message is a reply, prepopulate To/Cc/Subject/Body sections
        setUpReplyContent();

        //Floating Action Button takes the information entered by the User and sends an email
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendEmailTask sendEmailTask = new SendEmailTask();
                sendEmailTask.execute();
            }
        });
    }

    //AsyncTask confirms all fields populated and sends email
    private class SendEmailTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US);
            Date date = new Date();
            mDate.setText(simpleDateFormat.format(date));
        }

        @Override
        protected Void doInBackground(Void... ID) {
            mCredential = GoogleAccountCredential.usingOAuth2(getApplicationContext(), Arrays.asList(MainActivity.SCOPES)).setBackOff(new ExponentialBackOff()).setSelectedAccountName(mSettings.getString(MainActivity.PREF_ACCOUNT_NAME, null));
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.gmail.Gmail.Builder(transport, jsonFactory, mCredential).setApplicationName("Gmail API Android Quickstart").build();

            mMessage = new Message();
            mMessage.setLabelIds();
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
                                    sb.append(new String(Base64.decodeBase64(partII.getBody().getData())));
                                }
                            }
                        } else if (part.getMimeType().equals("text/plain")){
                            sb.append(new String (Base64.decodeBase64(part.getBody().getData())));
                        }
                    }
                } else {
                    sb.append(new String (Base64.decodeBase64(mMessage.getPayload().getBody().getData())));
                }
                String body = sb.toString();
                mEmail.setmPayloadPartsBodyData(body);

            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            Toast.makeText(ComposeEmailActivity.this, "Email Sent!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ComposeEmailActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    private void setUpReplyContent(){
        if(!getIntent().getStringExtra(EmailAdapter.EMAIL_SUBJECT).isEmpty()){
            String subject = "Re: " + getIntent().getStringExtra(EmailAdapter.EMAIL_SUBJECT);
            mSubject.setText(subject);}
        if(!getIntent().getStringExtra(EmailAdapter.EMAIL_CC).isEmpty()){
            mCC.setText(getIntent().getStringExtra(EmailAdapter.EMAIL_CC));}
        if(!getIntent().getStringExtra(EmailAdapter.EMAIL_TO).isEmpty()){
            mTo.setText(getIntent().getStringExtra(EmailAdapter.EMAIL_TO));}
        if(!getIntent().getStringExtra(EmailAdapter.EMAIL_BODY).isEmpty()){
            String body = "\n\n------------------\n" + getIntent().getStringExtra(EmailAdapter.EMAIL_BODY);
            mBody.setText(body);}
    }

    public static MimeMessage createEmail(String to, String from, String subject,
                                          String bodyText) throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);
        InternetAddress tAddress = new InternetAddress(to);
        InternetAddress fAddress = new InternetAddress(from);

        email.setFrom(new InternetAddress(from));
        email.addRecipient(javax.mail.Message.RecipientType.TO,
                new InternetAddress(to));
        email.setSubject(subject);
        email.setText(bodyText);
        return email;
    }

}

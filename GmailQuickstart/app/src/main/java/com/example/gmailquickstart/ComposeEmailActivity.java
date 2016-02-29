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
import android.text.InputType;
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
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class ComposeEmailActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ActionBar mActionBar;
    private EditText mTo,mCC,mBcc,mSubject,mBody;
    private com.google.api.services.gmail.Gmail mService;
    private GoogleAccountCredential mCredential;
    private FloatingActionButton mFab;
    SharedPreferences mSettings;
    private MimeMessage mMessage;
    private String mEmailTo,mEmailCC,mEmailBCC,mEmailDate,mEmailSubject,mEmailBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_email);

        mCC = (EditText) findViewById(R.id.xmlComposeCC);
        mBcc = (EditText) findViewById(R.id.xmlComposeBCC);
        mTo = (EditText) findViewById(R.id.xmlComposeTo);
        mSubject = (EditText) findViewById(R.id.xmlComposeSubject);
        mBody = (EditText) findViewById(R.id.xmlComposeBody);
        mFab = (FloatingActionButton) findViewById(R.id.fab);

        //alters keyboard type for email addresses
        mTo.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        mCC.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        mBcc.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
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
            mEmailTo = mTo.getText().toString();
            mEmailCC = mCC.getText().toString();
            mEmailBCC = mBcc.getText().toString();
            mEmailSubject = mSubject.getText().toString();
            mEmailBody = mBody.getText().toString();
        }

        @Override
        protected Void doInBackground(Void... ID) {
            mCredential = GoogleAccountCredential.usingOAuth2(getApplicationContext(), Arrays.asList(MainActivity.SCOPES)).setBackOff(new ExponentialBackOff()).setSelectedAccountName(mSettings.getString(MainActivity.PREF_ACCOUNT_NAME, null));
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.gmail.Gmail.Builder(transport, jsonFactory, mCredential).setApplicationName("Gmail API Android Quickstart").build();

            //Code to create a new Message and send it as an email
            try{
                mMessage = createEmail(mEmailTo,mSettings.getString(MainActivity.PREF_ACCOUNT_NAME,""),mEmailSubject,mEmailBody);
                sendMessage(mService,mSettings.getString(MainActivity.PREF_ACCOUNT_NAME,""),mMessage);}
            catch(MessagingException e){e.printStackTrace();}
            catch(IOException e){e.printStackTrace();}

            return null;
        }

        //upon completion, user is redirected to the MainActivity and given confirmation of successful delivery
        @Override
        protected void onPostExecute(Void param) {
            Toast.makeText(ComposeEmailActivity.this, "Email Sent!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ComposeEmailActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    //Method to pull reply information from intent, if responding to a previous email
    private void setUpReplyContent(){
        if(getIntent().hasExtra(EmailAdapter.EMAIL_SUBJECT)){
            String subject = "Re: " + getIntent().getStringExtra(EmailAdapter.EMAIL_SUBJECT);
            mSubject.setText(subject);}
        if(getIntent().hasExtra(EmailAdapter.EMAIL_CC)){
            mCC.setText(getIntent().getStringExtra(EmailAdapter.EMAIL_CC));}
        if(getIntent().hasExtra(EmailAdapter.EMAIL_TO)){
            mTo.setText(getIntent().getStringExtra(EmailAdapter.EMAIL_TO));}
        if(getIntent().hasExtra(EmailAdapter.EMAIL_BODY)){
            String body = "\n\n------------------\n" + getIntent().getStringExtra(EmailAdapter.EMAIL_BODY);
            mBody.setText(body);}
    }

    //Method to take user inputs and create a MimeMessage Object and populate it with the user's inputs
    public static MimeMessage createEmail(String to, String from, String subject, String bodyText) throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);
        InternetAddress tAddress = new InternetAddress(to);
        InternetAddress fAddress = new InternetAddress(from);

        email.setFrom(fAddress);
        email.addRecipient(javax.mail.Message.RecipientType.TO, tAddress);
        email.setSubject(subject);
        email.setText(bodyText);
        email.setSentDate(new Date());
        return email;
    }

    //Method to encode the MimeMessage object into a Gmail object for delivery via network call
    public static Message createMessageWithEmail(MimeMessage email) throws MessagingException, IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        email.writeTo(bytes);
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes.toByteArray());
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }

    //Method to take the newly constructed Gmail object and send via gmail servers
    public static void sendMessage(Gmail service, String userId, MimeMessage email) throws MessagingException, IOException {
        Message message = createMessageWithEmail(email);
        Message result = service.users().messages().send(userId, message).execute();
    }

}

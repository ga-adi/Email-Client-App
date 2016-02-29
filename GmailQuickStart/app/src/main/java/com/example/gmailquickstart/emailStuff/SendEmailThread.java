package com.example.gmailquickstart.emailStuff;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.example.gmailquickstart.EmailListActivity;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Draft;
import com.google.api.services.gmail.model.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by nat on 2/28/16.
 */
public class SendEmailThread extends AsyncTask<Email, Void, Boolean> {

    private com.google.api.services.gmail.Gmail mService = null;
    private Exception mLastError = null;
    private Context mContext;

    public SendEmailThread(GoogleAccountCredential credential,Context context) {
        mContext=context;
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.gmail.Gmail.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("GEE MAIL")
                .build();
    }
    @Override
    protected Boolean doInBackground(Email... params) {
        if(params[0]==null){
            return false;
        }
        Email emailToBeSent = (Email)params[0];

        Log.d("SendEmailThread", "sending email with following info FROM:" + emailToBeSent.getFromData() + "\r\n SUBJECT" + emailToBeSent.getSnippet() + "\r\n" + "BODY " + emailToBeSent.getBodyData());

        try{
            MimeMessage mimeMessage = createEmail(emailToBeSent.getToData().get(0),emailToBeSent.getFromData(),emailToBeSent.getSubject(),emailToBeSent.getBodyData());
            createMessageWithEmail(mimeMessage);
            if(emailToBeSent.isDraft()){
                createDraft(mService,"me",mimeMessage);
            }else {
                sendMessage(mService, "me", mimeMessage);
            }
        }catch(MessagingException me){
            me.printStackTrace();
            return false;
        }catch(IOException ioe){
            ioe.printStackTrace();
            return false;
        }
        //sendMail(mService, emailToBeSent.getFromData(), emailToBeSent.getToData(), emailToBeSent.getSubject(), emailToBeSent.getBodyData());
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);


        Activity a = (Activity)mContext;
        Intent intentToMessageMain = new Intent();
        intentToMessageMain.putExtra("RESULT_OF_COMPOSE",result);
        a.setResult(EmailListActivity.COMPOSE_EMAIL,intentToMessageMain);
        a.finish();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    public static boolean sendMail(Gmail service, String from, List<String> to, String title, String body) {
        if (service == null) {
            return false;
        }
        Message message = new Message();

        StringBuilder builder = new StringBuilder();
        builder.append("From: ");
        builder.append(from);
        builder.append("\nTo: ");
        if (to != null) {
            int toSize = to.size();
            for (int i = 0; i < toSize; i++) {
                String tmp = to.get(i).replace(",", "");
                builder.append(tmp);
                if (i < toSize - 1) {
                    builder.append(", ");
                }
            }
        }
        builder.append("\nDate: ");
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
        String date = sdf.format(new Date());
        builder.append(date);
        builder.append("\nMIME-Version: 1.0\nSubject: =?ISO-2022-JP?B?");
        if (title != null) {
            try {
                builder.append(Base64.encodeBase64URLSafeString(title.getBytes("ISO-2022-JP")));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        builder.append("?=");
        builder.append("\nContent-Type: text/plain; charset=ISO-2022-JP;");
        builder.append("\nContent-Transfer-Encoding: 7bit");
        builder.append("\n\n");
        builder.append(body);

        String encodedEmail = Base64.encodeBase64URLSafeString(builder.toString().getBytes());

        message.setRaw(encodedEmail);

        boolean result = false;

        try {
            message = service.users().messages().send("me", message).execute();
            result = true;
        } catch (UserRecoverableAuthIOException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
    /**
     * Create a MimeMessage using the parameters provided.
     *
     * @param to Email address of the receiver.
     * @param from Email address of the sender, the mailbox account.
     * @param subject Subject of the email.
     * @param bodyText Body text of the email.
     * @return MimeMessage to be used to send email.
     * @throws MessagingException
     */
    public  MimeMessage createEmail(String to, String from, String subject,
                                          String bodyText) throws MessagingException {
        Log.d("SendEmailThread", "inside createEmail");
        Log.d("SendEmailThread","to "+to);
        Log.d("SendEmailThread", "from " + from);
        Log.d("SendEmailThread", "bodyText " + bodyText);
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
    /**
     * Create a Message from an email
     *
     * @param email Email to be set to raw of message
     * @return Message containing base64url encoded email.
     * @throws IOException
     * @throws MessagingException
     */
    public  Message createMessageWithEmail(MimeMessage email)
            throws MessagingException, IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        email.writeTo(bytes);
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes.toByteArray());
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }
    /**
     * Send an email from the user's mailbox to its recipient.
     *
     * @param service Authorized Gmail API instance.
     * @param userId User's email address. The special value "me"
     * can be used to indicate the authenticated user.
     * @param email Email to be sent.
     * @throws MessagingException
     * @throws IOException
     */
    public  void sendMessage(Gmail service, String userId, MimeMessage email)
            throws MessagingException, IOException {
        Message message = createMessageWithEmail(email);
        message = service.users().messages().send(userId, message).execute();

        System.out.println("Message id: " + message.getId());
        System.out.println(message.toPrettyString());
    }

    public Draft createDraft(Gmail service, String userId, MimeMessage email)
            throws MessagingException, IOException {
        Message message = createMessageWithEmail(email);
        Draft draft = new Draft();
        draft.setMessage(message);
        draft = service.users().drafts().create(userId, draft).execute();

        System.out.println("draft id: " + draft.getId());
        System.out.println(draft.toPrettyString());
        return draft;
    }

}

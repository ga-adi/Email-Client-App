package com.example.gmailquickstart.emailStuff;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        Email emailToBeSent = params[0];

        Log.d("SendEmailThread","sending email with following info FROM:"+emailToBeSent.getFromData()+"\r\n SUBJECT"+emailToBeSent.getSnippet()+"\r\n"+"BODY "+emailToBeSent.getBodyData());
        sendMail(mService, emailToBeSent.getFromData(), emailToBeSent.getToData(), emailToBeSent.getSubject(), emailToBeSent.getBodyData());
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if(result){

        }else{
            Toast.makeText(mContext,"Unable to send the email",Toast.LENGTH_LONG).show();
        }

        Activity a = (Activity)mContext;
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


}

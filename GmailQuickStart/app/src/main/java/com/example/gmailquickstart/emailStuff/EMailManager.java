package com.example.gmailquickstart.emailStuff;

import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import java.util.ArrayList;

/**
 * Created by nat on 2/26/16.
 */
public class EMailManager {

    private static EMailManager mInstance;

    private GoogleAccountCredential mCredential;
    private String mUserEmail = "";

    public String getUserEmail() {
        return mUserEmail;
    }



    public GoogleAccountCredential getCredential() {
        return mCredential;
    }

    public void setCredential(GoogleAccountCredential credential) {
        mCredential = credential;
        mUserEmail=mCredential.getSelectedAccountName();

    }

    private ArrayList<Email>mEmails;
    private long mLastUpdated=0;

    public static EMailManager getInstance() {
        if (mInstance == null) {
            mInstance = new EMailManager();
        }
        return mInstance;
    }


    private EMailManager(){
       mEmails = new ArrayList<>();
    }

    public boolean isTimeToUpdate(){
        long currentTime = System.currentTimeMillis();
        if(currentTime-mLastUpdated<=120000){
            Log.d("EmailManager","Recommended not to update as its been less than two minutes since last update");
            return false;
        }
        Log.d("EmailManager","Recommended to UPDATE as its been more than two minutes since last update");
        return true;
    }
    public boolean startUpdate(){
        if(isTimeToUpdate()==false){
            return false;
        }
        clearEmails();
        return true;
    }


    public void endUpdate(){
        mLastUpdated = System.currentTimeMillis();
    }
    private void clearEmails(){
        mEmails.clear();
    }
    public void addEmail(Email email){

        mEmails.add(email);
    }

    public Email getEmailByID(String id){
        for(Email message:mEmails){
            if(message.getTheID().equals(id)){

                return message;
            }
        }
        return null;
    }
    public ArrayList<Email>getAllEmails(){
        return mEmails;
    }

    public void printAllToLog(){
        if(mEmails.size()==0){
            Log.d("PRINT NO EMAILS","no emails here");
        }else{
            Log.d("PRINT ALL EMAILS","THE NUMBER OF mEmails is "+mEmails.size());
        }
        for(Email mail: mEmails){
            Log.d("PRINT ALL MAIL ",mail.getSnippet()+" "+mail.getTheID());
        }
    }
}

package com.example.gmailquickstart;

import java.util.ArrayList;

/**
 * Created by Todo on 2/26/2016.
 */
public class EmailList {
    private static EmailList mInstance;
    private static ArrayList<Email> mEmailArrayList;

    private EmailList(){
        mEmailArrayList = new ArrayList<Email>();
    }

    public static EmailList getInstance(){
        if(mInstance==null){
            mInstance = new EmailList();
        }
        return mInstance;
    }

    public ArrayList<Email> getAllEmails(){
        return mEmailArrayList;
    }

    public ArrayList<Email> getInbox(){
        ArrayList<Email> inbox = new ArrayList<>();
        for (Email email:mEmailArrayList) {
            for(String x:email.getmLabelIDs()){
                if(x.equals("INBOX")){
                    inbox.add(email);
                }
            }
        }
        return inbox;
    }

    public ArrayList<Email> getSentMail(){
        ArrayList<Email> sentMail = new ArrayList<>();
        for (Email email:mEmailArrayList) {
            for(String x:email.getmLabelIDs()){
                if(x.equals("SENT")){
                    sentMail.add(email);
                }
            }
        }
        return sentMail;
    }

    public void addEmail(Email email){
        mEmailArrayList.add(email);
    }

    public void addEmail (int position, Email email){
        mEmailArrayList.add(position,email);
    }

    public Email get (int position){
        return mEmailArrayList.get(position);
    }

    public void clear (){
        mEmailArrayList.clear();
    }
}

package com.example.mom.clothtalk;

import java.util.ArrayList;

/**
 * Created by MOM on 2/28/16.
 */

public class InboxEmailList {

    private static InboxEmailList mInstance;
    private static ArrayList<Email> mEmailArrayList;

    private InboxEmailList(){
        mEmailArrayList = new ArrayList<Email>();
    }

    public static InboxEmailList getInstance(){
        if(mInstance==null){
            mInstance = new InboxEmailList();
        }
        return mInstance;
    }

    public ArrayList<Email> getAllEmails(){
        return mEmailArrayList;
    }

    public ArrayList<Email> getInbox(){
        ArrayList<Email> inbox = new ArrayList<>();
        for (Email email:mEmailArrayList) {
            if(email.getmLabelIDs()!=null){
                for(String x:email.getmLabelIDs()){
                    if(x.equals("INBOX")){
                        inbox.add(email);
                    }
                }
            }
        }
        return inbox;
    }

    public ArrayList<Email> getSentMail(){
        ArrayList<Email> sentMail = new ArrayList<>();
        for (Email email:mEmailArrayList) {
            if(email.getmLabelIDs()!=null){
                for(String x:email.getmLabelIDs()){
                    if(x.equals("SENT")){
                        sentMail.add(email);
                    }
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

    public void replaceEmail (int position, Email email){
        mEmailArrayList.set(position,email);
    }

    public Email get (int position){
        return mEmailArrayList.get(position);
    }

    public void clear (){
        mEmailArrayList.clear();
    }
}
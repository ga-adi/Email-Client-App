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

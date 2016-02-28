package com.example.gmailquickstart.emailStuff;

import java.util.ArrayList;

/**
 * Created by nat on 2/26/16.
 */
public class Email {
    String mSnippet;
    String mTheID;
    String mSubject;
    String mBodyData;
    ArrayList<String> mToData;
    String mFromData;


    public ArrayList<String> getToData() {
        return mToData;
    }

    public void addTo(String toData) {
        mToData.add(toData);
    }

    public String getFromData() {
        return mFromData;
    }

    public void setFromData(String fromData) {
        mFromData = fromData;
    }

    public String getBodyData() {
        return mBodyData;
    }

    public void setBodyData(String bodyData) {
        mBodyData = bodyData;
    }

    public String getSubject() {
        return mSubject;
    }

    public void setSubject(String subject) {
        mSubject = subject;
    }

    public String getSnippet() {
        return mSnippet;
    }

    public void setSnippet(String snippet) {
        mSnippet = snippet;
    }

    public String getTheID() {
        return mTheID;
    }

    public void setTheID(String theID) {
        mTheID = theID;
    }


    public Email(){
        mToData = new ArrayList<>();
    }
}

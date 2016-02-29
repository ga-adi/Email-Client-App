package com.example.gmailquickstart.emailStuff;

import java.util.ArrayList;

/**
 * Created by nat on 2/26/16.
 */
public class Email {
    private String mSnippet;
    private String mTheID;
    private String mSubject;
    private String mBodyData;
    private ArrayList<String> mToData;
    private String mFromData;

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    private String mType;

    public boolean isDraft() {
        return mIsDraft;
    }

    public void setIsDraft(boolean isDraft) {
        mIsDraft = isDraft;
    }

    private boolean mIsDraft;


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

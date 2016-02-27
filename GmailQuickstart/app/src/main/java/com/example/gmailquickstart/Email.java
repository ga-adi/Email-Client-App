package com.example.gmailquickstart;

/**
 * Created by Todo on 2/25/2016.
 */
public class Email {
    private String mEmailID;
    private String[] mLabelIDs = new String[10];
    private String mSnippet;

    private String mPayloadHeadersDate;
    private String mPayloadHeadersSubject;
    private String mPayloadHeadersFrom;
    private String mPayloadHeadersTo;
    private String mPayloadHeadersCc;
    private String mPayloadHeadersBcc;
    private String mPayloadPartsBodyData;

    public Email(){}

    public Email(String mEmailID, String mPayloadHeadersDate, String mPayloadHeadersFrom, String mPayloadHeadersTo) {
        this.mEmailID = mEmailID;
        this.mPayloadHeadersDate = mPayloadHeadersDate;
        this.mPayloadHeadersFrom = mPayloadHeadersFrom;
        this.mPayloadHeadersTo = mPayloadHeadersTo;
    }

    public Email(String mEmailID, String[] mLabelIDs, String mSnippet, String mPayloadHeadersDate, String mPayloadHeadersSubject, String mPayloadHeadersFrom, String mPayloadHeadersTo, String mPayloadHeadersCc, String mPayloadHeadersBcc, String mPayloadPartsBodyData) {
        this.mEmailID = mEmailID;
        this.mLabelIDs = mLabelIDs;
        this.mSnippet = mSnippet;
        this.mPayloadHeadersDate = mPayloadHeadersDate;
        this.mPayloadHeadersSubject = mPayloadHeadersSubject;
        this.mPayloadHeadersFrom = mPayloadHeadersFrom;
        this.mPayloadHeadersTo = mPayloadHeadersTo;
        this.mPayloadHeadersCc = mPayloadHeadersCc;
        this.mPayloadHeadersBcc = mPayloadHeadersBcc;
        this.mPayloadPartsBodyData = mPayloadPartsBodyData;
    }

    public String getmEmailID() {
        return mEmailID;
    }

    public void setmEmailID(String mEmailID) {
        this.mEmailID = mEmailID;
    }

    public String[] getmLabelIDs() {
        return mLabelIDs;
    }

    public void setmLabelIDs(String[] mLabelIDs) {
        this.mLabelIDs = mLabelIDs;
    }

    public String getmSnippet() {
        return mSnippet;
    }

    public void setmSnippet(String mSnippet) {
        this.mSnippet = mSnippet;
    }

    public String getmPayloadHeadersDate() {
        return mPayloadHeadersDate;
    }

    public void setmPayloadHeadersDate(String mPayloadHeadersDate) {
        this.mPayloadHeadersDate = mPayloadHeadersDate;
    }

    public String getmPayloadHeadersSubject() {
        return mPayloadHeadersSubject;
    }

    public void setmPayloadHeadersSubject(String mPayloadHeadersSubject) {
        this.mPayloadHeadersSubject = mPayloadHeadersSubject;
    }

    public String getmPayloadHeadersFrom() {
        return mPayloadHeadersFrom;
    }

    public void setmPayloadHeadersFrom(String mPayloadHeadersFrom) {
        this.mPayloadHeadersFrom = mPayloadHeadersFrom;
    }

    public String getmPayloadHeadersTo() {
        return mPayloadHeadersTo;
    }

    public void setmPayloadHeadersTo(String mPayloadHeadersTo) {
        this.mPayloadHeadersTo = mPayloadHeadersTo;
    }

    public String getmPayloadHeadersCc() {
        return mPayloadHeadersCc;
    }

    public void setmPayloadHeadersCc(String mPayloadHeadersCc) {
        this.mPayloadHeadersCc = mPayloadHeadersCc;
    }

    public String getmPayloadHeadersBcc() {
        return mPayloadHeadersBcc;
    }

    public void setmPayloadHeadersBcc(String mPayloadHeadersBcc) {
        this.mPayloadHeadersBcc = mPayloadHeadersBcc;
    }

    public String getmPayloadPartsBodyData() {
        return mPayloadPartsBodyData;
    }

    public void setmPayloadPartsBodyData(String mPayloadPartsBodyData) {
        this.mPayloadPartsBodyData = mPayloadPartsBodyData;
    }
}

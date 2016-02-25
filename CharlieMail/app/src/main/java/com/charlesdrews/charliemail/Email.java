package com.charlesdrews.charliemail;

/**
 * Represents an email w/ subject, sender, recipient, time sent, etc.
 * Created by charlie on 2/25/16.
 */
public class Email {
    private String mSubject;

    public Email(String subject) {
        mSubject = subject;
    }

    public String getSubject() { return mSubject; }
}

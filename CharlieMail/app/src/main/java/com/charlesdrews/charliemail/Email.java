package com.charlesdrews.charliemail;

import android.util.Log;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * Represents an email w/ subject, sender, recipient, time sent, etc.
 * Created by charlie on 2/25/16.
 */
public class Email {
    private String mFrom;
    private String mTo;
    private String mCc;
    private Date mSentDate;
    private String mSubject;
    private String mBody;

    public Email(MimeMessage email) {
        try {
            mFrom = getStringFromAddressArray(email.getFrom());
            mTo = getStringFromAddressArray(email.getRecipients(Message.RecipientType.TO));
            mCc = getStringFromAddressArray(email.getRecipients(Message.RecipientType.CC));
            mSentDate = email.getSentDate();
            mSubject = email.getSubject();
            mBody = email.getContent().toString();

        } catch (MessagingException e) {
            Log.e("Email() MessagingExc", e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Email() IOExc", e.getMessage());
            e.printStackTrace();
        }
    }

    private String getStringFromAddressArray(Address[] addresses) {
        if (addresses != null) {
            StringBuilder builder = new StringBuilder();
            for (Address address : addresses) {
                builder.append(address.toString());
                builder.append("; ");
            }
            return builder.toString();
        } else {
            return "";
        }
    }

    public String getFrom() {
        return mFrom;
    }

    public void setFrom(String from) {
        mFrom = from;
    }

    public String getTo() {
        return mTo;
    }

    public void setTo(String to) {
        mTo = to;
    }

    public String getCc() {
        return mCc;
    }

    public void setCc(String cc) {
        mCc = cc;
    }

    public Date getSentDate() {
        return mSentDate;
    }

    public String getSentDateString() {
        if (mSentDate == null) {
            return "";
        } else {
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());
            return dateFormat.format(mSentDate);
        }
    }

    public void setSentDate(Date sentDate) {
        mSentDate = sentDate;
    }

    public String getSubject() {
        return mSubject;
    }

    public void setSubject(String subject) {
        mSubject = subject;
    }

    public String getBody() {
        return mBody;
    }

    public void setBody(String body) {
        mBody = body;
    }
}

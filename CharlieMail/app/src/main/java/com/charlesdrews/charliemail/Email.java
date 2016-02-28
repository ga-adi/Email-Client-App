package com.charlesdrews.charliemail;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeMessage;

/**
 * Represents an email w/ subject, sender, recipient, time sent, etc.
 * Created by charlie on 2/25/16.
 */
public class Email implements Parcelable {
    private String mId, mFrom, mTo, mCc, mSentDate, mSubject, mBody;

    public Email(Parcel in) {
        mId = in.readString();
        mFrom = in.readString();
        mTo = in.readString();
        mCc = in.readString();
        mSentDate = in.readString();
        mSubject = in.readString();
        mBody = in.readString();
    }

    public Email(String id, MimeMessage email) {
        try {
            mId = id;

            mFrom = getStringFromAddressArray(email.getFrom());
            mTo = getStringFromAddressArray(email.getRecipients(Message.RecipientType.TO));
            mCc = getStringFromAddressArray(email.getRecipients(Message.RecipientType.CC));

            mSubject = email.getSubject();

            mBody = parseContent(email.getContent());

            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());
            mSentDate = dateFormat.format(email.getSentDate());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String parseContent(Object content) throws IOException, MessagingException {
        if (content instanceof String) {
            return (String) content;
        } else if (content instanceof Multipart) {
            return parseMultiPart((Multipart) content);
        } else if (content instanceof InputStream) {
            return parseInputStream((InputStream) content);
        } else {
            return "";
        }
    }

    private String parseMultiPart(Multipart content) throws IOException, MessagingException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < content.getCount(); i++) {
            BodyPart part = content.getBodyPart(i);
            sb.append(parseContent(part.getContent()));
            sb.append("\n");
        }
        return sb.toString();
    }

    private String parseInputStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
            sb.append("\n");
        }
        br.close();
        return sb.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mFrom);
        dest.writeString(mTo);
        dest.writeString(mCc);
        dest.writeString(mSentDate);
        dest.writeString(mSubject);
        dest.writeString(mBody);
    }

    public static final Parcelable.Creator<Email> CREATOR
            = new Parcelable.Creator<Email>() {
        public Email createFromParcel(Parcel in) {
            return new Email(in);
        }

        public Email[] newArray(int size) {
            return new Email[size];
        }
    };

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

    public String getId() {
        return mId;
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

    public String getSentDate() {
        return mSentDate;
    }

    public void setSentDate(Date sentDate) {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());
        mSentDate = dateFormat.format(sentDate);
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

package com.boloutaredoubeni.emailapp.models;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.boloutaredoubeni.emailapp.BR;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartBody;
import com.google.api.services.gmail.model.MessagePartHeader;

import java.io.Serializable;
import java.util.List;

/**
 * Copyright 2016 Boloutare Doubeni
 *
 * A Thread of emails represented by a DoublyLinkedList
 */
public class Email extends BaseObservable implements Serializable {

  private static final long serialVersionUID = -6099312954099962806L;

  final private String mId;
  private String mSnippet;
  private String mFrom;
  private String mDate;
  private Email mNextEmailInThread;
  private String mSubject;
  private String mBody;
  private Email mPreviousEmailInThread;
  private boolean mValid;

  public Email(String id, String from, String date, Email previousEmail,
               Email nextEmail, String subject, String body, String snippet) {
    mId = id;
    mFrom = from;
    mDate = date;
    mPreviousEmailInThread = previousEmail;
    mNextEmailInThread = nextEmail;
    mSubject = subject;
    mBody = body;
    mSnippet = snippet;
  }

  /**
   * Parse the email from GMail as an Email Object
   *
   * @param message A message object returned from the API call
   * @return My representation of an Email message for the app
   */
  public static Email createFrom(Message message) {
    String id = message.getId();
    String snippet = message.getSnippet();
    MessagePart messagePart = message.getPayload();
    MessagePartBody messagePartBody = messagePart.getBody();
    String body = messagePartBody.getData();
    List<MessagePartHeader> messagePartHeaders = messagePart.getHeaders();
    String from = "", subject = "", date = "";
    for (MessagePartHeader header : messagePartHeaders) {
      String name = header.getName().toLowerCase();
      switch (name) {
      case "from":
        from = header.getValue();
        break;
      case "subject":
        subject = header.getValue();
        break;
      case "date":
        date = header.getValue();
        break;
      default:
        continue;
      }
    }

    return new Email(id, from, date, null, null, subject, body, snippet);
  }

  public static Message createMessageFrom(Email email) {
    Message message = new Message();
    byte[] buffer = email.mBody.getBytes();
    String body = Base64.encodeBase64URLSafeString(buffer);
    message.setRaw(body);
    return message;
  }

  public static Email defaultEmail() {
    return new Email("", "", "", null, null, "", "", null);
  }

  public String getID() { return mId; }

  @Bindable
  public String getFrom() {
    return mFrom;
  }

  public void setFrom(String to) {
    mFrom = to;
    notifyPropertyChanged(BR.from);
  }

  @Bindable
  public String getDate() {
    return mDate;
  }

  public Email getNextEmail() { return mNextEmailInThread; }

  public Email getPrevEmail() { return mPreviousEmailInThread; }

  @Bindable
  public String getSubject() {
    return mSubject;
  }

  public void setSubject(String subject) {
    mSubject = subject;
    notifyPropertyChanged(BR.subject);
  }

  @Bindable
  public String getBody() {
    return mBody;
  }

  public void setBody(String body) {
    mBody = body;
    notifyPropertyChanged(BR.body);
  }

  @Bindable
  public String getSnippet() {
    return mSnippet;
  }

  public boolean isValid() {
    mValid = mFrom == null || !mFrom.isEmpty() ;
    return mValid;
  }
}

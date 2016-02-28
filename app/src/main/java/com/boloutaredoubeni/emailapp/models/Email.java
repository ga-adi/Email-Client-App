package com.boloutaredoubeni.emailapp.models;

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
public class Email implements Serializable {

  private static final long serialVersionUID = -6099312954099962806L;

  final private String mId;
  private String mSnippet;
  private String mFrom;
  private String mDate;
  private Email mNextEmailInThread;
  private String mSubject;
  private String mBody;
  private Email mPreviousEmailInThread;

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

  public String getID() { return mId; }

  public String getFrom() { return mFrom; }

  public String getDate() { return mDate; }

  public Email getNextEmail() { return mNextEmailInThread; }

  public Email getPrevEmail() { return mPreviousEmailInThread; }

  public String getSubject() { return mSubject; }

  public String getBody() { return mBody; }

  public String getSnippet() { return mSnippet; }
}

package com.adi.ho.jackie.emailapp;

import java.util.HashMap;

/**
 * Created by JHADI on 2/25/16.
 */
public class Email {

    private String id;
    private String snippet;
    private String sender;
    private String date;
    private String body;
    private String recipient;
    private String favorite;
    private String subject;

    public Email (HashMap<String,String> emailHeader){
        id = emailHeader.get("ID");
        snippet = emailHeader.get("SNIPPET");
        sender = emailHeader.get("SENDER");
        date = emailHeader.get("DATE");
        body = emailHeader.get("BODY");
        recipient = emailHeader.get("RECIPIENT");
        favorite = emailHeader.get("FAVORITE");
        subject = emailHeader.get("SUBJECT");
    }

    public String getId() {
        return id;
    }

    public String getSnippet() {
        return snippet;
    }

    public String getSender() {
        return sender;
    }

    public String getDate() {
        return date;
    }

    public String getBody(){ return body;}

    public String getRecipient(){ return recipient;}

    public String getFavorite(){return favorite;}

    public String getSubject(){return subject;}

}

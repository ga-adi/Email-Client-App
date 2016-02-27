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

    public Email (HashMap<String,String> emailHeader){
        id = emailHeader.get("ID");
        snippet = emailHeader.get("SNIPPET");
        sender = emailHeader.get("SENDER");
        date = emailHeader.get("DATE");
        body = emailHeader.get("BODY");
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
}

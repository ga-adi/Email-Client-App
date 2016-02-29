package com.adi.ho.jackie.emailapp;

import java.util.HashMap;

/**
 * Created by JHADI on 2/28/16.
 */
public class EmailDraft {

    private String recipient;
    private String subject;
    private String body;
    private String id;

    public EmailDraft(HashMap<String,String> hashMap){
        recipient = hashMap.get("RECIPIENT");
        subject = hashMap.get("SUBJECT");
        body = hashMap.get("BODY");
        id = hashMap.get("ID");

    }

    public String getRecipient() {
        return recipient;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public String getId() {
        return id;
    }
}

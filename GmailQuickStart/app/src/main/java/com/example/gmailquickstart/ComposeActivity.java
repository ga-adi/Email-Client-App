package com.example.gmailquickstart;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.gmailquickstart.emailStuff.EMailManager;
import com.example.gmailquickstart.emailStuff.Email;
import com.example.gmailquickstart.emailStuff.SendEmailThread;

import java.util.ArrayList;

public class ComposeActivity extends AppCompatActivity {

    boolean mIsDraftOriginally=false;
    String mUser="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        String theID = getIntent().getStringExtra("DRAFT");
        if(theID!=null){
            //this is a draft
            mIsDraftOriginally=true;
            //set the stuff up from the draft
            EMailManager emailManager = EMailManager.getInstance();
            Email mail =emailManager.getEmailByID(theID);
            EditText toEditText = (EditText)findViewById(R.id.to_edit_text);
            ArrayList<String>recipients =  mail.getToData();
            String recipientsField="";
            int total = recipients.size();
            for(int i=0;i<recipients.size();i++){
                if(i!=total-1) {
                    recipientsField += recipients.get(i)+ ",";
                }else{
                    recipientsField += recipients.get(i);
                }
            }
            toEditText.setText(recipientsField);

            //subject line
            EditText subjectEditText = (EditText)findViewById(R.id.subject_edit_text);
            String subjectField = mail.getSubject();
            if(subjectField!=null){
                subjectEditText.setText(subjectField);
            }

            //body line
            EditText bodyEditText = (EditText)findViewById(R.id.theMessageBody);
            String bodyField = mail.getBodyData();
            if(bodyField!=null){
                bodyEditText.setText(bodyField);
            }

        }
        EMailManager emailManager = EMailManager.getInstance();
        mUser=emailManager.getUserEmail();

        EditText fromEditText = (EditText)findViewById(R.id.from_edit_text);
        fromEditText.setText(mUser);

        Button sendEmailButton  = (Button)findViewById(R.id.sendTheMessageButton);
        sendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Email emailToSend = createEmail();
                //Toast.makeText(ComposeActivity.this,"send email called", Toast.LENGTH_LONG).show();
                //Log.d("SendEmailThread", "sending email with following info FROM:");// + emailToSend.getFromData() + " SUBJECT" + emailToSend.getSnippet() + "\r\n" + "BODY " + emailToSend.getBodyData());
                emailToSend.setIsDraft(false);
                SendEmailThread sendEmailThread = new SendEmailThread(EMailManager.getInstance().getCredential(),ComposeActivity.this);
                sendEmailThread.execute(emailToSend);

            }
        });

    }

    private Email createEmail(){
        EditText toEditText = (EditText)findViewById(R.id.to_edit_text);
        String theToField=toEditText.getText().toString();
        String[] recipients = theToField.split(",");

        EditText fromEditText = (EditText)findViewById(R.id.from_edit_text);
        String theFromField=fromEditText.getText().toString();

        //Toast.makeText(ComposeActivity.this,"theFromField "+theFromField,Toast.LENGTH_LONG).show();
        EditText subjectEditText = (EditText)findViewById(R.id.subject_edit_text);
        String subjectField=subjectEditText.getText().toString();

        EditText theBodyEditText = (EditText)findViewById(R.id.theMessageBody);
        String bodyField=theBodyEditText.getText().toString();

        Email emailToSend = new Email();
        for(int i=0;i<recipients.length;i++){
            emailToSend.addTo(recipients[i]);
        }
        emailToSend.setFromData(theFromField);
        emailToSend.setSubject(subjectField);
        emailToSend.setBodyData(bodyField);

        return emailToSend;

    }
    @Override
    public void onBackPressed() {

        Email draftEmail = createEmail();
        String theBody=draftEmail.getBodyData();
        if(theBody==null||theBody.length()==0||theBody.equals(" ")){
            setResult(EmailListActivity.COMPOSE_EMAIL);
            finish();
            return;
        }
        draftEmail.setIsDraft(true);
        SendEmailThread sendEmailThread = new SendEmailThread(EMailManager.getInstance().getCredential(),ComposeActivity.this);
        sendEmailThread.execute(draftEmail);
        super.onBackPressed();

    }
}

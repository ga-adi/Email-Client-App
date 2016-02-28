package com.example.gmailquickstart;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.gmailquickstart.emailStuff.EMailManager;
import com.example.gmailquickstart.emailStuff.Email;
import com.example.gmailquickstart.emailStuff.SendEmailThread;

public class ComposeActivity extends AppCompatActivity {

    String mUser="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

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

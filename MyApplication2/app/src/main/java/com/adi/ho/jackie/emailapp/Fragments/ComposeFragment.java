package com.adi.ho.jackie.emailapp.Fragments;


import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.adi.ho.jackie.emailapp.R;

import java.util.HashMap;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 * A simple {@link Fragment} subclass.
 */
public class ComposeFragment extends DialogFragment {

    EditText senderEditText;
    EditText subjectEditText;
    EditText bodyEditText;
    SendEmailTaskListener sendEmailTaskListener;

    public interface SendEmailTaskListener {
        void sendEmail(HashMap<String,String> hashMap);
    }

    public ComposeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            sendEmailTaskListener = (SendEmailTaskListener)activity;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        senderEditText = (EditText)view.findViewById(R.id.compose_sendto_edit);
         subjectEditText = (EditText)view.findViewById(R.id.composesubject_edit);
         bodyEditText = (EditText)view.findViewById(R.id.composeemail_body_edit);

        getDialog().setCanceledOnTouchOutside(true);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button sendEmailButton = (Button)view.findViewById(R.id.composeemail_send_button);
        sendEmailButton.setOnClickListener(sendEmailListener);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }
    View.OnClickListener sendEmailListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String sendingEmailTo = senderEditText.getText().toString();
            String sendingSubject = subjectEditText.getText().toString();
            String emailBody = bodyEditText.getText().toString();


            if (!isValidEmailAddress(sendingEmailTo)){
                senderEditText.setError("Valid email required");
                return;
            }
            if (sendingSubject.trim().isEmpty()){
                subjectEditText.setError("Subject required");
                return;
            }


            HashMap<String,String> emailContents = new HashMap<>();
            emailContents.put("SUBJECT", sendingSubject);
            emailContents.put("RECIPIENT", sendingEmailTo);
            emailContents.put("BODY", emailBody);

            sendEmailTaskListener.sendEmail(emailContents);
            getDialog().dismiss();
        }
    };

    public static boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }
}

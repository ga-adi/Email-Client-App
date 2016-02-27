package com.adi.ho.jackie.emailapp.recyclerlistitems;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.adi.ho.jackie.emailapp.EmailItemDetailActivity;
import com.adi.ho.jackie.emailapp.R;

/**
 * Created by JHADI on 2/25/16.
 */
public class EmailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private EmailSelectedListener mEmailListener;

    public interface EmailSelectedListener {
       public void onEmailSelected(String emailId);
    }

    TextView emailLabel;
    ImageView emailPic;
    TextView emailDate;
    ImageView emailStar;
    TextView emailSnippet;
    TextView emailBody;
    private Context context;

    public EmailViewHolder(View itemView) {
        super(itemView);
        context = itemView.getContext();
        itemView.setOnClickListener(this);

        emailLabel = (TextView) itemView.findViewById(R.id.email_label);
        emailDate = (TextView)itemView.findViewById(R.id.email_date);
        emailSnippet = (TextView)itemView.findViewById(R.id.email_snippet);
        emailBody = (TextView)itemView.findViewById(R.id.invisible_body);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context, EmailItemDetailActivity.class);
        String body = emailBody.getText().toString();
        intent.putExtra("EMAILBODY",body);
        context.startActivity(intent);
    }
}

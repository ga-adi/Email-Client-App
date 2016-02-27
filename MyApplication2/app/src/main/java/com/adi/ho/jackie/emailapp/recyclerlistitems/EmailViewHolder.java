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
    TextView emailId;
    private Context context;

    public EmailViewHolder(View itemView) {
        super(itemView);
        context = itemView.getContext();
        itemView.setOnClickListener(this);

        emailLabel = (TextView) itemView.findViewById(R.id.email_label);
        emailDate = (TextView)itemView.findViewById(R.id.email_date);
        emailSnippet = (TextView)itemView.findViewById(R.id.email_snippet);
        emailId = (TextView)itemView.findViewById(R.id.invisible_id);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context, EmailItemDetailActivity.class);
        String id = emailId.getText().toString();
        intent.putExtra("EMAILID",id);
        context.startActivity(intent);
    }
}

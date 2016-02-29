package com.adi.ho.jackie.emailapp.recyclerlistitems;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adi.ho.jackie.emailapp.EmailItemDetailActivity;
import com.adi.ho.jackie.emailapp.EmailItemDetailFragment;
import com.adi.ho.jackie.emailapp.R;

/**
 * Created by JHADI on 2/25/16.
 */
public class EmailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private MakeSecondFragmentListener mFragmentListener;

    public interface MakeSecondFragmentListener {
       public void makeSecondFragment(String emailId);
    }

    TextView emailLabel;
    ImageView emailPic;
    TextView emailDate;
    ImageView emailStar;
    TextView emailSnippet;
    TextView emailId;
    TextView emailSubject;
    RelativeLayout emailLayout;
    private Context context;

    public EmailViewHolder(View itemView) {
        super(itemView);
        context = itemView.getContext();
       // this.fragmentManager = fragmentManager;
        itemView.setOnClickListener(this);

        emailLabel = (TextView) itemView.findViewById(R.id.email_label);
        emailDate = (TextView)itemView.findViewById(R.id.email_date);
        emailSnippet = (TextView)itemView.findViewById(R.id.email_snippet);
        emailId = (TextView)itemView.findViewById(R.id.invisible_id);
        emailSubject = (TextView)itemView.findViewById(R.id.email_subject);
        emailLayout = (RelativeLayout)itemView.findViewById(R.id.email_layout_container);
    }

    @Override
    public void onClick(View v) {
        Activity activity = (Activity)context;
        String id = emailId.getText().toString();
        if (activity.findViewById(R.id.emailitem_detail_container) != null){

            try {
                mFragmentListener = (MakeSecondFragmentListener)activity;
                mFragmentListener.makeSecondFragment(id);
            } catch (ClassCastException e){
                e.printStackTrace();
            }


        } else {
            Intent intent = new Intent(context, EmailItemDetailActivity.class);
            intent.putExtra("EMAILID", id);
            context.startActivity(intent);
        }
    }
}

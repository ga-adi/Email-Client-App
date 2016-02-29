package com.charlesdrews.charliemail;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Bind email data to the RecyclerView
 * Created by charlie on 2/25/16.
 */
public class EmailRecyclerAdapter extends RecyclerView.Adapter<EmailRecyclerAdapter.ViewHolder> {
    private String mLabel;
    private ArrayList<Email> mEmails;

    public EmailRecyclerAdapter(String label, ArrayList<Email> emails) {
        mLabel = label;
        mEmails = emails;
    }

    //TODO - figure out how to get more emails when user scrolls to bottom of list

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View holder = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.email_list_item, parent, false);

        return new ViewHolder(holder);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Email email = mEmails.get(position);

        //holder.mId.setText(email.getId());
        holder.mSubject.setText(email.getSubject());

        try {
            holder.mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) v.getContext()).onEmailSelected(mLabel, email);
                }
            });
        } catch (ClassCastException e) {
            Log.e("EmailRecyclerAdapter", "Error casting v.getContext() to MainActivity");
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mEmails.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mLayout;
        public TextView mSubject;//, mId;

        public ViewHolder(View rootView) {
            super(rootView);
            mLayout = rootView.findViewById(R.id.email_list_layout);
            mSubject = (TextView) rootView.findViewById(R.id.email_list_subject);
            //mId = (TextView) rootView.findViewById(R.id.email_list_id_hidden);
        }
    }
}

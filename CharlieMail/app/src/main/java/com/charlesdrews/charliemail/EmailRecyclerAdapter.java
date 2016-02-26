package com.charlesdrews.charliemail;

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
    private ArrayList<Email> mEmails;

    public EmailRecyclerAdapter(ArrayList<Email> emails) {
        mEmails = emails;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View holder = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.email_list_item, parent, false);
        View subject = holder.findViewById(R.id.email_subject);

        try {
            //TODO - set on click listeners for other components of email list item
            subject.setOnClickListener( (View.OnClickListener) parent.getContext() );
        } catch (ClassCastException e) {
            Log.e("EmailRecyclerAdapter", "Error casting parent.getContext() to View.OnClickListener");
            e.printStackTrace();
        }

        return new ViewHolder(holder);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mSubject.setText(mEmails.get(position).getSubject());
    }

    @Override
    public int getItemCount() {
        return mEmails.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mSubject;

        public ViewHolder(View rootView) {
            super(rootView);
            mSubject = (TextView) rootView.findViewById(R.id.email_subject);
        }
    }
}

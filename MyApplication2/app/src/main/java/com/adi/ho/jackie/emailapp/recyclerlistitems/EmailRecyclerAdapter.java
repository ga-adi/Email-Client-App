package com.adi.ho.jackie.emailapp.recyclerlistitems;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adi.ho.jackie.emailapp.Email;
import com.adi.ho.jackie.emailapp.EmailItemDetailActivity;
import com.adi.ho.jackie.emailapp.EmailItemDetailFragment;
import com.adi.ho.jackie.emailapp.R;

import java.util.List;

/**
 * Created by JHADI on 2/25/16.
 */
public class EmailRecyclerAdapter extends RecyclerView.Adapter<EmailViewHolder> {

    private Context context;
    private List<Email> emailList;
    private FragmentManager fragmentManager;

    public EmailRecyclerAdapter(Context context, List<Email> emailList) {
        this.context = context;
        this.emailList = emailList;
    }

    @Override
    public EmailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.email_layout, null);
        EmailViewHolder evh = new EmailViewHolder(view);
        return evh;
    }

    @Override
    public void onBindViewHolder(EmailViewHolder holder, final int position) {
        holder.emailDate.setText(emailList.get(position).getDate());
        holder.emailLabel.setText(emailList.get(position).getSender());
        holder.emailSnippet.setText(emailList.get(position).getSnippet());
        holder.emailId.setText(emailList.get(position).getId());
        holder.emailSubject.setText(emailList.get(position).getSubject());

    }

    @Override
    public int getItemCount() {
        return emailList.size();
    }

    //Methods for animating removing and adding items
    public Email removeItem(int position) {
        final Email email = emailList.remove(position);
        notifyItemRemoved(position);
        return email;
    }

    public void addItem(int position, Email email) {
        emailList.add(position, email);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final Email email = emailList.remove(fromPosition);
        emailList.add(toPosition, email);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void animateTo(List<Email> emails) {
        applyAndAnimateRemovals(emails);
        applyAndAnimateAdditions(emails);
        applyAndAnimateMovedItems(emails);
    }

    private void applyAndAnimateRemovals(List<Email> emails) {
        for (int i = emailList.size() - 1; i >= 0; i--) {
            final Email email = emailList.get(i);
            if (!emails.contains(email)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<Email> emails) {
        for (int i = 0, count = emails.size(); i < count; i++) {
            final Email email = emails.get(i);
            if (!emailList.contains(email)) {
                addItem(i, email);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<Email> emails) {
        for (int toPosition = emails.size() - 1; toPosition >= 0; toPosition--) {
            final Email email = emails.get(toPosition);
            final int fromPosition = emailList.indexOf(email);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }


}

package com.adi.ho.jackie.emailapp.recyclerlistitems;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adi.ho.jackie.emailapp.Email;
import com.adi.ho.jackie.emailapp.R;

import java.util.List;

/**
 * Created by JHADI on 2/25/16.
 */
public class EmailRecyclerAdapter extends RecyclerView.Adapter<EmailViewHolder> {

    private Context context;
    private List<Email> emailList;

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
    public void onBindViewHolder(EmailViewHolder holder, int position) {
        holder.emailDate.setText(emailList.get(position).getDate());
        holder.emailLabel.setText(emailList.get(position).getSender());
        holder.emailSnippet.setText(emailList.get(position).getSnippet());
        holder.emailId.setText(emailList.get(position).getId());
    }

    @Override
    public int getItemCount() {
        return emailList.size();
    }
}

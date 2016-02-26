package com.boloutaredoubeni.emailapp.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.boloutaredoubeni.emailapp.R;
import com.google.api.services.gmail.model.Message;

import java.util.ArrayList;

/**
 * Copyright 2016 Boloutare Doubeni
 */
public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.ViewHolder> {

  private ArrayList<Message> mMessages;

  public InboxAdapter(ArrayList<Message> messageList) {
    mMessages = messageList;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, null);
    return new ViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    Message message = mMessages.get(position);
    holder.mMessageText.setText(message.getId());
  }

  @Override
  public int getItemCount() {
    return mMessages.size();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    TextView mMessageText;

    public ViewHolder(View view) {
      super(view);
      mMessageText = (TextView)view.findViewById(R.id.msg_txt);
    }
  }

  public void addMessages(ArrayList<Message> messages) {
    mMessages.addAll(messages);
    notifyDataSetChanged();
  }
}

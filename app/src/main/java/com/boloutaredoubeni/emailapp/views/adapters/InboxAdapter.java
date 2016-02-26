package com.boloutaredoubeni.emailapp.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.google.api.services.gmail.model.Message;

import java.util.List;

/**
 * Copyright 2016 Boloutare Doubeni
 */
public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.ViewHolder> {

  private List<Message> mMessages;

  public InboxAdapter(List<Message> messageList) {
    mMessages = messageList;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return null;
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {

  }

  @Override
  public int getItemCount() {
    return mMessages.size();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    public ViewHolder(View view) {
      super(view);
    }

  }
}

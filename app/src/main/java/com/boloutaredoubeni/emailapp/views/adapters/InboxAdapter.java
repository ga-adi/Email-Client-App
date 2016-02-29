package com.boloutaredoubeni.emailapp.views.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.boloutaredoubeni.emailapp.R;
import com.boloutaredoubeni.emailapp.databinding.MessageItemBinding;
import com.boloutaredoubeni.emailapp.models.Email;

import java.util.ArrayList;

/**
 * Copyright 2016 Boloutare Doubeni
 */
public class InboxAdapter
    extends RecyclerView.Adapter<InboxAdapter.ViewHolder> {

  private Context mContext;
  private ArrayList<Email> mEmails;

  public InboxAdapter(ArrayList<Email> messageList, Context context) {
    mContext = context;
    mEmails = messageList;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View itemView = inflater.inflate(R.layout.message_item, null);
    return new ViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    Email email = mEmails.get(position);
    holder.getBinding().setEmail(email);
    holder.getBinding().executePendingBindings();
  }

  @Override
  public int getItemCount() {
    return mEmails.size();
  }

  static class ViewHolder extends RecyclerView.ViewHolder {
    private MessageItemBinding mBinding;

    public ViewHolder(View view) {
      super(view);
      mBinding = DataBindingUtil.bind(view);
    }

    public MessageItemBinding getBinding() { return mBinding; }
  }

  public void addMessages(ArrayList<Email> messages) {
    mEmails.addAll(messages);
    notifyDataSetChanged();
  }

  public Email getEmailAt(int position) { return mEmails.get(position); }
}

package com.example.gmailquickstart;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by Todo on 2/26/2016.
 */
public class EmailAdapter extends RecyclerView.Adapter<EmailAdapter.ViewHolder> {

    ArrayList<Email> mArrayList;

    public EmailAdapter(ArrayList<Email> array) {
        mArrayList = array;
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Email currentEmail = mArrayList.get(position);
        holder.emailSnippet.setText(currentEmail.getmSnippet());
        holder.emailSubject.setText(currentEmail.getmPayloadHeadersSubject());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.emaillayout,parent,false);
        return new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView emailSnippet;
        public TextView emailSubject;
        public ViewHolder(View view){
            super(view);
            emailSnippet = (TextView)view.findViewById(R.id.xmlEmailSnippet);
            emailSubject = (TextView)view.findViewById(R.id.xmlEmailSubject);
        }

        @Override
        public void onClick(View v) {
            //Code to open email view screen
        }
    }
}

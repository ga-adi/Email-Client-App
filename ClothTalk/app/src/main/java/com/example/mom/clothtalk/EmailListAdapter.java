package com.example.mom.clothtalk;

/**
 * Created by MOM on 2/28/16.
 */
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sbabba on 2/26/16.
 */
public class EmailListAdapter extends ArrayAdapter<InboxEmailList> {

    ArrayList<InboxEmailList> mEmailViewArrayList;

    public EmailListAdapter(Context context, ArrayList<InboxEmailList> newEmailViewArrayList) {
        super(context, -1);
        this.mEmailViewArrayList = mEmailViewArrayList;

        mEmailViewArrayList = new ArrayList<InboxEmailList>();

        mEmailViewArrayList.addAll(newEmailViewArrayList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        InboxEmailList inboxEmailList = mEmailViewArrayList.get(position);

        Context context = getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View inboxEmailsLayoutView = inflater.inflate(R.layout.email_layout,parent,false);

        TextView headerText = (TextView) inboxEmailsLayoutView.findViewById(R.id.headerText);
        TextView subjectText = (TextView) inboxEmailsLayoutView.findViewById(R.id.subjectText);
        TextView snippetText = (TextView) inboxEmailsLayoutView.findViewById(R.id.snippetText);
        TextView timeText = (TextView) inboxEmailsLayoutView.findViewById(R.id.timeText);
//
//        headerText.setText(inboxEmailList.getHeaderText());
//        subjectText.setText(inboxEmailList.getSubjectText());
//        snippetText.setText(inboxEmailList.getSnippetText());
//        timeText.setText(inboxEmailList.getTimeText());

        return inboxEmailsLayoutView;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }
}
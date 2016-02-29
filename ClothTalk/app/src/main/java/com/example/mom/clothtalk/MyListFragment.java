package com.example.mom.clothtalk;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by MOM on 2/28/16.
 */
public class MyListFragment extends ListFragment {

    ArrayList<String> mStringArray;
    onEmailSelectedListener mListener;

    public interface onEmailSelectedListener {
        public void onEmailSelectedListener(String selectedEmail);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (onEmailSelectedListener) getActivity();
        } catch (ClassCastException e){
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ArrayAdapter myFragmentAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, mStringArray);

        setListAdapter(myFragmentAdapter);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mListener.onEmailSelectedListener(getListAdapter().getItem(position).toString());
    }
}


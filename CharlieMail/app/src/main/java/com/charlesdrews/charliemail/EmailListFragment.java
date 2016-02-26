package com.charlesdrews.charliemail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Contains a RecyclerView of emails from a specified list: inbox, drafts, or sent
 * Created by charlie on 2/25/16.
 */
public class EmailListFragment extends Fragment {
    private ArrayList<Email> mEmails;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ListsPagerAdapter.SELECTED_TAB_KEY)) {
            int selectedTab = getArguments().getInt(ListsPagerAdapter.SELECTED_TAB_KEY);
            getEmails(selectedTab);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_email_list, container, false);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        recyclerView.setAdapter(new EmailRecyclerAdapter(mEmails));

        return rootView;
    }

    private void getEmails(int selectedTab) {
        //TODO - this is just for testing
        //TODO - this should be async - call gmail api here

        String testSubject = ListsPagerAdapter.TAB_NAMES[selectedTab] + " email #";

        if (mEmails == null) {
            mEmails = new ArrayList<>();
        } else {
            mEmails.clear();
        }

        for (int i = 0; i < 10; i++) {
            mEmails.add(new Email(testSubject + i));
        }
    }
}

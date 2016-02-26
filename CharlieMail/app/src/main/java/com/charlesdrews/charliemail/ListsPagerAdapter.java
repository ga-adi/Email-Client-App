package com.charlesdrews.charliemail;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Create and retrieve the fragment corresponding to each email list tab/page
 * Created by charlie on 2/25/16.
 */
public class ListsPagerAdapter extends FragmentStatePagerAdapter {
    public static final String SELECTED_TAB_KEY = "selectedTabKey";
    public static final int NUMBER_OF_TABS = 3;
    public static final int INBOX_INDEX = 0;
    public static final int DRAFTS_INDEX = 1;
    public static final int SENT_INDEX = 2;
    public static final String[] TAB_NAMES = {"Inbox", "Drafts", "Sent"};
    public static final String[] GMAIL_LABELS = {"INBOX", "DRAFT", "SENT"};

    private int mNumOfTabs;
    private EmailListFragment mInboxListFrag, mDraftListFrag, mSentListFrag;

    public ListsPagerAdapter(FragmentManager fragmentManager, int numOfTabs) {
        super(fragmentManager);
        mNumOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle arguments = new Bundle();
        arguments.putInt(SELECTED_TAB_KEY, position);

        switch (position) {
            case INBOX_INDEX:
                if (mInboxListFrag == null) {
                    mInboxListFrag = new EmailListFragment();
                    mInboxListFrag.setArguments(arguments);
                }
                return mInboxListFrag;
            case DRAFTS_INDEX:
                if (mDraftListFrag == null) {
                    mDraftListFrag = new EmailListFragment();
                    mDraftListFrag.setArguments(arguments);
                }
                return mDraftListFrag;
            case SENT_INDEX:
                if (mSentListFrag == null) {
                    mSentListFrag = new EmailListFragment();
                    mSentListFrag.setArguments(arguments);
                }
                return mSentListFrag;
            default:
                return null;
        }
    }

    @Override
    public int getCount() { return mNumOfTabs; }
}

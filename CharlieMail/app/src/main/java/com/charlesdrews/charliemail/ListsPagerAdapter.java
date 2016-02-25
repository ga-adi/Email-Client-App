package com.charlesdrews.charliemail;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by charlie on 2/25/16.
 */
public class ListsPagerAdapter extends FragmentStatePagerAdapter {
    public static final String SELECTED_TAB_KEY = "selectedTabKey";
    private int mNumOfTabs;

    public ListsPagerAdapter(FragmentManager fragmentManager, int numOfTabs) {
        super(fragmentManager);
        mNumOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        if (position >= 0 && position <=2) {
            Bundle arguments = new Bundle();
            arguments.putInt(SELECTED_TAB_KEY, position);

            EmailListFragment fragment = new EmailListFragment();
            fragment.setArguments(arguments);
            return fragment;
        }
        return null;
    }

    @Override
    public int getCount() { return mNumOfTabs; }
}

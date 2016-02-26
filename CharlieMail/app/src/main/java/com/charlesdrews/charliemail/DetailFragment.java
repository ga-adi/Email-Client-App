package com.charlesdrews.charliemail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Show detail for a selected email
 * Created by charlie on 2/26/16.
 */
public class DetailFragment extends Fragment {
    private String mSubject;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(MainActivity.SELECTED_EMAIL_KEY)) {
            mSubject = getArguments().getString(MainActivity.SELECTED_EMAIL_KEY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        if (mSubject != null) {
            TextView subject = (TextView) rootView.findViewById(R.id.detail_subject);
            subject.setText(mSubject);
        }
        return rootView;
    }
}

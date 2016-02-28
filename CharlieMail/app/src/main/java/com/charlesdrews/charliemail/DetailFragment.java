package com.charlesdrews.charliemail;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

/**
 * Show detail for a selected email
 * Created by charlie on 2/26/16.
 */
public class DetailFragment extends Fragment {
    private Email mEmail;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(MainActivity.SELECTED_EMAIL_KEY)) {
            mEmail = getArguments().getParcelable(MainActivity.SELECTED_EMAIL_KEY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        if (mEmail != null) {
            TextView mFrom = (TextView) rootView.findViewById(R.id.detail_from);
            TextView mTo = (TextView) rootView.findViewById(R.id.detail_to);
            TextView mCc = (TextView) rootView.findViewById(R.id.detail_cc);
            TextView mSentDate = (TextView) rootView.findViewById(R.id.detail_date);
            TextView mSubject = (TextView) rootView.findViewById(R.id.detail_subject);
            WebView mBody = (WebView) rootView.findViewById(R.id.detail_body);

            mFrom.setText(String.format(getString(R.string.detail_from), mEmail.getFrom()));
            mTo.setText(String.format(getString(R.string.detail_to), mEmail.getTo()));
            mCc.setText(String.format(getString(R.string.detail_cc), mEmail.getCc()));
            mSentDate.setText(mEmail.getSentDate());
            mSubject.setText(mEmail.getSubject());
            mBody.loadData(mEmail.getBody(), "text/html", "UTF-8");
            mBody.setBackgroundColor(Color.TRANSPARENT);
        }
        return rootView;
    }
}

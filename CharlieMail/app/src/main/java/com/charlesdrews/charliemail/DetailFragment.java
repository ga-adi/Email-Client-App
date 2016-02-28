package com.charlesdrews.charliemail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
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
    private TextView mFrom, mTo, mCc, mSentDate, mSubject; //, mBody;
    private WebView mBody;

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

        mFrom = (TextView) rootView.findViewById(R.id.detail_from);
        mTo = (TextView) rootView.findViewById(R.id.detail_to);
        mCc = (TextView) rootView.findViewById(R.id.detail_cc);
        mSentDate = (TextView) rootView.findViewById(R.id.detail_date);
        mSubject = (TextView) rootView.findViewById(R.id.detail_subject);
        //mBody = (TextView) rootView.findViewById(R.id.detail_body);
        mBody = (WebView) rootView.findViewById(R.id.detail_body);

        if (mEmail != null) {
            mFrom.setText(String.format(getString(R.string.detail_from), mEmail.getFrom()));
            mTo.setText(String.format(getString(R.string.detail_to), mEmail.getTo()));
            mCc.setText(String.format(getString(R.string.detail_cc), mEmail.getCc()));
            mSentDate.setText(mEmail.getSentDate());
            mSubject.setText(mEmail.getSubject());
            //mBody.setText(Html.fromHtml(mEmail.getBody()));
            mBody.loadData(mEmail.getBody(), "text/html", "utf-8");
        }
        return rootView;
    }
}

package com.example.gmailquickstart;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.example.gmailquickstart.emailStuff.EMailManager;
import com.example.gmailquickstart.emailStuff.Email;

/**
 * A fragment representing a single email detail screen.
 * This fragment is either contained in a {@link EmailListActivity}
 * in two-pane mode (on tablets) or a {@link emailDetailActivity}
 * on handsets.
 */
public class emailDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private Email mEmail;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public emailDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            Log.d("emailFragment","INside onCreate of detail Fragment");
            String theID = getArguments().getString(ARG_ITEM_ID);
            Log.d("emailFragment","Inside with theID of "+theID);
            // mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

            EMailManager eMailManager = EMailManager.getInstance();
            eMailManager.printAllToLog();
            mEmail= eMailManager.getEmailByID(theID);
            Log.d("emailDetailFragment",theID);
            if(mEmail==null){
                Log.d("emailDetailFragment","email is null");
            }else{
                Log.d("emailDetailFragment","email is not null");
            }

            Button replyButton = (Button)getActivity().findViewById(R.id.replyButton);
            if(replyButton!=null) {
                replyButton.setEnabled(true);
            }

            replyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(),"PRESSED REPLY",Toast.LENGTH_SHORT).show();
                    Intent toComposeIntent = new Intent(getActivity(), ComposeActivity.class);
                    toComposeIntent.putExtra("DRAFT",mEmail.getTheID());
                    startActivity(toComposeIntent);

                }
            });
            /*Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.content);
            }*/
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.email_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mEmail != null) {
            Log.d("emailDetailFragment", "email is not null, aboout to call webview");
           // ((WebView) rootView.findViewById(R.id.web_view)).loadData(mEmail.getBodyData(), "text/html", null);
            WebView webView = (WebView)rootView.findViewById((R.id.web_view));
            webView.loadData(mEmail.getBodyData(), "text/html", null);
            if(webView==null){
                Log.d("emailDetailFragment","webview is null");
            }
            //TextView backUpTextView  =(TextView) rootView.findViewById(R.id.backup_textView);
            int red = (int)(Math.random()*255)+1;
            int blue = (int)(Math.random()*255)+1;
            int green = (int)(Math.random()*255)+1;
            //rootView.setBackgroundColor(Color.rgb(red, blue, green));
            webView.setBackgroundColor(Color.rgb(red, blue, green));
        }

        return rootView;
    }
}

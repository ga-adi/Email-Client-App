package com.example.gmailquickstart;

import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gmailquickstart.emailStuff.EMailManager;
import com.example.gmailquickstart.emailStuff.Email;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartHeader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An activity representing a list of emails. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link emailDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */

public class EmailListActivity extends AppCompatActivity {

    GoogleAccountCredential mCredential;
    private TextView mOutputText;
    ProgressDialog mProgress;


    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int COMPOSE_EMAIL=1003;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { GmailScopes.GMAIL_LABELS,GmailScopes.GMAIL_READONLY,GmailScopes.GMAIL_COMPOSE };

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_email_list);

        //1.to show progress bar
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Calling Gmail API ...");

        //2.
        // Initialize credentials and service object.
        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));

        EMailManager emailManager = EMailManager.getInstance();
        emailManager.setCredential(mCredential);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toComposeIntent = new Intent(EmailListActivity.this,ComposeActivity.class);
                startActivityForResult(toComposeIntent,COMPOSE_EMAIL);
            }
        });

        View recyclerView = findViewById(R.id.email_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.email_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(new ArrayList<Email>()));
    }

    //OVERIDDEN METHODS BELOW
    /**
     * Called whenever this activity is pushed to the foreground, such as after
     * a call to onCreate().
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (isGooglePlayServicesAvailable()) {
            refreshResults();
        } else {
            Toast.makeText(EmailListActivity.this,"Google Play Services required:  after installing, close and relaunch this app.",Toast.LENGTH_LONG).show();
        }
    }
    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    isGooglePlayServicesAvailable();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                Log.d("MainActivity", "Inside request account picker");
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        Log.d("MainActivity","Inside request account picker with "+accountName);
                        mCredential.setSelectedAccountName(accountName);
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    mOutputText.setText("Account unspecified.");
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode != RESULT_OK) {
                    chooseAccount();
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    //methods from Google API example
    /**
     * Attempt to get a set of data from the Gmail API to display. If the
     * email address isn't known yet, then call chooseAccount() method so the
     * user can pick an account.
     */
    private void refreshResults() {
        if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else {
            if (isDeviceOnline()) {
                EMailManager emailManager = EMailManager.getInstance();
                if(emailManager.isTimeToUpdate()) {
                    Log.d("EmailListActivity","About time to call UPDATE");
                    new MakeRequestTask(mCredential).execute();
                }else{
                    Log.d("EmailListActivity","TO SOON TO UPDATE");
                }
            } else {
                Toast.makeText(EmailListActivity.this,"no network connection avail",Toast.LENGTH_LONG).show();
                //mOutputText.setText("No network connection available.");
            }
        }
    }

    /**
     * Starts an activity in Google Play Services so the user can pick an
     * account.
     */
    private void chooseAccount() {
        startActivityForResult(
                mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date. Will
     * launch an error dialog for the user to update Google Play Services if
     * possible.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        final int connectionStatusCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            return false;
        } else if (connectionStatusCode != ConnectionResult.SUCCESS ) {
            return false;
        }
        return true;
    }

    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                connectionStatusCode,
                EmailListActivity.this,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    //method to send mail

    ///INNER CLASSES BELOW
    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Email> mValues;

        public SimpleItemRecyclerViewAdapter(List<Email> items) {
            mValues = items;
        }

        public void changeUnderlyingValues(ArrayList<Email> listEmails){
                mValues.clear();
                for(Email email:listEmails) {
                    mValues.add(email);
                }

        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.email_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mSubjectView.setText(mValues.get(position).getSubject());
            holder.mContentView.setText(mValues.get(position).getSnippet());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(emailDetailFragment.ARG_ITEM_ID, holder.mItem.getTheID());
                        emailDetailFragment fragment = new emailDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.email_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, emailDetailActivity.class);
                        intent.putExtra(emailDetailFragment.ARG_ITEM_ID, holder.mItem.getTheID());

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mSubjectView;
            public final TextView mContentView;
            public Email mItem;


            public ViewHolder(View view) {
                super(view);
                mView = view;
                mSubjectView = (TextView) view.findViewById(R.id.theSubject);
                mContentView = (TextView) view.findViewById(R.id.content);
            }




            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }

    //////////////////////////////////////////
    /**
     * An asynchronous task that handles the Gmail API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.gmail.Gmail mService = null;
        private Exception mLastError = null;

        public MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.gmail.Gmail.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Gmail API Android Quickstart")
                    .build();
        }

        /**
         * Background task to call Gmail API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Fetch a list of Gmail labels attached to the specified account.
         * @return List of Strings labels.
         * @throws IOException
         */
        private List<String> getDataFromApi() throws IOException {
            // Get the labels in the user's account.
            String user = "me";
            List<String> labels = new ArrayList<String>();

            //labels
            /*ListLabelsResponse listResponse = mService.users().labels().list(user).execute();
            for (Label label : listResponse.getLabels()) {
                labels.add(label.getName());
            }*/

            //get message id
            ArrayList<String> theLabels = new ArrayList<>();
            theLabels.add("INBOX");
            theLabels.add("CATEGORY_PERSONAL");
            //ListMessagesResponse response =mService.users().messages().list(user).execute();
            ListMessagesResponse response = mService.users().messages().list(user).setLabelIds(theLabels).setIncludeSpamTrash(false).setMaxResults(20L).execute();
            //ListMessagesResponse response = mService.users().messages().list(user).setQ("google").execute();

            EMailManager emailManager =  EMailManager.getInstance();
            emailManager.startUpdate();
            StringBuilder builder = new StringBuilder();
            String body2=" ";
            String bod=" ";
            for (Message message : response.getMessages()) {

                Message actualMessage = mService.users().messages().get(user, message.getId()).execute();






                Log.d("EmalListActivity"," body "+bod);
                //actualMessage.getPayload().getBody();
                //Log.d("MainActivity", "stuff: " + actualMessage.getPayload().getHeaders());
                ArrayList<MessagePartHeader> headerContainer = (ArrayList) actualMessage.getPayload().getHeaders();
                String emailSubject=" ";
                String emailTo="";
                String emailFrom="";
                for (MessagePartHeader messagePartHeader : headerContainer) {
                    if (messagePartHeader.getName().equals("Subject")) {
                        emailSubject = messagePartHeader.getValue();
                        Log.d("emailListActivity", "subject " + emailSubject);
                    }else if(messagePartHeader.getName().equals("To")){
                        emailTo=messagePartHeader.getValue();
                    }else if(messagePartHeader.getName().equals("From")){
                        emailFrom=messagePartHeader.getValue();

                    }
                }
                ArrayList<MessagePart> messageParts= (ArrayList)actualMessage.getPayload().getParts();
                Log.d("EmailListActivity","About to loop through messageParts");

                String htmlData="";
                String plainData="";
                for(MessagePart m : messageParts){
                    if(m.getMimeType().contains("multipart")){
                        Log.d("EmailListActivity","This message contains multipart");
                        ArrayList<MessagePart>parts = (ArrayList)m.getParts();
                        Log.d("EmailListActivity","size is "+parts.size());
                        for(MessagePart p:parts){
                            Log.d("EmailListActivity","Looping through message parts "+p.getMimeType());
                            if(p.getMimeType().contains("html")){
                                Log.d("EmailListActivity", "attempting to access html data");
                                htmlData=new String(Base64.decodeBase64(p.getBody().getData()));
                                Log.d("EmailListActivity", "attempting to access html data "+htmlData);
                            }else if(p.getMimeType().contains("plain")){
                                Log.d("EmailListActivity", "attempting to access plain data");
                                plainData=new String(Base64.decodeBase64(p.getBody().getData()));
                                Log.d("EmailListActivity", "attempting to access plain data "+plainData);
                            }
                        }

                    }

                    //Log.d("EmailListActivity","MessagePart "+m.getPartId()+" "+m.getMimeType());

                    //MessagePartBody body = m.getBody();
                    //if(body==null){
                    //    continue;
                    //}
                    //byte[] data=body.decodeData();

                    //String encodedData = body.getData();
                   // byte[] data = Base64.decodeBase64(encodedData);
                    //String result = new String(Base64.decodeBase64(encodedData));
                  // String content=new String(data);
                   // Log.d("EmailListActivity","the content "+content);


                }
               // byte[] b = actualMessage.decodeRaw();
                //String str = new String(b, Charset.forName("UTF-8"));
                //Log.d("MainActivity",str);

                Email newEmail = new Email();
                newEmail.setSnippet(actualMessage.getSnippet());
                newEmail.setTheID(actualMessage.getId());
                newEmail.setSubject(emailSubject);
                newEmail.setBodyData(htmlData);
                newEmail.addTo(emailTo);
                newEmail.setFromData(emailFrom);
                emailManager.addEmail(newEmail);

                Log.d("MainActivity", "Subject: " + emailSubject+" id "+actualMessage.getId());
                //Log.d("MainActivity", "Message snippet: " + actualMessage.getSnippet());

            }
            emailManager.endUpdate();

            //send email
            //Gmail service, String from, List<String> to, String title, String body
            //ArrayList<String>toPerson =  new ArrayList<>();
            //toPerson.add("wctygret@gmail.com");
            //sendMail(mService,"nat2014111@gmail.com",toPerson,"TEST","This is the body of the email");
            //RecyclerView recyclerView = (RecyclerView)emailListActivity.this.findViewById(R.id.email_list);
            //recyclerView.setAdapter();
            return labels;
        }



        @Override
        protected void onPreExecute() {
            //mOutputText.setText("");
            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {
            mProgress.hide();
            EMailManager eMailManager = EMailManager.getInstance();
            eMailManager.printAllToLog();

            RecyclerView recyclerView = (RecyclerView)EmailListActivity.this.findViewById(R.id.email_list);
            SimpleItemRecyclerViewAdapter theAdapter= (SimpleItemRecyclerViewAdapter) recyclerView.getAdapter();
            theAdapter.changeUnderlyingValues(eMailManager.getAllEmails());
            theAdapter.notifyDataSetChanged();

        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            MainActivity.REQUEST_AUTHORIZATION);
                } else {
                    //mOutputText.setText("The following error occurred:\n"
                     //       + mLastError.getMessage());
                }
            } else {
                //mOutputText.setText("Request cancelled.");
            }
        }
    }
}

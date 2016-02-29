package com.example.gmailquickstart;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Base64;
import com.google.api.client.util.ExponentialBackOff;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;

import com.google.api.services.gmail.model.*;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    GoogleAccountCredential mCredential;
    private Toolbar mToolbar;
    private ActionBar mActionbar;
    private EmailAdapter mEmailAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mRecyclerViewLayoutManager;
    private FloatingActionButton mFAB;
    public SharedPreferences settings;

    //scopes updated for Read/write permissions
    public static final String[] SCOPES = {GmailScopes.GMAIL_LABELS,GmailScopes.GMAIL_READONLY,GmailScopes.GMAIL_COMPOSE,GmailScopes.MAIL_GOOGLE_COM,GmailScopes.GMAIL_INSERT,GmailScopes.GMAIL_MODIFY};
    public static final int REQUEST_ACCOUNT_PICKER = 1000;
    public static final int REQUEST_AUTHORIZATION = 1001;
    public static final String PREF_ACCOUNT_NAME = "stringKey";
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    public static final String SHARED_PREFS = "prefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_phone);

        //Create Layout elements
        mRecyclerView = (RecyclerView) findViewById(R.id.xmlRecyclerView);
        mFAB = (FloatingActionButton)findViewById(R.id.xmlMainfab);
        mToolbar = (Toolbar)findViewById(R.id.xmlActionBar);
        setSupportActionBar(mToolbar);
        mActionbar = getSupportActionBar();
        mActionbar.setDisplayHomeAsUpEnabled(true);
        mActionbar.setHomeButtonEnabled(true);

        // Initialize credentials and service object (ActionBar title updated to reflect
        // the email account currently listed)
        settings = getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE);
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));
        mActionbar.setTitle(settings.getString(MainActivity.PREF_ACCOUNT_NAME,""));

        //Instantiate List of Email Objects in RecyclerView via custom adapter
        mEmailAdapter = new EmailAdapter(EmailList.getInstance().getAllEmails());
        mRecyclerViewLayoutManager = new LinearLayoutManager(MainActivity.this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mRecyclerViewLayoutManager);
        mRecyclerView.setAdapter(mEmailAdapter);

        //Floating action button launches compose email screen
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ComposeEmailActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isGooglePlayServicesAvailable()) {refreshResults();}
        else {Toast.makeText(MainActivity.this, "\"Google Play Services required: \"" +
                "+ \"after installing, close and relaunch this app.\"", Toast.LENGTH_SHORT).show();}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {isGooglePlayServicesAvailable();}
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        mCredential.setSelectedAccountName(accountName);
                        settings = getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();}
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(MainActivity.this, "Account unspecified.", Toast.LENGTH_SHORT).show();}
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode != RESULT_OK) {chooseAccount();}
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //Method to retrieve data from the Gmail servers... If no account has been selected yet,
    // chooseAccount() method is called so the user can pick an account.
    private void refreshResults() {
        if (mCredential.getSelectedAccountName() == null) {chooseAccount();}
        else {
            if (isDeviceOnline()) {new MakeRequestTask(mCredential).execute();}
            else {
                Toast.makeText(MainActivity.this, "No network connection available.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Starts an activity in Google Play Services so the user can pick an account
    private void chooseAccount() {
        startActivityForResult(mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
    }

    //Check to confirm network connectivity
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**Check that Google Play services APK is installed and up to date. Will launch an error dialog
     * for the user to update Google Play Services if possible.*/
    private boolean isGooglePlayServicesAvailable() {
        final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            return false;
        } else if (connectionStatusCode != ConnectionResult.SUCCESS ) {
            return false;
        }
        return true;
    }

    //Display an error dialog showing that Google Play Services is missing or out of date.
    void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        Dialog dialog = GooglePlayServicesUtil.getErrorDialog(connectionStatusCode,
                MainActivity.this, REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    //An asynchronous task to handle Gmail API call to pull all emails into RecyclerView
    private class MakeRequestTask extends AsyncTask<Void, Void, ArrayList<Email>> {
        private com.google.api.services.gmail.Gmail mService = null;
        private Exception mLastError = null;

        //Async constructor takes google account credentials to instantiate a Gmail object
        public MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.gmail.Gmail.Builder(transport, jsonFactory, credential)
                    .setApplicationName("Gmail API Android Quickstart").build();
        }

        //Background task to call Gmail API and pull all data in worker thread
        @Override
        protected ArrayList<Email> doInBackground(Void... params) {
            try {return getDataFromApi();}
            catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        //Fetch a list of Gmail message data attached to the specified account
        private ArrayList<Email> getDataFromApi() throws IOException {

            //Pull full list of emails from Gmail servers
            ListMessagesResponse list = mService.users().messages().list("me").execute();
            List<Message> messages = list.getMessages();

            //Populate individual Email objects from the list of messages pulled from Gmail
            for (int i = 0; i < messages.size(); i++) {

                //Retrieve Message object from each id in the list of messages
                Message message = messages.get(i);
                Message actual = mService.users().messages().get("me", message.getId()).execute();

                //Confirm if each Message is already accounted for in EmailList Singleton. If it is,
                // the data is pulled directly from the arraylist instead of the network to speed
                // up runtime of populating the email list
                Boolean isNew = true;
                for (Email z:EmailList.getInstance().getAllEmails()) {
                    if(z.getmEmailID().equals(actual.getId())){
                        isNew = false;}
                }

                if(isNew){
                    //create new email object to represent the message in the app and show the
                    // subject/snippet in the RecyclerView.  Only ID/Snippet/LabelIDs/Subject are
                    // pulled in this activity's network call to reduce the amount of data needed
                    // to load the page (secondary network call made to populate remaining data
                    // points in the ReadEmailActivity)
                    Email email = new Email();
                    email.setmEmailID(actual.getId());
                    email.setmSnippet(actual.getSnippet());
                    email.setmLabelIDs(actual.getLabelIds().toArray(email.getmLabelIDs()));

                    //test to confirm JSON location indices for the "subject" Email Header property
                    for (int x = 0; x < actual.getPayload().getHeaders().size(); x++) {
                        if(actual.getPayload().getHeaders().get(x).getName().equals("Subject")){
                            email.setmPayloadHeadersSubject(actual.getPayload().getHeaders().get(x).getValue());}
                    }
                    EmailList.getInstance().addEmail(i, email);
                }
            }

            return EmailList.getInstance().getInbox();
        }

        //update the email list once all data is confirmed
        @Override
        protected void onPostExecute(ArrayList<Email> output) {
            mEmailAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onCancelled() {
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError).getConnectionStatusCode());}
                else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(((UserRecoverableAuthIOException) mLastError).getIntent(),MainActivity.REQUEST_AUTHORIZATION);}
                else {
                    Toast.makeText(MainActivity.this, "The following error occurred:\n"+ mLastError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            else {Toast.makeText(MainActivity.this, "Request cancelled.", Toast.LENGTH_SHORT).show();}
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuinflater = getMenuInflater();
        menuinflater.inflate(R.menu.options_menu,menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.xmlActionBarSearch).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Type Keyword");
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);

        //Code to update search results dynamically as the User types
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    EmailList.getInstance().getInbox();
                    mEmailAdapter.notifyDataSetChanged();
                } else {
                    ArrayList<Email> newList = new ArrayList<>();
                    for (Email email:EmailList.getInstance().getInbox()) {
                        if(email.getmPayloadHeadersSubject().contains(newText)){
                            newList.add(email);
                        }
                    }
                    EmailList.getInstance().clear();
                    EmailList.getInstance().getAllEmails().addAll(newList);
                    mEmailAdapter.notifyDataSetChanged();
                }
                return true;
            }
        });



        return super.onCreateOptionsMenu(menu);
    }

    //Options menu click prompts user to change email accounts
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_changeAccount:
                //If account is changed, the arraylist is cleared prior to populating it with the new email objects
                chooseAccount();
                EmailList.getInstance().getAllEmails().clear();
                mEmailAdapter.notifyDataSetChanged();
                refreshResults();

        }
        return super.onOptionsItemSelected(item);
    }
}
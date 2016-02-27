package com.example.mom.clothtalk;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.gmail.model.ListMessagesResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private GoogleAccountCredential mCredential;
    private ListView mEmailsListView;
    private ProgressDialog mProgress;
    private ArrayList<String> mInboxArrayList;
    private ArrayAdapter<String> mAdapter;

    private boolean mTwoPane;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {GmailScopes.GMAIL_LABELS,GmailScopes.GMAIL_READONLY, GmailScopes.GMAIL_COMPOSE, GmailScopes.GMAIL_INSERT, GmailScopes.GMAIL_MODIFY, GmailScopes.MAIL_GOOGLE_COM};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Calling Gmail API...");

        mEmailsListView = (ListView) findViewById(R.id.emails_ListView);
        mInboxArrayList = new ArrayList<String>();


        mAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, mInboxArrayList);
        mEmailsListView.setAdapter(mAdapter);

        MailAsyncTask mailAsyncTask = new MailAsyncTask(mCredential);
        mailAsyncTask.execute();
        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);



        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                startActivity(intent);
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    protected void onResume() {
//        mInboxArrayList.clear();
        super.onResume();
        if (isGooglePlayServicesAvailable()) {
            refreshResults();
        } else {
            Toast.makeText(MainActivity.this, "Google Play Services require: after installing, close and relaunch this app", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    isGooglePlayServicesAvailable();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        mCredential.setSelectedAccountName(accountName);
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(MainActivity.this, "Account unspecified.", Toast.LENGTH_SHORT).show();
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
    private void refreshResults() {
        if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else {
            if (isDeviceOnline()) {
//                Log.d("Gets here","GET GERE");
                new MailAsyncTask(mCredential).execute();
            } else {
                Toast.makeText(MainActivity.this, "No network connection available.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void chooseAccount() {
        startActivityForResult(
                mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
    }
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability  googleApi = GoogleApiAvailability.getInstance();
        int connectionStatusCode =
                googleApi.isGooglePlayServicesAvailable(this);
        if (googleApi.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            return false;
        } else if (connectionStatusCode != ConnectionResult.SUCCESS ) {
            return false;
        }
        return true;
    }

    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                connectionStatusCode,
                MainActivity.this,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    private String getInputData(InputStream inputStream)throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String data;
        while ((data = reader.readLine())!= null){
            stringBuilder.append(data);
        }
        reader.close();
        return stringBuilder.toString();
    }

    public class MailAsyncTask extends AsyncTask<String,Void, List<String>>{
        private com.google.api.services.gmail.Gmail mService = null;
        private Exception mLastError = null;

        public MailAsyncTask(GoogleAccountCredential credential){
            System.out.println("In MailAsyncTask...");
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.gmail.Gmail.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Gmail API Android Quickstart")
                    .build();
        }
        @Override
        protected void onPreExecute() {
            mProgress.show();
        }

        @Override
        protected List<String> doInBackground(String... urls) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }
        private List<String> getDataFromApi() throws IOException {
//            Log.d("String", "Gets Here");
            String user = "me";
            List<String> labels = new ArrayList<String>();
            List<String> messages = new ArrayList<>();

            ListLabelsResponse listResponse = mService.users().labels().list(user).execute();
            ListMessagesResponse messageResponse = mService.users().messages().list(user).execute();

            for (Label label : listResponse.getLabels()) {
                labels.add(label.getName());
                String labelName = label.getName();
//                mInboxArrayList.add(labelName);
//                Log.d("String", label.getName());
            }
            int counter = 0;
            for (com.google.api.services.gmail.model.Message message : messageResponse.getMessages()) {
//                Log.d("Sincerely is it?", "Gonna stop here?");
                counter++;
                com.google.api.services.gmail.model.Message messageContent = mService.users().messages().get("me", message.getId()).execute();
                String s = messageContent.getSnippet();
                mInboxArrayList.add(s);
                if (counter >= 10) {
                    break;
                }
            }
            return labels;
        }
        @Override
        protected void onPostExecute(List<String> urls) {
            mProgress.hide();
            if (urls == null || urls.size() == 0){
                Toast.makeText(MainActivity.this, "No Results Returned", Toast.LENGTH_SHORT).show();
            }
            urls.add(0, "Data retrieved using the Gmail API:");
            mAdapter.notifyDataSetChanged();
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
                    Toast.makeText(MainActivity.this, "The following error occured: " + mLastError.getMessage(), Toast.LENGTH_SHORT).show();
//                    mOutputText.setText("The following error occurred:\n"
//                            + mLastError.getMessage());
                    mLastError.getMessage();
                }
            } else {
                Toast.makeText(MainActivity.this, "Request cancelled.", Toast.LENGTH_SHORT).show();
//                mOutputText.setText("Request cancelled.");
            }
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();

        ComponentName component = new ComponentName(this, MainActivity.class);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(component));
        searchView.getSuggestionsAdapter();


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}

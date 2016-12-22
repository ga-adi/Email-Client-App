package com.charlesdrews.charliemail;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.gmail.GmailScopes;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String SHARED_PREFS_KEY = MainActivity.class.getPackage().getName() + ".SHARED_PREFS";
    public static final String SELECTED_EMAIL_KEY = "selectedEmailKey";
    public static final String COMPOSE_INDICATOR_KEY = "composeIndicator";
    private static final int REQUEST_ACCOUNT_PICKER = 1000;
    public static final int REQUEST_AUTHORIZATION = 1001;
    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    private static final int REQUEST_GET_ACCOUNTS_PERMISSION = 1003;
    public static final String PREF_ACCOUNT_NAME = "accountName";
    public static final String[] SCOPES = {GmailScopes.GMAIL_MODIFY};

    private GoogleAccountCredential mCredential;
    private String mAuthResultMsg = "";
    private boolean mTwoPanes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences settings = getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));

        mTwoPanes = (findViewById(R.id.detail_fragment_container) != null);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(MainActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mCredential.getSelectedAccountName() == null) {
            if (isGooglePlayServicesAvailable()) {
                chooseAccount();
            } else {
                mAuthResultMsg = "Google Play Services required: after installing, close and relaunch this app.";
            }

            if (!mAuthResultMsg.isEmpty()) {
                Toast.makeText(MainActivity.this, mAuthResultMsg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public GoogleAccountCredential getCredential() {
        return mCredential;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onEmailSelected(String label, Email email) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SELECTED_EMAIL_KEY, email);

        if (mTwoPanes) {
            Fragment fragment;
            if (label.equals(ListsPagerAdapter.GMAIL_LABELS[1])) { //DRAFT
                fragment = new ComposeFragment();
            } else {
                fragment = new DetailFragment();
            }
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            if (label.equals(ListsPagerAdapter.GMAIL_LABELS[1])) { //DRAFT
                bundle.putBoolean(COMPOSE_INDICATOR_KEY, true);
            } else {
                bundle.putBoolean(COMPOSE_INDICATOR_KEY, false);
            }
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                if (mTwoPanes) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(COMPOSE_INDICATOR_KEY, true);

                    ComposeFragment composeFragment = new ComposeFragment();
                    composeFragment.setArguments(bundle);

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.detail_fragment_container, composeFragment)
                            .commit();
                } else {
                    Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                    intent.putExtra(COMPOSE_INDICATOR_KEY, true);
                    startActivity(intent);
                }
                break;
            default:
                break;
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

                        SharedPreferences settings =
                                getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();

                        // In order to set the selected account on the credential object,
                        // must have GET_ACCOUNTS permission. Need to request that for API 23+
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                                ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS)
                                        != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this,
                                    new String[]{Manifest.permission.GET_ACCOUNTS},
                                    REQUEST_GET_ACCOUNTS_PERMISSION);

                        } else {
                            mCredential.setSelectedAccountName(accountName);
                            mAuthResultMsg = accountName + " authorized.";
                        }
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    mAuthResultMsg = "Account unspecified.";
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_GET_ACCOUNTS_PERMISSION:
                SharedPreferences settings = getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
                String accountName = settings.getString(PREF_ACCOUNT_NAME, null);
                if (accountName != null) {
                    mCredential.setSelectedAccountName(accountName);
                    mAuthResultMsg = accountName + " authorized.";
                }
                break;
            default:
                break;
        }
    }

    void chooseAccount() {
        startActivityForResult(
                mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
    }

    private boolean isGooglePlayServicesAvailable() {
        final int connectionStatusCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            return false;
        } else if (connectionStatusCode != ConnectionResult.SUCCESS) {
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
}

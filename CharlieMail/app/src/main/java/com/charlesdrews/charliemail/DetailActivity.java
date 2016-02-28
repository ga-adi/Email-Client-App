package com.charlesdrews.charliemail;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //TODO - if user rotates to landscape while here, should revert to MainActivity instead

        Email email = null;
        if (getIntent().getExtras() != null) {
            email = getIntent().getParcelableExtra(MainActivity.SELECTED_EMAIL_KEY);
        }

        if (email != null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(MainActivity.SELECTED_EMAIL_KEY, email);

            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(arguments);

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.detail_fragment_container, detailFragment)
                        .commit();
            } else {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_fragment_container, detailFragment)
                        .commit();
            }
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO - make compose email button
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}

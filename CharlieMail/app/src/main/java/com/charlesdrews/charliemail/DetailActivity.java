package com.charlesdrews.charliemail;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

        // Check if sent here by MainActivity in order to compose an email
        boolean compose = getIntent().getExtras().getBoolean(MainActivity.COMPOSE_INDICATOR_KEY);
        if (compose) {
            handleComposeFragment(savedInstanceState);
        } else {
            // If not here to compose, then here to view an email
            handleDetailFragment(savedInstanceState);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this, DetailActivity.class);
                intent.putExtra(MainActivity.COMPOSE_INDICATOR_KEY, true);
                startActivity(intent);
            }
        });
    }

    private void handleComposeFragment(Bundle savedInstanceState) {
        ComposeFragment composeFragment = new ComposeFragment();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_fragment_container, composeFragment)
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, composeFragment)
                    .commit();
        }
    }

    private void handleDetailFragment(Bundle savedInstanceState) {
        Email email = null;
        if (getIntent().getExtras() != null) {
            email = getIntent().getParcelableExtra(MainActivity.SELECTED_EMAIL_KEY);
        }

        if (email == null) {
            startActivity(new Intent(DetailActivity.this, MainActivity.class));
        } else {
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
    }
}

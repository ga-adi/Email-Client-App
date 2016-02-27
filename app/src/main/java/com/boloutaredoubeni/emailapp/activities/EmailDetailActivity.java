package com.boloutaredoubeni.emailapp.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.boloutaredoubeni.emailapp.fragments.EmailDetailFragment;

public class EmailDetailActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (getResources().getConfiguration().orientation ==
        Configuration.ORIENTATION_LANDSCAPE) {
      finish();
      return;
    }

    if (savedInstanceState == null) {
      EmailDetailFragment fragment = new EmailDetailFragment();
      fragment.setArguments(getIntent().getExtras());
      getSupportFragmentManager()
          .beginTransaction()
          .add(android.R.id.content, fragment)
          .commit();
    }

  }
}

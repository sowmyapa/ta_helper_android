package com.cs442.team4.tahelper.preferences;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.cs442.team4.tahelper.R;

public class MyPreferenceActivity extends PreferenceActivity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.userpreferences);
  }

}
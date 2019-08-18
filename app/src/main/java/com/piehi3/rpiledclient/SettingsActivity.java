package com.piehi3.rpiledclient;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends AppCompatActivity {

    //host name
    public static final String KEY_PREF_HOST_NAME = "host_name";
    public static final String PREF_HOST_NAME_DEFAULT = "raspberrypiLEDServer";

    //port for host
    public static final String KEY_PREF_PORT = "port";
    public static final int PREF_PORT_DEFAULT = 6066;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        //sets the new setting fragment as the main context of the activity
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();

        //action for going back to the main menu
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);//associates the preferences files with the settings fragment
        }
    }
}
package edu.mit.zbt.rushgps.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;


public class DevSettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);

        updateSummaries();
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updateSummary(sharedPreferences, key);
    }

    private void updateSummaries() {
        SharedPreferences prefs = getPreferenceScreen().getSharedPreferences();
        for (String key : prefs.getAll().keySet()) {
            updateSummary(prefs, key);
        }
    }

    private void updateSummary(SharedPreferences prefs, String key) {
        Preference pref = findPreference(key);
        String val = prefs.getString(pref.getKey(), "");
        pref.setSummary(val);
    }

}

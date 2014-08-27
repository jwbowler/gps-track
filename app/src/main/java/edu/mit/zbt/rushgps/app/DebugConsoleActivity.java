package edu.mit.zbt.rushgps.app;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;


public class DebugConsoleActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_console);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        setupTabs();
    }

    private void setupTabs() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(true);

        ActionBar.Tab tab1 = actionBar
            .newTab()
            .setText("Location data")
            .setTag("DebugDataFragment")
            .setTabListener(
                new FragmentTabListener<DebugDataFragment>(this, "first",
                                DebugDataFragment.class));

        actionBar.addTab(tab1);
        actionBar.selectTab(tab1);

        ActionBar.Tab tab2 = actionBar
            .newTab()
            .setText("App state")
            .setTag("DebugSettingsFragment")
            .setTabListener(
                new FragmentTabListener<DebugSettingsFragment>(this, "second",
                                DebugSettingsFragment.class));

        actionBar.addTab(tab2);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

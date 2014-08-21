package edu.mit.zbt.rushgps.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.lang.reflect.Field;


public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient());

        mWebView.loadUrl("http://www.google.com");

        // Old devices have a dedicated "menu" button that users always forget exists. For menu
        // items that don't get icons in the top bar, new devices will put a menu-looking button on
        // screen. This hack forces this behavior on all devices.
        //
        // http://stackoverflow.com/questions/20444596/how-to-force-action-bar-overflow-icon-to-show
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");

            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            // #yolo
        }
    }

    protected void onStart() {
        super.onStart();

        LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            displayPromptForEnablingGPS();
        }

        startGpsService();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void onChangeCarClick(MenuItem item) {
        //DialogFragment carListDialogFragment = new CarListDialogFragment();
        //carListDialogFragment.show(getFragmentManager(), null);

        startActivity(new Intent(this, CarListActivity.class));
    }

    public void onDevConsoleClick(MenuItem item) {
        startActivity(new Intent(this, DevConsoleActivity.class));
    }

    public void onStopClick(MenuItem item) {
        stopGpsService();
        finish();
    }

    private void startGpsService() {
        startService(new Intent(this, GpsService.class));
    }

    private void stopGpsService() {
        stopService(new Intent(this, GpsService.class));
    }

    private void displayPromptForEnablingGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final int message = R.string.prompt_gps;

        builder.setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                startActivity(new Intent(action));
                                d.dismiss();
                            }
                        }
                )
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                            }
                        }
                );
        builder.create().show();
    }
}


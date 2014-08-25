package edu.mit.zbt.rushgps.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.lang.reflect.Field;


public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    public static final int REQUEST_CODE_CAR_SELECTION_NECESSARY = 0;
    public static final int REQUEST_CODE_CAR_SELECTION_OPTIONAL = 1;

    public static final int RESULT_CODE_CAR_SELECTED = 1;
    public static final int RESULT_CODE_CAR_NOT_SELECTED = -1;
    public static final int RESULT_CODE_USER_PROBABLY_HIT_BACK_BUTTON = 0;

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient());

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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelable("currentCar", CarInfo.getCurrentCar());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        CarInfo.setCurrentCar((CarInfo) savedInstanceState.getParcelable("currentCar"));
    }

    protected void onStart() {
        super.onStart();

        LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            displayPromptForEnablingGPS();
        }

        startGpsService();

        if (!isGpsEndpointSet()) {
            startCarListActivity();
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String baseUrl = prefs.getString("pref_base", "");
        String endpoint = prefs.getString("pref_webview_endpoint", "");
        mWebView.loadUrl(baseUrl + endpoint);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "requestCode = " + Integer.valueOf(requestCode).toString());
        Log.d(TAG, "resultCode = " + Integer.valueOf(resultCode).toString());

        if (requestCode == REQUEST_CODE_CAR_SELECTION_NECESSARY) {
            if (resultCode != RESULT_CODE_CAR_SELECTED) {
                stopGpsService();
                finish();
            }
        }
    }

    private void startCarListActivity() {
        boolean carSelectionNecessary = !isGpsEndpointSet();

        int requestCode = carSelectionNecessary ? REQUEST_CODE_CAR_SELECTION_NECESSARY
                                                : REQUEST_CODE_CAR_SELECTION_OPTIONAL;

        Intent intent = new Intent(this, CarListActivity.class);
        intent.putExtra("carSelectionNecessary", carSelectionNecessary);

        startActivityForResult(intent, requestCode);
    }

    public void onChangeCarClick(MenuItem item) {
        //DialogFragment carListDialogFragment = new CarListDialogFragment();
        //carListDialogFragment.show(getFragmentManager(), null);

        startCarListActivity();
    }

    public void onDevConsoleClick(MenuItem item) {
        startActivity(new Intent(this, DevConsoleActivity.class));
    }

    public void onStopClick(MenuItem item) {
        stopGpsService();
        finish();
    }

    public boolean isGpsEndpointSet() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return !(prefs.getString("pref_gps_endpoint", "").equals(""));
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


package edu.mit.zbt.rushgps.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.lang.reflect.Field;


public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    public static final int REQUEST_CODE_SELECTION_NECESSARY = 0;
    public static final int REQUEST_CODE_SELECTION_OPTIONAL = 1;

    public static final int RESULT_CODE_SELECTED = 1;
    public static final int RESULT_CODE_NOT_SELECTED = -1;
    public static final int RESULT_CODE_USER_PROBABLY_HIT_BACK_BUTTON = 0;

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, true);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminateVisibility(true);

        setContentView(R.layout.activity_main);

        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
//        mWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mWebView.setWebViewClient(new MainActivityWebViewClient(this));

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
            Log.e(TAG, e.getMessage());
        }
    }

    protected void onStart() {
        super.onStart();

        LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            displayPromptForEnablingGPS();
        }

        startGpsService();

        if (!ActiveDriver.isSetInPreferences(this)) {
            startActiveDriversListActivity();
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String baseUrl = prefs.getString("pref_base_url", "");
        String endpointPattern = prefs.getString("pref_webview_endpoint_pattern", "");
        String activeDriverId = prefs.getString("pref_driver_id", "");

        String driverIdUrl = baseUrl + String.format(endpointPattern, activeDriverId);
        Log.d(TAG, "driverIdUrl = " + driverIdUrl);
        mWebView.loadUrl(driverIdUrl);
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

        if (requestCode == REQUEST_CODE_SELECTION_NECESSARY) {
            if (resultCode != RESULT_CODE_SELECTED) {
                quitApp();
            }
        }
    }

    private void startActiveDriversListActivity() {
        boolean selectionNecessary = !ActiveDriver.isSetInPreferences(this);

        int requestCode = selectionNecessary ? REQUEST_CODE_SELECTION_NECESSARY
                                             : REQUEST_CODE_SELECTION_OPTIONAL;

        Intent intent = new Intent(this, ActiveDriversListActivity.class);
        intent.putExtra("selectionNecessary", selectionNecessary);

        startActivityForResult(intent, requestCode);
    }

    public void onChangeCarClick(MenuItem item) {
        startActiveDriversListActivity();
    }

    public void onDevConsoleClick(MenuItem item) {
        startActivity(new Intent(this, DebugConsoleActivity.class));
    }

    public void onStopClick(MenuItem item) {
        quitApp();
    }

    private void quitApp() {
        stopGpsService();
        ActiveDriver.resetPreferences(this);
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

    private class MainActivityWebViewClient extends WebViewClient {
        private Context mContext;

        public MainActivityWebViewClient(Context context) {
            mContext = context;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            setProgressBarIndeterminateVisibility(false);
        }

//        @Override
//        public void onReceivedError(WebView view, int errorCode, String description,
//                                    String failingUrl) {
//            String message = "Failed to load " + failingUrl
//                             + " - " + description;
//            Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
//        }
    }
}


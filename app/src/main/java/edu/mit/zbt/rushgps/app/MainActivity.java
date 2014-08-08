package edu.mit.zbt.rushgps.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.view.View;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    protected void onDestroy() {
        super.onDestroy();
    }

    // Assigned to the button in activity_main.xml
    public void onStopButtonClick(View v) {
        stopGpsService();
        finish();
    }

    private void startGpsService() {
        Intent serviceIntent = new Intent(this, GpsService.class);
        startService(serviceIntent);
    }

    private void stopGpsService() {
        Intent serviceIntent = new Intent(this, GpsService.class);
        stopService(serviceIntent);
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


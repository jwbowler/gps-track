package edu.mit.zbt.rushgps.app;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class GpsService extends Service
        implements LocationListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "GpsService";

    public static final String LOCATION_INTENT_FILTER = "locationChanged";

    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;

    private Location mLastLocation = null;
    private LocationManager mLocationManager = null;

    private NotificationManager mNotificationManager = null;

    private boolean mAlreadyRunning = false;

    public class GpsServiceBinder extends Binder {
        public Location getLocation() {
            return GpsService.this.mLastLocation;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new GpsServiceBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);

        if (mAlreadyRunning) {
            Log.d(TAG, "already running!");
            return START_STICKY;
        }

        updateUrls();

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Criteria locationProviderCriteria = new Criteria();
        locationProviderCriteria.setAccuracy(Criteria.ACCURACY_FINE);

        mLocationManager.requestLocationUpdates(
                LOCATION_INTERVAL, LOCATION_DISTANCE,
                locationProviderCriteria, this, null);

        createOngoingNotification();

        mAlreadyRunning = true;

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();

        if (mLocationManager != null) {
            mLocationManager.removeUpdates(this);
        }

        if (mNotificationManager != null) {
            mNotificationManager.cancelAll();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged: " + location);
        mLastLocation = location;

        // Send the location update to the HTTP endpoint.
        if (RestClient.isGpsEndpointSet()) {
            RestClient.postGps(location);
        }

        // Broadcast the location update to other classes in this app (e.g. DevDebugDataFragment).
        Intent broadcast = new Intent(LOCATION_INTENT_FILTER);
        broadcast.putExtra("location", location);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG, "onProviderDisabled: " + provider);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG, "onProviderEnabled: " + provider);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, "onStatusChanged: " + provider);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updateUrls();
    }

    private void updateUrls() {
        updateBaseUrl();
        updateCarListEndpoint();
        updateGpsEndpoint();
    }

    private void updateBaseUrl() {
        String url = PreferenceManager.getDefaultSharedPreferences(this)
                                      .getString("pref_base", "");
        RestClient.setBaseUrl(url);
    }

    private void updateCarListEndpoint() {
        String endpoint = PreferenceManager.getDefaultSharedPreferences(this)
                                           .getString("pref_car_list_endpoint", "");
        RestClient.setCarListEndpoint(endpoint);
    }

    private void updateGpsEndpoint() {
        String endpoint = PreferenceManager.getDefaultSharedPreferences(this)
                                           .getString("pref_gps_endpoint", "");
        RestClient.setGpsEndpoint(endpoint);
    }

    private void createOngoingNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(getString(R.string.notification_content))
                        .setOngoing(true);

        // The stack builder object will contain an artificial back stack for the started Activity.
        // This ensures that navigating backward from the Activity leads back to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        Intent resultIntent = new Intent(this, MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        // Something something intent
        builder.setContentIntent(stackBuilder.getPendingIntent(
                0, PendingIntent.FLAG_UPDATE_CURRENT));

        mNotificationManager.notify(0, builder.build());
    }

}

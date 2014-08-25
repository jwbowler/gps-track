package edu.mit.zbt.rushgps.app;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DevDebugDataFragment extends Fragment implements ServiceConnection {
    private static String TAG = "DevDebugDataFragment";

    private LocalBroadcastManager mLocalBroadcastManager = null;
    private GpsService.GpsServiceBinder mGpsServiceBinder = null;

    private TextView mLatitudeView = null;
    private TextView mLongitudeView = null;
    private TextView mAccuracyView = null;

    private BroadcastReceiver mLocationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location = intent.getParcelableExtra("location");
            updateWithLocation(location);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context context = getActivity().getApplicationContext();
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(context);

        context.bindService(new Intent(context, GpsService.class), this, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View layout = inflater.inflate(R.layout.fragment_debug_data, container, false);
        mLatitudeView = (TextView) layout.findViewById(R.id.debug_latitude);
        mLongitudeView = (TextView) layout.findViewById(R.id.debug_longitude);
        mAccuracyView = (TextView) layout.findViewById(R.id.debug_accuracy);

        mLocalBroadcastManager.registerReceiver(
                mLocationReceiver, new IntentFilter(GpsService.LOCATION_INTENT_FILTER));

        updateDataViews();

        return layout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mLocalBroadcastManager.unregisterReceiver(mLocationReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Context context = getActivity().getApplicationContext();
        context.unbindService(this);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mGpsServiceBinder = (GpsService.GpsServiceBinder) service;

        updateDataViews();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        // lol
    }

    private void updateDataViews() {
        if (mGpsServiceBinder == null) {
            writeToAllViews("Waiting for location service");
            return;
        }

        Location location = mGpsServiceBinder.getLocation();
        if (location == null) {
            writeToAllViews("Waiting for location");
            return;
        }

        updateWithLocation(location);
    }

    private void writeToAllViews(String string) {
        mLatitudeView.setText(string);
        mLongitudeView.setText(string);
        mAccuracyView.setText(string);
    }

    private void updateWithLocation(Location location) {
        String latitude = ((Double) location.getLatitude()).toString();
        String longitude = ((Double) location.getLongitude()).toString();
        String accuracy = ((Float) location.getAccuracy()).toString();

        mLatitudeView.setText(latitude);
        mLongitudeView.setText(longitude);
        mAccuracyView.setText(accuracy);
    }

}

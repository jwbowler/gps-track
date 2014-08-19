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

    private BroadcastReceiver mLocationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location = intent.getParcelableExtra("location");

            String latitude = ((Double) location.getLatitude()).toString();
            mLatitudeView.setText(latitude);
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
        mLatitudeView = (TextView) layout.findViewById(R.id.view_latitude);

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

    public void updateDataViews() {
        if (mGpsServiceBinder == null) {
            mLatitudeView.setText("Waiting for location service");
            return;
        }

        Location location = mGpsServiceBinder.getLocation();
        if (location == null) {
            mLatitudeView.setText("Waiting for location");
            return;
        }

        String latitude = ((Double) location.getLatitude()).toString();
        mLatitudeView.setText(latitude);
    }

//    @Override
//    public void onStart() {
//        try {
//            Context context = getActivity().getApplicationContext();
//            context.bindService(new Intent(context, GpsService.class), this, 0);
//        } catch (NullPointerException e) {
//            Log.e(TAG, e.getMessage());
//        }
//
//    }
//
//    @Override
//    public void onStop() {
//        try {
//            getActivity().getApplicationContext().unbindService(this);
//        } catch (NullPointerException e) {
//            Log.e(TAG, e.getMessage());
//        }
//    }

}

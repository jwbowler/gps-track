package edu.mit.zbt.rushgps.app;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class CarListFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<List<CarInfo>> {

    private static String TAG = "CarListFragment";

    private ArrayAdapter mAdapter;
    private List<CarInfo> mCarList;

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new ArrayAdapter<CarInfo>(getActivity(), android.R.layout.simple_list_item_1);
        setListAdapter(mAdapter);
        setListShown(false);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        CarInfo car = mCarList.get(position);
        CarInfo.setCurrentCar(car);

        String carId = mCarList.get(position).getId();
        Log.d(TAG, "Switching to car: " + carId);
        String gpsEndpoint = "/cars/" + carId + "/location";
        PreferenceManager.getDefaultSharedPreferences(getActivity())
                         .edit().putString("pref_gps_endpoint", gpsEndpoint).commit();

        getActivity().setResult(MainActivity.RESULT_CODE_CAR_SELECTED);
        getActivity().finish();
    }

    @Override
    public Loader<List<CarInfo>> onCreateLoader(int id, Bundle args) {
        return new CarListLoader(getActivity());
    }

    @Override
    public void onLoadFinished(final Loader<List<CarInfo>> loader, List<CarInfo> data) {
        if (data.size() == 0) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            final int message = R.string.alert_retry_get_carlist;
            final int loaderId = loader.getId();

            final String negativeMessage;
            if (getActivity().getIntent().getExtras().getBoolean("carSelectionNecessary", false)) {
                negativeMessage = "Quit";
            } else {
                String carDescription = CarInfo.getCurrentCar().getDescription();
                negativeMessage = "Stick with \"" + carDescription + "\"";
            }

            builder.setMessage(message)
                    .setPositiveButton("Retry",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface d, int id) {
                                    getLoaderManager().getLoader(loaderId).forceLoad();
                                    d.dismiss();
                                }
                            })
                    .setNeutralButton("Debug",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getActivity(),
                                                               DevConsoleActivity.class);
                                    getActivity().startActivity(intent);
                                }
                            })
                    .setNegativeButton(negativeMessage,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface d, int id) {
                                    getActivity().setResult(
                                            MainActivity.RESULT_CODE_CAR_NOT_SELECTED);
                                    getActivity().finish();
                                    d.cancel();
                                }
                            }
                    );
            builder.create().show();

        } else {
            mAdapter.clear();
            mAdapter.addAll(data);
            mCarList = data;

            setListShown(true);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<CarInfo>> loader) {
        mAdapter.clear();
    }
}

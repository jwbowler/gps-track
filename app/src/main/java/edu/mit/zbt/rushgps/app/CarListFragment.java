package edu.mit.zbt.rushgps.app;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class CarListFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<List<CarInfo>> {

    private static String TAG = "CarListFragment";

    private ArrayAdapter mAdapter;

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new ArrayAdapter<CarInfo>(getActivity(), android.R.layout.simple_list_item_1);
        setListAdapter(mAdapter);
        setListShown(false);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(TAG, "hi!");
    }

    @Override
    public Loader<List<CarInfo>> onCreateLoader(int id, Bundle args) {
        return new CarListLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<CarInfo>> loader, List<CarInfo> data) {
        mAdapter.clear();
        mAdapter.addAll(data);

        setListShown(true);
    }

    @Override
    public void onLoaderReset(Loader<List<CarInfo>> loader) {
        mAdapter.clear();
    }
}

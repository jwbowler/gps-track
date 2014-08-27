package edu.mit.zbt.rushgps.app;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class ActiveDriversListFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<List<ActiveDriver>> {

    private static String TAG = "ActiveDriversListFragment";

    private ArrayAdapter mAdapter;
    private List<ActiveDriver> mActiveDriverList;

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new ArrayAdapter<ActiveDriver>(getActivity(), android.R.layout.simple_list_item_1);
        setListAdapter(mAdapter);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ActiveDriver activeDriver = mActiveDriverList.get(position);
        activeDriver.setInPreferences(getActivity());

        getActivity().setResult(MainActivity.RESULT_CODE_SELECTED);
        getActivity().finish();
    }

    @Override
    public Loader<List<ActiveDriver>> onCreateLoader(int id, Bundle args) {
        return new ActiveDriversListLoader(getActivity());
    }

    @Override
    public void onLoadFinished(final Loader<List<ActiveDriver>> loader, List<ActiveDriver> data) {
        if (data.size() == 0) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            final int message = R.string.alert_retry_get_active_drivers;
            final int loaderId = loader.getId();

            final String negativeMessage;
            if (getActivity().getIntent().getExtras().getBoolean("selectionNecessary", false)) {
                negativeMessage = "Quit";
            } else {
                negativeMessage = "Keep current";
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
                                                               DebugConsoleActivity.class);
                                    getActivity().startActivity(intent);
                                }
                            })
                    .setNegativeButton(negativeMessage,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface d, int id) {
                                    getActivity().setResult(
                                            MainActivity.RESULT_CODE_NOT_SELECTED);
                                    getActivity().finish();
                                    d.cancel();
                                }
                            }
                    );
            builder.create().show();

        } else {
            mAdapter.clear();
            mAdapter.addAll(data);
            mActiveDriverList = data;

            getActivity().setProgressBarIndeterminateVisibility(false);
            setListShown(true);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<ActiveDriver>> loader) {
        mAdapter.clear();
    }
}

package edu.mit.zbt.rushgps.app;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import org.apache.http.HttpException;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ActiveDriversListLoader extends AsyncTaskLoader<List<ActiveDriver>> {
    private static String TAG = "ActiveDriversListLoader";

    public ActiveDriversListLoader(Context context) {
        super(context);
    }

    @Override
    public List<ActiveDriver> loadInBackground() {
        List<ActiveDriver> list;

        try {
            list = RestClient.getActiveDriversList(getContext());
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
            list = new ArrayList<ActiveDriver>();
        } catch (HttpException e) {
            list = new ArrayList<ActiveDriver>();
        }

        return list;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

}
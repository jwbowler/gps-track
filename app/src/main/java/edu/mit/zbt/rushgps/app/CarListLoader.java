package edu.mit.zbt.rushgps.app;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import org.apache.http.HttpException;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class CarListLoader extends AsyncTaskLoader<List<CarInfo>> {
    private static String TAG = "AppListLoader";

    public CarListLoader(Context context) {
        super(context);
    }

    @Override
    public List<CarInfo> loadInBackground() {
        List<CarInfo> list;
        try {
            list = RestClient.getCarsList();
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
            list = new ArrayList<CarInfo>();
        } catch (HttpException e) {
            list = new ArrayList<CarInfo>();
        }
        return list;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

}
package edu.mit.zbt.rushgps.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.turbomanage.httpclient.AsyncCallback;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.android.AndroidHttpClient;

import org.apache.http.HttpException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RestClient {
    private static final String TAG = "RestClient";

    private static AndroidHttpClient httpClient = null;
    private static String baseUrl = null;

    private static String getActiveDriversListEndpoint(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString("pref_active_drivers_list_endpoint", "");
    }

    private static String getGpsEndpoint(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return String.format(prefs.getString("pref_gps_endpoint_pattern", ""),
                prefs.getString("pref_active_driver_id", ""));
    }

    public static void updateBaseUrl(Context context) {
        String newBaseUrl = PreferenceManager.getDefaultSharedPreferences(context)
                            .getString("pref_base_url", "");
        if ((httpClient == null) || !newBaseUrl.equals(baseUrl)) {
            httpClient = new AndroidHttpClient(baseUrl);
        }
    }

    public static void postGps(Location location, final Context context) {
        JSONObject json = new JSONObject();

        if (httpClient == null) {
            updateBaseUrl(context);
        }

        try {
            json.put("latitude", location.getLatitude());
            json.put("longitude", location.getLongitude());
            json.put("accuracy", location.getAccuracy());
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }

        byte[] jsonStr = json.toString().getBytes();

        httpClient.post(getGpsEndpoint(context), "application/json", jsonStr, new AsyncCallback() {
            @Override
            public void onComplete(HttpResponse httpResponse) {
                Log.d(TAG, httpResponse.getBodyAsString());
                Log.d(TAG, "Complete");
            }

            @Override
            public void onError(Exception e) {
                String message = "Failed to POST location data: " + e.getMessage();
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    public static List<ActiveDriver> getActiveDriversList(Context context) throws JSONException, HttpException {
        JSONArray activeDrivers = getActiveDriversJson(context);
        List<ActiveDriver> list = new ArrayList<ActiveDriver>();

        for (int i = 0; i < activeDrivers.length(); i++) {
            list.add(new ActiveDriver(
                    activeDrivers.getJSONObject(i).getString("_id"),
                    activeDrivers.getJSONObject(i).getString("driverId"),
                    activeDrivers.getJSONObject(i).getJSONObject("driverContent").getString("name"),
                    activeDrivers.getJSONObject(i).getJSONObject("carContent").getString("name")
             ));
        }
        return list;
    }

    private static JSONArray getActiveDriversJson(Context context) throws JSONException, HttpException {
        Log.d(TAG, "activeDriversListEndpoint = " + getActiveDriversListEndpoint(context));
        if (httpClient == null) {
            updateBaseUrl(context);
        }
        HttpResponse response = httpClient.get(getActiveDriversListEndpoint(context), null);
        if (response == null) {
            throw new HttpException();
        }
        System.out.println(response.getBodyAsString());
        return new JSONArray(response.getBodyAsString());
    }

}

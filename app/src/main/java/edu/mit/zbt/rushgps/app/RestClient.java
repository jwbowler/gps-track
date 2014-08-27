package edu.mit.zbt.rushgps.app;

import android.content.Context;
import android.location.Location;
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
    private static String activeDriversListEndpoint = null;
    private static String gpsEndpoint = null;

    public static void setBaseUrl(String baseUrl) {
        httpClient = new AndroidHttpClient(baseUrl);
    }

    public static void setActiveDriversListEndpoint(String newCarListEndpoint) {
        activeDriversListEndpoint = newCarListEndpoint;
    }

    public static void setGpsEndpoint(String newGpsEndpoint) {
        gpsEndpoint = newGpsEndpoint;
    }

    public static void postGps(Location location, final Context context) {
        JSONObject json = new JSONObject();

        try {
            json.put("latitude", location.getLatitude());
            json.put("longitude", location.getLongitude());
            json.put("accuracy", location.getAccuracy());
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }

        byte[] jsonStr = json.toString().getBytes();

        httpClient.post(gpsEndpoint, "application/json", jsonStr, new AsyncCallback() {
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

    public static List<ActiveDriver> getActiveDriversList() throws JSONException, HttpException {
        JSONArray activeDrivers = getActiveDriversJson();
        List<ActiveDriver> list = new ArrayList<ActiveDriver>();

        for (int i = 0; i < activeDrivers.length(); i++) {
            list.add(new ActiveDriver(
                    activeDrivers.getJSONObject(i).getString("_id"),
                    activeDrivers.getJSONObject(i).getString("driverId"),
                    activeDrivers.getJSONObject(i).getJSONObject("driverContent").getString("name"),
                    activeDrivers.getJSONObject(i).getJSONObject("carContent").getString("name"),
                    activeDrivers.getJSONObject(i).getString("instruction")));
        }
        return list;
    }

    private static JSONArray getActiveDriversJson() throws JSONException, HttpException {
        Log.d(TAG, "activeDriversListEndpoint = " + activeDriversListEndpoint);
        HttpResponse response = httpClient.get(activeDriversListEndpoint, null);
        if (response == null) {
            throw new HttpException();
        }
        System.out.println(response.getBodyAsString());
        return new JSONArray(response.getBodyAsString());
    }

}

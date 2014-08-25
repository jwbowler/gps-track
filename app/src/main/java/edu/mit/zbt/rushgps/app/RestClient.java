package edu.mit.zbt.rushgps.app;

import android.location.Location;
import android.util.Log;

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
    private static String carListEndpoint = null;
    private static String gpsEndpoint = null;

    public static void setBaseUrl(String baseUrl) {
        httpClient = new AndroidHttpClient(baseUrl);
    }

    public static void setCarListEndpoint(String newCarListEndpoint) {
        carListEndpoint = newCarListEndpoint;
    }

    public static void setGpsEndpoint(String newGpsEndpoint) {
        gpsEndpoint = newGpsEndpoint;
    }

    public static boolean isGpsEndpointSet() {
        return gpsEndpoint != null && gpsEndpoint != "";
    }

    public static void postGps(Location location) {
        JSONObject json = new JSONObject();

        try {
            json.put("lat", location.getLatitude());
            json.put("long", location.getLongitude());
            json.put("accuracy", location.getAccuracy());
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        System.out.println(json.toString());
        byte[] jsonStr = json.toString().getBytes();

        httpClient.post(gpsEndpoint, "application/json", jsonStr, new AsyncCallback() {
            @Override
            public void onComplete(HttpResponse httpResponse) {
                System.out.println(httpResponse.getBodyAsString());
                System.out.println("Complete");
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static List<CarInfo> getCarsList() throws JSONException, HttpException {
//        return getMockCarsList();

        JSONArray cars = getCarsJson();
        List<CarInfo> list = new ArrayList<CarInfo>();

        for (int i = 0; i < cars.length(); i++) {
            list.add(new CarInfo(
                    cars.getJSONObject(i).getString("_id"),
                    cars.getJSONObject(i).getString("description")));
        }
        return list;
    }

//    public static List<CarInfo> getMockCarsList() {
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            // rofl
//        }
//        List<CarInfo> list = new ArrayList<CarInfo>();
//        list.add(new CarInfo("123", "The van"));
//        list.add(new CarInfo("456", "Charles's car"));
//        list.add(new CarInfo("789", "Kyle's car"));
//
//        return list;
//    }

    private static JSONArray getCarsJson() throws JSONException, HttpException {
        HttpResponse response = httpClient.get(carListEndpoint, null);
        if (response == null) {
            throw new HttpException();
        }
        System.out.println(response.getBodyAsString());
        return new JSONArray(response.getBodyAsString());
    }

}

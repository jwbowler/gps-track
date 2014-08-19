package edu.mit.zbt.rushgps.app;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.turbomanage.httpclient.AsyncCallback;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.android.AndroidHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RestClient {
    private static final String TAG = "RestClient";

    private static AndroidHttpClient httpClient = null;

    public static void setBaseUrl(String baseUrl) {
        httpClient = new AndroidHttpClient(baseUrl);
    }

    public static void postGps(Location location) {
        JSONObject json = new JSONObject();

        try {
            json.put("lat", location.getLatitude());
            json.put("long", location.getLongitude());
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        System.out.println(json.toString());
        byte[] jsonStr = json.toString().getBytes();

        httpClient.post("/cars/carID/location.php", "application/json", jsonStr, new AsyncCallback() {
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

    public class GetCarsAsync extends AsyncTask<Void, Void, List<CarInfo>> {
        private final String TAG = "GetCarsAsync";

        @Override
        protected List<CarInfo> doInBackground(Void... params) {
            try {
                return getCarsList();
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
                return null;
            }
        }
    }

    private static List<CarInfo> getCarsList() throws JSONException {
        JSONArray cars = getCarsJson();
        List<CarInfo> list = new ArrayList<CarInfo>();

        for (int i = 0; i < cars.length(); i++) {
            list.add(new CarInfo(
                    cars.getJSONObject(i).getString("_id"),
                    cars.getJSONObject(i).getString("description")));
        }

        return list;
    }

    private static JSONArray getCarsJson() throws JSONException {
        HttpResponse response = httpClient.get("/cars/json", null);
        return new JSONArray(response.getBodyAsString());
    }

}

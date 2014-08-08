package edu.mit.zbt.rushgps.app;

import android.util.Log;

import com.turbomanage.httpclient.AsyncCallback;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.android.AndroidHttpClient;

import org.json.JSONException;
import org.json.JSONObject;

public class RestClient {
    private static final String TAG = "RestClient";

    private static final String BASE_URL = "https://cliu2014.scripts.mit.edu";
    private static AndroidHttpClient httpClient = new AndroidHttpClient(BASE_URL);

    public static void postGps(double latitude, double longitude) {
        JSONObject json = new JSONObject();
        try {
            json.put("car_id", 123);
            json.put("lat", latitude);
            json.put("long", longitude);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        System.out.println(json.toString());
        byte[] jsonStr = json.toString().getBytes();

        httpClient.post("/rushGPSEndpoint.php", "application/json", jsonStr, new AsyncCallback() {
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
}

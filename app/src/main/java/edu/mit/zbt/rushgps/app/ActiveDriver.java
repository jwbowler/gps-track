package edu.mit.zbt.rushgps.app;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ActiveDriver {
    private final String id;
    private final String driverId;
    private final String driverName;
    private final String carName;

    public ActiveDriver(String id, String driverId, String driverName, String carName) {
        this.id = id;
        this.driverId = driverId;
        this.driverName = driverName;
        this.carName = carName;
    }

    public String getId() {
        return id;
    }
    public String getDriverId() { return driverId; }
    public String getDriverName() {
        return driverName;
    }
    public String getCarName() {
        return carName;
    }
    public String toString() {
        return driverName + " driving " + carName;
    }

    public void setInPreferences(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString("pref_active_driver_id", id)
                .putString("pref_driver_id", driverId)
                .putString("pref_driver_name", driverName)
                .putString("pref_car_name", carName)
                .commit();
    }

    public static void resetPreferences(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString("pref_active_driver_id", "")
                .putString("pref_driver_id", "")
                .putString("pref_driver_name", "")
                .putString("pref_car_name", "")
                .commit();
    }

    public static boolean isSetInPreferences(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return !prefs.getString("pref_active_driver_id", "").equals("")
                && !prefs.getString("pref_driver_id", "").equals("")
                && !prefs.getString("pref_driver_name", "").equals("")
                && !prefs.getString("pref_car_name", "").equals("");
    }
}

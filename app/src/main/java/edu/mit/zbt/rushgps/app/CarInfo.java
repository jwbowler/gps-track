package edu.mit.zbt.rushgps.app;


import android.os.Parcel;
import android.os.Parcelable;

public class CarInfo implements Parcelable {
    private final String id;
    private final String description;

    private static CarInfo currentCar = null;

    public CarInfo(String id, String description) {
        this.id = id;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public static CarInfo getCurrentCar() {
        return currentCar;
    }

    public static void setCurrentCar(CarInfo newCar) {
        currentCar = newCar;
    }

    @Override
    public String toString() {
        return description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(description);
    }

    public static final Creator<CarInfo> CREATOR = new Creator<CarInfo>() {
        @Override
        public CarInfo createFromParcel(Parcel parcel) {
            return new CarInfo(parcel);
        }

        @Override
        public CarInfo[] newArray(int size) {
            return new CarInfo[size];
        }
    };

    private CarInfo(Parcel parcel) {
        id = parcel.readString();
        description = parcel.readString();
    }
}
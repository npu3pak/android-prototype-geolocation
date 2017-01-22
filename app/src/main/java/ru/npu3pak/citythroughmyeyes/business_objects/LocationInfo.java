package ru.npu3pak.citythroughmyeyes.business_objects;

import android.location.Address;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class LocationInfo implements Parcelable {
    public Address address;
    public Date timestamp;

    public LocationInfo() {
    }

    //region Реализация Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(address, flags);
        dest.writeSerializable(timestamp);
    }

    public static final Parcelable.Creator<LocationInfo> CREATOR = new Parcelable.Creator<LocationInfo>() {
        public LocationInfo createFromParcel(Parcel in) {
            return new LocationInfo(in);
        }

        public LocationInfo[] newArray(int size) {
            return new LocationInfo[size];
        }
    };

    protected LocationInfo(Parcel in) {
        address = in.readParcelable(Address.class.getClassLoader());
        timestamp = (Date) in.readSerializable();
    }
    //endregion
}

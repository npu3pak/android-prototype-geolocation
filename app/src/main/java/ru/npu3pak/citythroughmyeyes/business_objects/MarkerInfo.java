package ru.npu3pak.citythroughmyeyes.business_objects;

import android.os.Parcel;
import android.os.Parcelable;

public class MarkerInfo extends LocationInfo {
    public long id = -1;
    public MarkerType type = MarkerType.SIMPLE;
    public int color = MarkerColor.INDIGO.getIntValue();
    public String comment;

    public MarkerInfo() {
        super();
    }

    //region Реализация Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(color);
        dest.writeString(comment);
        dest.writeSerializable(type);
    }

    public static final Parcelable.Creator<MarkerInfo> CREATOR = new Parcelable.Creator<MarkerInfo>() {
        public MarkerInfo createFromParcel(Parcel in) {
            return new MarkerInfo(in);
        }

        public MarkerInfo[] newArray(int size) {
            return new MarkerInfo[size];
        }
    };

    private MarkerInfo(Parcel in) {
        super(in);
        color = in.readInt();
        comment = in.readString();
        type = (MarkerType) in.readSerializable();
    }
    //endregion
}

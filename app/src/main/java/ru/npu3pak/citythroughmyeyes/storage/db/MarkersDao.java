package ru.npu3pak.citythroughmyeyes.storage.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ru.npu3pak.citythroughmyeyes.business_objects.MarkerInfo;
import ru.npu3pak.citythroughmyeyes.business_objects.MarkerType;

import static ru.npu3pak.citythroughmyeyes.storage.db.MarkersSchema.Field.*;

public class MarkersDao {
    private Context context;

    public MarkersDao(Context context) {
        this.context = context;
    }

    public void addMarker(MarkerInfo marker) {
        SQLiteDatabase db = MarkersDbHelper.getInstance(context).getWritableDatabase();
        ContentValues values = getContentValues(marker);
        marker.id = db.insert(MarkersSchema.MARKERS_TABLE_NAME, null, values);
        db.close();
    }

    @NonNull
    private ContentValues getContentValues(MarkerInfo marker) {
        ContentValues values = new ContentValues();
        values.put(MARKER_TYPE.toString(), marker.type.toString());
        values.put(MARKER_COLOR.toString(), marker.color);
        values.put(MARKER_COMMENT.toString(), marker.comment);
        values.put(MARKER_TIMESTAMP_MILLIS.toString(), marker.timestamp.getTime());
        values.put(ADMIN_AREA.toString(), marker.address.getAdminArea());
        values.put(SUB_ADMIN_AREA.toString(), marker.address.getSubAdminArea());
        values.put(LOCALITY.toString(), marker.address.getLocality());
        values.put(SUB_LOCALITY.toString(), marker.address.getSubLocality());
        values.put(THOROUGHFARE.toString(), marker.address.getThoroughfare());
        values.put(SUB_THOROUGHFARE.toString(), marker.address.getSubThoroughfare());
        values.put(PREMISES.toString(), marker.address.getPremises());
        values.put(COUNTRY_NAME.toString(), marker.address.getCountryName());
        values.put(LATITUDE.toString(), marker.address.getLatitude());
        values.put(LONGITUDE.toString(), marker.address.getLongitude());
        return values;
    }

    public void deleteMarker(MarkerInfo marker) {
        SQLiteDatabase db = MarkersDbHelper.getInstance(context).getWritableDatabase();
        String clause = _ID + " = ?";
        String[] args = {String.valueOf(marker.id)};
        db.delete(MarkersSchema.MARKERS_TABLE_NAME, clause, args);
        db.close();
    }

    public void updateMarker(MarkerInfo marker) {
        SQLiteDatabase db = MarkersDbHelper.getInstance(context).getWritableDatabase();
        ContentValues values = getContentValues(marker);
        String clause = _ID + " = ?";
        String[] args = {String.valueOf(marker.id)};
        db.update(MarkersSchema.MARKERS_TABLE_NAME, values, clause, args);
        db.close();
    }

    public List<MarkerInfo> getAllMarkers() {
        String[] projection = {
                _ID,
                MARKER_TYPE.toString(),
                MARKER_COLOR.toString(),
                MARKER_COMMENT.toString(),
                MARKER_TIMESTAMP_MILLIS.toString(),
                ADMIN_AREA.toString(),
                SUB_ADMIN_AREA.toString(),
                LOCALITY.toString(),
                SUB_LOCALITY.toString(),
                THOROUGHFARE.toString(),
                SUB_THOROUGHFARE.toString(),
                PREMISES.toString(),
                COUNTRY_NAME.toString(),
                LATITUDE.toString(),
                LONGITUDE.toString()
        };
        String sortOrder = MARKER_TIMESTAMP_MILLIS + " DESC";

        SQLiteDatabase db = MarkersDbHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.query(MarkersSchema.MARKERS_TABLE_NAME, projection, null, null, null, null, sortOrder);

        List<MarkerInfo> markers = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            MarkerInfo marker = new MarkerInfo();
            marker.id = cursor.getLong(cursor.getColumnIndex(_ID));
            marker.type = MarkerType.valueOf(cursor.getString(cursor.getColumnIndex(MARKER_TYPE.toString())));
            marker.color = cursor.getInt(cursor.getColumnIndex(MARKER_COLOR.toString()));
            marker.comment = cursor.getString(cursor.getColumnIndex(MARKER_COMMENT.toString()));
            marker.timestamp = new Date(cursor.getLong(cursor.getColumnIndex(MARKER_TIMESTAMP_MILLIS.toString())));
            marker.address = new Address(Locale.getDefault());
            marker.address.setAdminArea(cursor.getString(cursor.getColumnIndex(ADMIN_AREA.toString())));
            marker.address.setSubAdminArea(cursor.getString(cursor.getColumnIndex(SUB_ADMIN_AREA.toString())));
            marker.address.setLocality(cursor.getString(cursor.getColumnIndex(LOCALITY.toString())));
            marker.address.setSubLocality(cursor.getString(cursor.getColumnIndex(SUB_LOCALITY.toString())));
            marker.address.setThoroughfare(cursor.getString(cursor.getColumnIndex(THOROUGHFARE.toString())));
            marker.address.setSubThoroughfare(cursor.getString(cursor.getColumnIndex(SUB_THOROUGHFARE.toString())));
            marker.address.setPremises(cursor.getString(cursor.getColumnIndex(PREMISES.toString())));
            marker.address.setCountryName(cursor.getString(cursor.getColumnIndex(COUNTRY_NAME.toString())));
            marker.address.setLatitude(cursor.getDouble(cursor.getColumnIndex(LATITUDE.toString())));
            marker.address.setLongitude(cursor.getDouble(cursor.getColumnIndex(LONGITUDE.toString())));
            markers.add(marker);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return markers;
    }
}

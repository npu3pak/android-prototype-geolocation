package ru.npu3pak.citythroughmyeyes.storage.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MarkersDbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "UserMarkers.db";
    private static final int DB_SCHEMA_VERSION = 1;
    private static final String SQL_CREATE_DATABASE =
            "CREATE TABLE "
                    + MarkersSchema.MARKERS_TABLE_NAME
                    + "("
                    + MarkersSchema.Field._ID + " INTEGER PRIMARY KEY,"
                    + MarkersSchema.getCommaSeparatedDescriptors()
                    + ");";

    private static MarkersDbHelper instance;

    public static MarkersDbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new MarkersDbHelper(context.getApplicationContext());
        }
        return instance;
    }

    private MarkersDbHelper(Context context) {
        super(context, DB_NAME, null, DB_SCHEMA_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_DATABASE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Пока не требуется
    }
}

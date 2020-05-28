package ru.ifmo.practice.map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SQLMapDatabase extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Map";
    private static final String MARKERS_TABLE_NAME = "Markers";
    private static final String MARKERS_COLUMN_LATITUDE = "Latitude";
    private static final String MARKERS_COLUMN_LONGITUDE = "Longitude";
    private static final String MARKERS_COLUMN_DESCRIPTION = "Description";


    public SQLMapDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + MARKERS_TABLE_NAME + " (" +
                "id SERIAL PRIMARY KEY, " +
                MARKERS_COLUMN_LATITUDE + " REAL, " +
                MARKERS_COLUMN_LONGITUDE + " REAL, " +
                MARKERS_COLUMN_DESCRIPTION +  " TEXT" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MARKERS_TABLE_NAME);
        onCreate(db);
    }

    public Map<LatLng, String> loadMarkers() {
        SQLiteDatabase database = getReadableDatabase();

        String[] projection = {
                MARKERS_COLUMN_LATITUDE,
                MARKERS_COLUMN_LONGITUDE,
                MARKERS_COLUMN_DESCRIPTION
        };

        try (Cursor cursor = database.query(
                MARKERS_TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null)) {
            Map<LatLng, String> markers = new HashMap<>();

            while (cursor.moveToNext()) {
                markers.put(
                        new LatLng(cursor.getDouble(0), cursor.getDouble(1)),
                        cursor.getString(2));
            }

            return markers;
        }
    }

    public void saveMarkers(Map<LatLng, String> markers) {
        SQLiteDatabase database = getWritableDatabase();

        database.delete(MARKERS_TABLE_NAME, null, null);

        if (markers.isEmpty()) {
            return;
        }

        StringBuilder query = new StringBuilder("INSERT INTO " + MARKERS_TABLE_NAME + " (" +
                MARKERS_COLUMN_LATITUDE + ", " +
                MARKERS_COLUMN_LONGITUDE + ", " +
                MARKERS_COLUMN_DESCRIPTION + ")\n" +
                "VALUES\n");
        Iterator<Map.Entry<LatLng, String>> iterator = markers.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<LatLng, String> marker = iterator.next();
            query.append("(").append(marker.getKey().latitude).append(", ")
                    .append(marker.getKey().longitude).append(", '")
                    .append(marker.getValue()).append("')");
            if (iterator.hasNext()) {
                query.append(",\n");
            } else {
                query.append(";");
            }
        }

        database.execSQL(query.toString());
    }
}

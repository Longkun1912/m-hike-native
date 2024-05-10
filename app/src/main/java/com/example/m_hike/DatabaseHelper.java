package com.example.m_hike;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.m_hike.model.Hike;
import com.example.m_hike.model.Observation;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "hikes.db";
    private static final int DATABASE_VERSION = 1;

    // Hike entity and attributes
    private static final String TABLE_HIKES = "hikes";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    private static final String COLUMN_LOCATION = "location";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_PARKING = "parking";
    private static final String COLUMN_LENGTH = "length";
    private static final String COLUMN_DIFFICULTY = "difficulty";
    private static final String COLUMN_DESCRIPTION = "description";

    // Observation entity and attributes
    private static final String TABLE_OBSERVATIONS = "observations";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_COMMENT = "comment";
    private static final String COLUMN_HIKE_ID = "hike_id";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create table "hikes"
            String hikeTableQuery = "CREATE TABLE " + TABLE_HIKES + " ("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_NAME + " TEXT,"
                + COLUMN_LOCATION + " TEXT,"
                + COLUMN_DATE + " TEXT,"
                + COLUMN_PARKING + " TEXT,"
                + COLUMN_LENGTH + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_DIFFICULTY + " TEXT)";
        sqLiteDatabase.execSQL(hikeTableQuery);

        String observationHikeForeignKeyQuery = "FOREIGN KEY(" + COLUMN_HIKE_ID + ") REFERENCES " + TABLE_HIKES + "(" + COLUMN_ID + ")";

        // Create table "observations"
        String observationTableQuery = "CREATE TABLE " + TABLE_OBSERVATIONS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TITLE+ " TEXT,"
                + COLUMN_HIKE_ID + " INTEGER, "
                + COLUMN_TIME + " TEXT,"
                + COLUMN_COMMENT + " TEXT,"
                + observationHikeForeignKeyQuery + ")";
        sqLiteDatabase.execSQL(observationTableQuery);
    }

    // Assuming you have a method in your DatabaseHelper class to delete all data
    public void deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();

        // Delete all records from the "observations" table
        db.delete(TABLE_OBSERVATIONS, null, null);

        // Delete all records from the "hikes" table
        db.delete(TABLE_HIKES, null, null);

        // Close the database connection
        db.close();
    }


    public void addNewHike(Hike hike){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, hike.getName());
        values.put(COLUMN_LOCATION, hike.getLocation());
        values.put(COLUMN_DATE, hike.getDate());
        values.put(COLUMN_PARKING, hike.getParking());
        values.put(COLUMN_LENGTH, hike.getLength());
        values.put(COLUMN_DESCRIPTION, hike.getDescription());
        values.put(COLUMN_DIFFICULTY, hike.getDifficulty());

        database.insert(TABLE_HIKES, null, values);
        database.close();
    }

    @SuppressLint("Range")
    public Hike getHikeById(int hikeId) {
        SQLiteDatabase database = this.getReadableDatabase();
        Hike hike = null;

        String selectQuery = "SELECT * FROM " + TABLE_HIKES + " WHERE " + COLUMN_ID + " = ?";
        Cursor cursor = database.rawQuery(selectQuery, new String[]{String.valueOf(hikeId)});

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
            String location = cursor.getString(cursor.getColumnIndex(COLUMN_LOCATION));
            String date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE));
            String parking = cursor.getString(cursor.getColumnIndex(COLUMN_PARKING));
            String length = cursor.getString(cursor.getColumnIndex(COLUMN_LENGTH));
            String difficulty = cursor.getString(cursor.getColumnIndex(COLUMN_DIFFICULTY));
            String description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));

            hike = new Hike(id, name, location, date, parking, length, difficulty,description);
            cursor.close();
        }
        return hike;
    }

    @SuppressLint("Range")
    public Observation getObservationById(int observationId){
        SQLiteDatabase database = this.getReadableDatabase();
        Observation observation = null;

        String selectQuery = "SELECT * FROM " + TABLE_OBSERVATIONS + " WHERE " + COLUMN_ID + " = ?";
        Cursor cursor = database.rawQuery(selectQuery, new String[]{String.valueOf(observationId)});

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
            String time = cursor.getString(cursor.getColumnIndex(COLUMN_TIME));
            String comment = cursor.getString(cursor.getColumnIndex(COLUMN_COMMENT));

            observation = new Observation(id, title, time, comment);
            cursor.close();
        }
        database.close();
        return observation;
    }


    @SuppressLint("Range")
    public List<Hike> getHikes(String hike_name) {
        List<Hike> hikeList = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_HIKES;

        if (hike_name != null && !hike_name.isEmpty()) {
            selectQuery += " WHERE " + COLUMN_NAME + " LIKE '%" + hike_name + "%'";
        }

        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                String location = cursor.getString(cursor.getColumnIndex(COLUMN_LOCATION));
                String date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE));
                String parking = cursor.getString(cursor.getColumnIndex(COLUMN_PARKING));
                String length = cursor.getString(cursor.getColumnIndex(COLUMN_LENGTH));
                String difficulty = cursor.getString(cursor.getColumnIndex(COLUMN_DIFFICULTY));
                String description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));

                Hike hike = new Hike(id, name, location, date, parking, length, difficulty, description);
                hikeList.add(hike);
            } while (cursor.moveToNext());

            cursor.close();
        }
        database.close();
        return hikeList;
    }

    public void updateHike(int hikeId, Hike newHikeData) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, newHikeData.getName());
        values.put(COLUMN_LOCATION, newHikeData.getLocation());
        values.put(COLUMN_DATE, newHikeData.getDate());
        values.put(COLUMN_PARKING, newHikeData.getParking());
        values.put(COLUMN_LENGTH, newHikeData.getLength());
        values.put(COLUMN_DIFFICULTY, newHikeData.getDifficulty());
        values.put(COLUMN_DESCRIPTION, newHikeData.getDescription());

        // Define the WHERE clause to update the row with the specific ID
        String whereClause = COLUMN_ID + " = ?";

        // Define the value for the WHERE clause as the hikeId
        String[] whereArgs = {String.valueOf(hikeId)};

        // Perform the update operation
        database.update(TABLE_HIKES, values, whereClause, whereArgs);

        database.close();
    }

    public void updateObservation(int observationId, Observation newObservationData){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TITLE, newObservationData.getTitle());
        values.put(COLUMN_TIME, newObservationData.getTime());
        values.put(COLUMN_COMMENT, newObservationData.getComment());

        String whereClause = COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(observationId)};

        // Perform the update operation
        database.update(TABLE_OBSERVATIONS, values, whereClause, whereArgs);

        database.close();
    }


    public void deleteHike(int hikeId){
        SQLiteDatabase database = getWritableDatabase();

        // Define the WHERE clause to delete the row with a specific ID
        String whereClause = COLUMN_ID + " = ?";

        // Define the value for the WHERE clause as the hikeId
        String[] whereArgs = {String.valueOf(hikeId)};

        // Perform the delete operation
        database.delete(TABLE_HIKES, whereClause, whereArgs);

        database.close();
    }

    public void removeObservation(int observationId){
        SQLiteDatabase database = getWritableDatabase();

        // Define the WHERE clause to delete the row with a specific ID
        String whereClause = COLUMN_ID + " = ?";

        // Define the value for the WHERE clause as the hikeId
        String[] whereArgs = {String.valueOf(observationId)};

        // Perform the delete operation
        database.delete(TABLE_OBSERVATIONS, whereClause, whereArgs);

        database.close();
    }

    @SuppressLint("Range")
    public List<Observation> getObservationsForHike(int hikeId) {
        List<Observation> observations = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();

        System.out.println("Hike: " + getHikeById(hikeId).getName());

        String selectQuery = "SELECT * FROM " + TABLE_OBSERVATIONS + " WHERE " + COLUMN_HIKE_ID + " = ?";
        Cursor cursor = database.rawQuery(selectQuery, new String[]{String.valueOf(hikeId)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int observationId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
                String time = cursor.getString(cursor.getColumnIndex(COLUMN_TIME));
                String comment = cursor.getString(cursor.getColumnIndex(COLUMN_COMMENT));

                Observation observation = new Observation(observationId, title, time, comment);
                observations.add(observation);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return observations;
    }

    public void addNewObservation(Observation observation, int hikeId){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TITLE, observation.getTitle());
        values.put(COLUMN_TIME, observation.getTime());
        values.put(COLUMN_COMMENT, observation.getComment());
        values.put(COLUMN_HIKE_ID, hikeId);

        database.insert(TABLE_OBSERVATIONS, null, values);
        database.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HIKES);
        onCreate(db);
    }
}

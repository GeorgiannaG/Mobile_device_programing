package com.ubb.georgigalatanu.residences;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "db_residences";
    private static final String TABLE_RESIDENCES = "residences";
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MEMOS_TABLE = "CREATE TABLE " + TABLE_RESIDENCES + "("
        + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TITLE + " TEXT,"
        + KEY_DESCRIPTION + " TEXT" + ")";
        db.execSQL(CREATE_MEMOS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESIDENCES);
        onCreate(db);
    }

    public void addResidence(Residence residence) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, residence.getTitle());
        values.put(KEY_DESCRIPTION, residence.getDescription());
        db.insert(TABLE_RESIDENCES, null, values);
        db.close();
    }

    public Residence getResidence(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RESIDENCES, new String[] { KEY_ID,
                        KEY_TITLE, KEY_DESCRIPTION}, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Residence residence = new Residence(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2));
        return residence;
    }

    public List<Residence> getAllResidences() {
        List<Residence> residenceList = new ArrayList<Residence>();
        String selectQuery = "SELECT * FROM " + TABLE_RESIDENCES;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Residence residence = new Residence(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2));
                residenceList.add(residence);
            } while (cursor.moveToNext());
        }
        return residenceList;
    }

    public int updateResidence(Residence residence) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, residence.getTitle());
        values.put(KEY_DESCRIPTION, residence.getDescription());
        return db.update(TABLE_RESIDENCES, values, KEY_ID + " = ?",
                new String[]{String.valueOf(residence.getId())});
    }

    public void deleteShop(Residence residence) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RESIDENCES, KEY_ID + " = ?",
                new String[] { String.valueOf(residence.getId()) });
        db.close();
    }
}

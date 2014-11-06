package com.merqurius;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "BookManager.db";
    private static final int DATABASE_VERSION = 1;

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(Database.CREATE_BOOK);
        database.execSQL(Database.CREATE_COLLECTION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL(Database.DROP_BOOK);
        db.execSQL(Database.DROP_COLLECTION);
        onCreate(db);
    }

    public void insertCollection(String collectionName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Database.COLLECTION_NAME, collectionName);
        //cv.put(COLLECTION_ID, "");
        db.insertOrThrow(Database.COLLECTION_TBL, null, cv);
        db.close();
    }

    public Cursor selectAllCollections(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(Database.SELECT_ALL_COLLECTIONS, new String[]{});
    }

    public Cursor selectBookTitlesByCollection(String collectionName){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(Database.SELECT_BOOK_TITLES_FOR_COLLECTION,new String[]{collectionName});
    }
}

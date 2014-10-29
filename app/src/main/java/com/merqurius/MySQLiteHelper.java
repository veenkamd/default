package com.merqurius;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

    //Book Table
    public static final String BOOK_TBL    = "book";
    public static final String TITLE       = "title";
    public static final String AUTHOR      = "author";
    public static final String GENRE       = "genre";
    public static final String ISBN        = "isbn";
    public static final String DESCRIPTION = "description";
    public static final String STATUS      = "status";
    public static final String LOANED_TO   = "loaned_to";
    public static final String NUM_PAGES   = "num_pages";
    public static final String COLLECTION  = "collection";
    public static final String RATING      = "rating";

    //Collection Table
    public static final String COLLECTION_TBL = "collection";
    public static final String COLLECTION_NAME = "collection_name";
    public static final String COLLECTION_ID   = "collection_id";

    private static final String DATABASE_NAME = "BookManager.db";
    private static final int DATABASE_VERSION = 1;

    //Create SQL statements
    public static final String CREATE_BOOK = "create table " + BOOK_TBL + "("
            + ISBN + " varchar(13) primary key, "
            + STATUS + " varchar(20) not null, "
            + TITLE + " varchar(200) not null, "
            + GENRE + " varchar(50) not null, "
            + LOANED_TO + " varchar(50), "
            + NUM_PAGES + " integer, "
            + AUTHOR + " varchar(50) not null, "
            + COLLECTION + " integer not null, "
            + RATING + " integer, "
            + DESCRIPTION + " varchar(300));";

    public static final String CREATE_COLLECTION = "create table " + COLLECTION_TBL + "("
            + COLLECTION_ID + " integer primary key, "
            + COLLECTION_NAME + "varchar(25));";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_BOOK);
        database.execSQL(CREATE_COLLECTION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + BOOK_TBL);
        db.execSQL("DROP TABLE IF EXISTS " + COLLECTION_TBL);
        onCreate(db);
    }

    public void insertCollection(String collectionName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLLECTION_ID, "");
        cv.put(COLLECTION, collectionName);
        db.insertOrThrow(COLLECTION_TBL, null, cv);
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

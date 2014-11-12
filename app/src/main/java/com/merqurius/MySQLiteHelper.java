package com.merqurius;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.merqurius.test.Book;

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

    public Cursor selectLoanTo(String isbn) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(Database.SELECT_LOANED_TO_BY_ISBN,new String[]{isbn});
    }

    public void insertBook(String collectionName, Book book){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Database.TITLE, book.getTitle());
        cv.put(Database.ISBN, book.getIsbn());
        cv.put(Database.AUTHOR, book.getAuthor());
        cv.put(Database.DESCRIPTION, book.getDescription());
        cv.put(Database.STATUS, book.getStatus());
        cv.put(Database.GENRE, book.getGenre());
        cv.put(Database.COLLECTION, book.getCollection());
        cv.put(Database.LOANED_TO, book.getLoaned_to());
        cv.put(Database.NUM_PAGES, book.getNumPages());
        cv.put(Database.RATING, book.getRating());
        db.insertOrThrow(Database.BOOK_TBL, null, cv);
        db.close();
    }

    public int moveBook(String collectionName, Book book){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Database.COLLECTION, book.getCollection());
        return db.update(Database.BOOK_TBL, cv, Database.WHERE_ISBN,
                new String []{book.getIsbn()});
    }
}

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

    public void renameCollection(String collectionName, String newCollectionName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Database.COLLECTION_NAME, newCollectionName);
        //cv.put(COLLECTION_ID, "");
        db.update(Database.COLLECTION_TBL, cv, Database.WHERE_COLLECTION_NAME,
                  new String[]{collectionName});
        db.close();
    }


    public Cursor selectAllCollections(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(Database.SELECT_ALL_COLLECTIONS, new String[]{});
    }

    public Cursor selectBookTitlesByCollection(String collectionName){
        SQLiteDatabase db = this.getReadableDatabase();
        Log.d("Select from", collectionName);
        return db.rawQuery(Database.SELECT_BOOK_TITLES_FOR_COLLECTION,new String[]{collectionName});
    }

    public Cursor selectBookFromSearch(String title, String author, String isbn){
        SQLiteDatabase db = this.getReadableDatabase();
        Log.d("Select from", title + " " + author + " " + isbn);
        return db.rawQuery(Database.SELECT_BOOK_FROM_SEARCH, new String[]{title, author, isbn});
    }

    public Cursor selectBookDetails(String title, String collectionName){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(Database.SELECT_BOOK_DETAILS, new String[]{title, collectionName});
    }

    public Cursor selectLoanTo(String isbn) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(Database.SELECT_LOANED_TO_BY_ISBN,new String[]{isbn});
    }

    public void insertBook(String collectionName, Book book){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Database.ISBN, book.getIsbn());
        cv.put(Database.STATUS, book.getStatus());
        cv.put(Database.TITLE, book.getTitle());
        cv.put(Database.GENRE, book.getGenre());
        cv.put(Database.LOANED_TO, book.getLoaned_to());
        cv.put(Database.NUM_PAGES, book.getNumPages());
        cv.put(Database.AUTHOR, book.getAuthor());
        cv.put(Database.PUBLISHED, book.getPublished());
        cv.put(Database.COLLECTION, book.getCollection());
        cv.put(Database.IMGURL, book.getImgURL());
        cv.put(Database.RATING, book.getRating());
        cv.put(Database.DESCRIPTION, book.getDescription());
        db.insertOrThrow(Database.BOOK_TBL, null, cv);

        db.close();
    }

    public int moveBook(String collectionName, Book book){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Database.COLLECTION, book.getCollection());
        Log.d("Moving " + book.getIsbn() + "to", book.getCollection());
        return db.update(Database.BOOK_TBL, cv, Database.WHERE_ISBN,
                new String []{book.getIsbn()});
    }

    public int loanBook(Book book){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Database.LOANED_TO, book.getLoaned_to());
        Log.d("Loaning " + book.getIsbn() + "to", book.getLoaned_to());
        return db.update(Database.BOOK_TBL, cv, Database.WHERE_ISBN, new String[]{book.getIsbn()});
    }

    public void deleteBook(String title, String collection){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Database.BOOK_TBL, Database.WHERE_TITLE + " AND " + Database.WHERE_COLLECTION,
                new String[]{title,collection});
        db.close();
    }

    public void deleteCollection(String collectionName){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Database.BOOK_TBL, Database.WHERE_COLLECTION, new String[]{collectionName});
        db.delete(Database.COLLECTION_TBL, Database.WHERE_COLLECTION_NAME, new String[]{collectionName});
        db.close();
    }
}

package com.merqurius;

public final class Database {
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
    public static final String IMGURL      = "imgurl";
    public static final String RATING      = "rating";

    public static final String WHERE_ISBN = "isbn = ?";

    //Collection Table Column Names
    public static final String COLLECTION_TBL = "collection";
    public static final String COLLECTION_NAME = "collection_name";
    public static final String COLLECTION_ID   = "collection_id";

    //Create Statements
    public static final String CREATE_BOOK = "create table " + BOOK_TBL + "("
            + ISBN + " varchar(13) primary key, "
            + STATUS + " varchar(20) not null, "
            + TITLE + " varchar(200) not null, "
            + GENRE + " varchar(50) not null, "
            + LOANED_TO + " varchar(50), "
            + NUM_PAGES + " integer, "
            + AUTHOR + " varchar(50) not null, "
            + COLLECTION + " integer not null, "
            + IMGURL + " varchar(200) not null, "
            + RATING + " integer, "
            + DESCRIPTION + " varchar(300));";
    public static final String CREATE_COLLECTION = "create table " + COLLECTION_TBL + "("
            + COLLECTION_ID + " integer primary key, "
            + COLLECTION_NAME + " varchar(25));";

    //Drop Statements
    public static final String DROP_BOOK = "DROP TABLE IF EXISTS " + BOOK_TBL;
    public static final String DROP_COLLECTION = "DROP TABLE IF EXISTS " + COLLECTION_TBL;

    //Queries
    public static final String SELECT_ALL_COLLECTIONS = "SELECT * FROM collection";
    public static final String SELECT_BOOK_TITLES_FOR_COLLECTION =
            "SELECT title FROM book WHERE collection = ?";
    public static final String SELECT_BOOK_DETAILS =
            "SELECT * FROM book WHERE title = ? AND collection = ?";
    public static final String SELECT_LOANED_TO_BY_ISBN =
            "SELECT loaned_to FROM book WHERE isbn = ?";
}

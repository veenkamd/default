package com.merqurius;

/**
 * Created by Dan on 19/10/2014.
 */
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
    public static final String RATING      = "rating";

    //Collection Table Column Names
    public static final String COLLECTION_TBL = "collection";
    public static final String COLLECTION_NAME = "collection_name";
    public static final String COLLECTION_ID   = "collection_id";

    //Queries
    public static final String SELECT_ALL_COLLECTIONS = "SELECT * FROM collection";
    public static final String SELECT_BOOK_TITLES_FOR_COLLECTION =
            "SELECT title FROM book WHERE collection = ?";
}

package com.merqurius.test;

public class Book {
    private String title;
    private String author;
    private String isbn;
    private String genre;
    private String description;
    private String status; //This shouldn't be a string, but I'm doing this temporarily for a skeleton
    private String loaned_to;
    private String collection;
    private int numPages;
    private int rating;

    public Book(String t, String a, String i, String g, String d, String s, String l, String c, int n, int r){
        title = t;
        author = a;
        isbn = i;
        genre = g;
        description = d;
        status = s;
        loaned_to = l;
        collection = c;
        numPages = n;
        rating = r;
    }

    public Book(String t, String a, String i){
        title = t;
        author = a;
        isbn = i;
        genre = "unknown";
        description = "unknown";
        status = "unknown";
        loaned_to = "unknown";
        collection = "unknown";
        numPages = 0;
        rating = 0;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLoaned_to() {
        return loaned_to;
    }

    public void setLoaned_to(String loaned_to) {
        this.loaned_to = loaned_to;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public int getNumPages() {
        return numPages;
    }

    public void setNumPages(int numPages) {
        this.numPages = numPages;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}

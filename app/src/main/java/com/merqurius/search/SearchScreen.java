package com.merqurius.search;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.merqurius.Database;
import com.merqurius.MySQLiteHelper;
import com.merqurius.R;
import com.merqurius.book.details.BookDetailsScreen;
import com.merqurius.collections.CollectionsScreen;
import com.merqurius.home.HomeScreen;
import com.merqurius.search.SearchResultsScreen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class SearchScreen extends Activity implements View.OnClickListener {

    Button search;
    EditText titlebox, authorbox, isbnbox;
    String title, author, isbn;
    Boolean online = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent SearchIntent = getIntent();
        online = SearchIntent.getBooleanExtra("mode", true);

        search = (Button) findViewById(R.id.buttonSearch);
        search.setOnClickListener(this);

        titlebox = (EditText) findViewById(R.id.title_text);
        titlebox.setTextColor(Color.WHITE);
        authorbox = (EditText) findViewById(R.id.author_text);
        authorbox.setTextColor(Color.WHITE);
        isbnbox = (EditText) findViewById(R.id.isbn_text);
        isbnbox.setTextColor(Color.WHITE);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.buttonSearch:
                title = titlebox.getText().toString();
                author = authorbox.getText().toString();
                isbn = isbnbox.getText().toString();

                if(online) {
                    String q = buildQuery(title, author, isbn);
                    String r = fetchResults(q);
                    if (!r.equals("Unable to Connect")) {
                        Intent searchResultsIntent = new Intent(v.getContext(), SearchResultsScreen.class);
                        searchResultsIntent.putExtra("query", q);
                        searchResultsIntent.putExtra("response", r);
                        Log.d(getClass().getName(), "Forwarding to results screen.");
                        startActivityForResult(searchResultsIntent, 0);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage(r);
                        builder.show();
                    }
                    break;
                }
                else {
                    queryDB(title, author, isbn, v);
                }
        }
    }

    private void queryDB(String t, String a, String i, View v){
        MySQLiteHelper db = new MySQLiteHelper(this);
        Cursor cursor = db.selectBookFromSearch( "%" + t + "%", "%" + a + "%", i);
        String[] authors, titles, isbns, pubdates, descs, imgurls, collections;
        authors = new String[cursor.getCount()];
        titles = new String[cursor.getCount()];
        isbns = new String[cursor.getCount()];
        pubdates = new String[cursor.getCount()];
        descs = new String[cursor.getCount()];
        imgurls = new String[cursor.getCount()];
        collections = new String[cursor.getCount()];

        if(cursor.moveToFirst())
        {
            int p = 0;
            do {
                Log.d("Processing book", "" + p);
                int columnIndex = cursor.getColumnIndex(Database.AUTHOR);
                authors[p] = cursor.getString(columnIndex);
                Log.d("Author", authors[p]);
                columnIndex = cursor.getColumnIndex(Database.TITLE);
                titles[p] = cursor.getString(columnIndex);
                Log.d("Title", titles[p]);
                columnIndex = cursor.getColumnIndex(Database.ISBN);
                isbns[p] = cursor.getString(columnIndex);
                columnIndex = cursor.getColumnIndex(Database.PUBLISHED);
                pubdates[p] = cursor.getString(columnIndex);
                columnIndex = cursor.getColumnIndex(Database.DESCRIPTION);
                descs[p] = cursor.getString(columnIndex);
                columnIndex = cursor.getColumnIndex(Database.IMGURL);
                imgurls[p] = cursor.getString(columnIndex);
                columnIndex = cursor.getColumnIndex(Database.COLLECTION);
                collections[p] = cursor.getString(columnIndex);
                p++;
            } while(cursor.moveToNext());


            Log.d(getClass().getName(), "Result received.");
            Intent searchResultsIntent = new Intent(v.getContext(), SearchResultsScreen.class);
            searchResultsIntent.putExtra("author", authors);
            searchResultsIntent.putExtra("title", titles);
            searchResultsIntent.putExtra("isbn", isbns);
            searchResultsIntent.putExtra("published", pubdates);
            searchResultsIntent.putExtra("description", descs);
            searchResultsIntent.putExtra("img", imgurls);
            searchResultsIntent.putExtra("collection", collections);
            searchResultsIntent.putExtra("mode", false);
            startActivityForResult(searchResultsIntent, 0);

        }
    }

    private String buildQuery(String t, String a, String i) {
        String query = "https://www.googleapis.com/books/v1/volumes?q=";
        boolean termsAdded = false;
        if(t.length() > 0){
            if(termsAdded) query += "+";
            else termsAdded = true;
            t = t.replaceAll(" ", ",");
            query += "intitle:" + t;
        }
        if(a.length() > 0){
            if(termsAdded) query += "+";
            else termsAdded = true;
            a = a.replaceAll(" ", ",");
            query += "inauthor:" + a;
        }
        if(i.length() > 0){
            if(termsAdded) query += "+";
            else termsAdded = true;
            i = i.replaceAll(" ", ",");
            query += "isbn:" + i;
        }

        query += "&key=AIzaSyBWUqhTT8y4aC9hyFgjenA3lhqi1cnV0R0&fields=items(volumeInfo"
                + "/title,volumeInfo/authors,volumeInfo/publishedDate,volumeInfo/description,"
                + "volumeInfo/industryIdentifiers)";

        return query;
    }

    private String fetchResults(String query) {
        try {
            return new SearchInet(this).execute(query).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        return "Unable to Connect";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.global, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHome: startActivity(new Intent( this, HomeScreen.class));
                break;
            case R.id.menuCollections: startActivity(new Intent(this, CollectionsScreen.class));
                break;
            case R.id.action_settings:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

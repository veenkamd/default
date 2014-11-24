package com.merqurius.search;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.merqurius.R;
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
    EditText titlebox, authorbox, genrebox, isbnbox;
    String title, author, genre, isbn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        search = (Button) findViewById(R.id.buttonSearch);
        search.setOnClickListener(this);

        titlebox = (EditText) findViewById(R.id.title_text);
        titlebox.setTextColor(Color.WHITE);
        authorbox = (EditText) findViewById(R.id.author_text);
        authorbox.setTextColor(Color.WHITE);
        genrebox = (EditText) findViewById(R.id.genre_text);
        genrebox.setTextColor(Color.WHITE);
        isbnbox = (EditText) findViewById(R.id.isbn_text);
        isbnbox.setTextColor(Color.WHITE);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.buttonSearch:
                title = titlebox.getText().toString();
                author = authorbox.getText().toString();
                genre = genrebox.getText().toString();
                isbn = isbnbox.getText().toString();

                String q = buildQuery(title, author, genre, isbn);
                String r = fetchResults(q);
                if(! r.equals("Unable to Connect")) {
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
    }

    private String buildQuery(String t, String a, String g, String i) {
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
        if(g.length() > 0){
            if(termsAdded) query += "+";
            else termsAdded = true;
            g = g.replaceAll(" ", ",");
            query += "insubject:" + g;
        }
        if(i.length() > 0){
            if(termsAdded) query += "+";
            else termsAdded = true;
            i = i.replaceAll(" ", ",");
            query += "isbn:" + i;
        }

        query += "&key=AIzaSyBWUqhTT8y4aC9hyFgjenA3lhqi1cnV0R0&fields=items(volumeInfo"
                + "/title,volumeInfo/authors,volumeInfo/industryIdentifiers)";

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
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

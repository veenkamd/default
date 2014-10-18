package com.merqurius.search;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.merqurius.R;
import com.merqurius.search.SearchResultsScreen;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

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
        authorbox = (EditText) findViewById(R.id.author_text);
        genrebox = (EditText) findViewById(R.id.genre_text);
        isbnbox = (EditText) findViewById(R.id.isbn_text);

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

                Intent searchResultsIntent = new Intent(v.getContext(), SearchResultsScreen.class);
                searchResultsIntent.putExtra("response", r);
                startActivityForResult(searchResultsIntent, 0);
                break;
        }
    }

    private String buildQuery(String t, String a, String g, String i) {
        String query = "https://www.googleapis.com/books/v1/volumes?q=";
        boolean termsAdded = false;
        if(t.length() > 0){
            if(termsAdded) query += "+";
            else termsAdded = true;
            query += "intitle:" + t;
        }
        if(a.length() > 0){
            if(termsAdded) query += "+";
            else termsAdded = true;
            query += "inauthor:" + a;
        }
        if(g.length() > 0){
            if(termsAdded) query += "+";
            else termsAdded = true;
            query += "insubject:" + g;
        }
        if(i.length() > 0){
            if(termsAdded) query += "+";
            else termsAdded = true;
            query += "isbn:" + i;
        }

        query += "&key=AIzaSyBWUqhTT8y4aC9hyFgjenA3lhqi1cnV0R0";

        return query;
    }

    private String fetchResults(String query){
        HttpURLConnection connection = null;
        // Build Connection.
        try {
            URL url = new URL(query);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(5000); // 5 seconds
            connection.setConnectTimeout(5000); // 5 seconds
            /*int responseCode = connection.getResponseCode();
            if(responseCode != 200){
                //Log.w(getClass().getName(), "GoogleBooksAPI request failed. Response Code: " + responseCode);
                connection.disconnect();
                throw new Exception();
            }*/
            // Read data from response.
            StringBuilder builder = new StringBuilder();
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = responseReader.readLine();
            while (line != null){
                builder.append(line);
                line = responseReader.readLine();
            }
            String response = builder.toString();
            connection.disconnect();
            return response;

        } catch (Exception e){
            connection.disconnect();
            new AlertDialog.Builder(this)
                    //.setTitle("Search Terms")
                    .setMessage("Unable to connect.")
                            /*.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })*/
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        return "";
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

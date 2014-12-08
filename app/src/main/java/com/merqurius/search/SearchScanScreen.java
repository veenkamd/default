package com.merqurius.search;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.merqurius.MySQLiteHelper;
import com.merqurius.R;
import com.google.zxing.integration.android.*;
import com.merqurius.book.details.BookDetailsScreen;
import com.merqurius.collections.CollectionsScreen;
import com.merqurius.home.HomeScreen;

import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

public class SearchScanScreen extends Activity {
    final Context context = this;
    String title, author, isbn, pubdate, desc, imgurl, collection;
    boolean foundStuff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        // starting the scanner
        try {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "SCAN_MODE");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException e){
            Log.d("SearchScanScreen", "Scanner not installed");
            Toast.makeText(context, "Please install the ZXing Barcode Scanner app before trying to scan a book.",
                    Toast.LENGTH_LONG).show();

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_scan, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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

    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String barcodeScan;
        if (resultCode == Activity.RESULT_OK){
             barcodeScan = intent.getStringExtra("SCAN_RESULT");
             String q = buildQuery(barcodeScan);
             String r = fetchResults(q);
             if(! r.equals("Unable to Connect")) {
                Intent searchResultsIntent = new Intent(context, SearchResultsScreen.class);
                searchResultsIntent.putExtra("query", q);
                searchResultsIntent.putExtra("response", r);
                parseText(r);
                 if(foundStuff){
                     Log.d(getClass().getName(), "Forwarding to details screen.");
                     Intent detailsIntent = new Intent (this, BookDetailsScreen.class);
                     detailsIntent.putExtra("author", author);
                     detailsIntent.putExtra("title", title);
                     detailsIntent.putExtra("isbn", isbn);
                     detailsIntent.putExtra("published", pubdate);
                     detailsIntent.putExtra("description", desc);
                     detailsIntent.putExtra("img", imgurl);
                     detailsIntent.putExtra("collection", collection);
                     startActivityForResult(detailsIntent, 0);
                 } else {
                     builder.setMessage("The barcode scanned does not match any available ISBN.");
                     builder.show();
                 }
             } else {
                builder.setMessage(r);
                builder.show();
             }
        } else {
            builder.setMessage("The scan did not work. Please try again.");
            builder.show();
            Intent homeIntent = new Intent(context, HomeScreen.class);
            startActivityForResult(homeIntent, 0);
        }

    }

    private String buildQuery(String s) {
        String query = "https://www.googleapis.com/books/v1/volumes?q=";
        if(s.length() > 0){
            s = s.replaceAll(" ", ",");
            query += "isbn:" + s;
        }
        query += "&key=AIzaSyBWUqhTT8y4aC9hyFgjenA3lhqi1cnV0R0&fields=items(volumeInfo"
                + "/title,volumeInfo/authors,volumeInfo/publishedDate,volumeInfo/description,"
                + "volumeInfo/industryIdentifiers,volumeInfo/imageLinks/thumbnail)";

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

    private void parseText(String response){
        StringTokenizer st = new StringTokenizer(response, ":{}[]\"\t\n");
        boolean endList = false;
        foundStuff = false;

        String tok = null;
        if(!st.hasMoreTokens()) {
            endList = true;
        }
        else
            tok = st.nextToken();


        if(endList)
            title = null;
        else {

            //find next book
            while (!tok.equals("title")) {
                if(!st.hasMoreTokens()) {
                    endList = true;
                    break;
                }
                tok = st.nextToken();
            }
            if(!st.hasMoreTokens()) {
                title = null;
            }

            tok = st.nextToken();
            tok = st.nextToken();

            title = tok;
            foundStuff = true;
            while(!tok.equals("authors") && !tok.equals("publishedDate") && !tok.equals("description") && !tok.equals("industryIdentifiers") && !tok.equals("thumbnail") && !tok.equals("title")){
                if(!st.hasMoreTokens()){
                    //endList = true;
                    break;
                }
                tok = st.nextToken();
            }
            if(tok.equals("authors")) {
                tok = st.nextToken("\"");
                author = st.nextToken("\"");
            }
            else
                author = "";

            while(!tok.equals("publishedDate") && !tok.equals("description") && !tok.equals("industryIdentifiers") && !tok.equals("thumbnail") && !tok.equals("title")) {
                if(!st.hasMoreTokens()){
                    break;
                }
                tok = st.nextToken();
            }
            if(tok.equals("publishedDate")) {
                tok = st.nextToken("\"");
                pubdate = st.nextToken("\"");
            }

            while(!tok.equals("description") && !tok.equals("industryIdentifiers") && !tok.equals("thumbnail") && !tok.equals("title")) {
                if(!st.hasMoreTokens()){
                    break;
                }
                tok = st.nextToken();
            }
            if(tok.equals("description")) {
                tok = st.nextToken("\"");
                desc = st.nextToken("\"");
            }

            boolean endOfDesc = false;
            while(!tok.equals("industryIdentifiers") && !tok.equals("thumbnail") && !tok.equals("title")) {
                if(!st.hasMoreTokens()) {
                    endList = true;
                    break;
                }
                tok = st.nextToken();
                if(desc != null && !endOfDesc && !desc.equals("") && !tok.equals("industryIdentifiers")  && !tok.equals("thumbnail") && !tok.equals("title")) {
                    if(tok.contains("}"))
                        endOfDesc = true;
                    else
                        desc += tok;
                }
            }
            if(desc != null && !desc.equals("")) {
                desc = desc.replaceAll("\\\\", "\""); //fix quotes
                desc = desc.trim();
                if(!endOfDesc)
                    desc = desc.substring(0, desc.length() - 1); //remove end-of-field comma
            }


            boolean isbn10 = false;
            boolean isbn13 = false;
            if (tok.equals("industryIdentifiers")) {
                //determine type of identifier
                //try to find ISBN13; if not, ISBN10
                //if neither are present, will have to look up by author/title

                while (!isbn13 && !tok.equals("thumbnail") && !tok.equals("title")) {
                    while (!tok.equals("type") && !tok.equals("title")) {
                        if (!st.hasMoreTokens()) {
                            break;
                        }
                        tok = st.nextToken();
                    }
                    if (!st.hasMoreTokens())
                        break;
                    if(tok.equals("type")) {
                        tok = st.nextToken();
                        tok = st.nextToken();
                        if (tok.equals("ISBN_10") || tok.equals("ISBN_13")) {
                            if (tok.equals("ISBN_10") && !isbn13) {
                                isbn10 = true;
                                tok = st.nextToken(":");
                                tok = st.nextToken("\"");
                                isbn = st.nextToken("\"");
                            } else {
                                isbn13 = true;
                                tok = st.nextToken(":");
                                tok = st.nextToken("\"");
                                isbn = st.nextToken("\"");
                            }

                        }
                    }
                }
            }

            if (!isbn10 && !isbn13)
                isbn = null;

            while (st.hasMoreTokens() && !st.nextToken().equals("thumbnail")) {

                //Log.d(getClass().getName(), "token: " +  st.nextToken());
            }
            if (st.hasMoreTokens()) {
                st.nextToken();
                imgurl = st.nextToken();
            } else
                imgurl = "";


        }
        Log.d(getClass().getName(), "...Done.");
    }

}

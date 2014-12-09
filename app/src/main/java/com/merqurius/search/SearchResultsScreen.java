package com.merqurius.search;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

import com.merqurius.R;
import com.merqurius.book.details.BookDetailsScreen;
import com.merqurius.collections.CollectionsScreen;
import com.merqurius.home.HomeScreen;


public class SearchResultsScreen extends Activity implements View.OnClickListener{

    String q, r;
    int prevId, nextId, currIndex;
    boolean foundStuff;
    Button prev, next;
    Button[] result;
    String[] authors;
    String[] titles;
    String[] isbns;
    String[] pubdates;
    String[] descs;
    String[] imgurls;
    String[] collections;
    Boolean online, endReached;
    int numResults;


    LinearLayout layout, pagingLayout;
    LinearLayout.LayoutParams params, pagingParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        layout = (LinearLayout) findViewById(R.id.layout_search_results);
        pagingLayout = new LinearLayout(this);
        pagingLayout.setHorizontalGravity(Gravity.RIGHT);

        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(12, 0, 12, 0);

        pagingParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        pagingParams.setMargins(12, 24, 12, 12);

        Intent searchResultsIntent = getIntent();
        online = searchResultsIntent.getBooleanExtra("mode", true);

        if(online) {
            authors = new String[10];
            titles = new String[10];
            isbns = new String[10];
            pubdates = new String[10];
            descs = new String[10];
            Log.d(getClass().getName(), "Online query");
            q = searchResultsIntent.getStringExtra("query");
            r = searchResultsIntent.getStringExtra("response");
            numResults = 10;
            parseText(r);

        } else {
            authors = searchResultsIntent.getStringArrayExtra("author");
            titles = searchResultsIntent.getStringArrayExtra("title");
            isbns = searchResultsIntent.getStringArrayExtra("isbn");
            pubdates = searchResultsIntent.getStringArrayExtra("published");
            descs = searchResultsIntent.getStringArrayExtra("description");
            imgurls = searchResultsIntent.getStringArrayExtra("img");
            collections = searchResultsIntent.getStringArrayExtra("collection");
            if(titles.length < 1)
                foundStuff = false;
            else foundStuff = true;
            numResults = titles.length;
        }
        endReached = false;
        if (foundStuff) {
            result = new Button[numResults];
            for (int i = 0; i < numResults; i++) {
                if (titles[i] != null) {
                    result[i] = new Button(this);
                    result[i].setId(i);
                    result[i].setLayoutParams(params);
                    result[i].setTextColor(Color.WHITE);
                    result[i].setTypeface(null, Typeface.BOLD);
                    result[i].setText(authors[i] + ": \"" + titles[i] + "\"");
                    result[i].setClickable(true);
                    result[i].setOnClickListener(this);
                    layout.addView(result[i]);
                } else
                    endReached = true;
            }
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("No Results to Display");
            builder.show();
        }



            //prevId = View.generateViewId(); //conflicted with results buttons
            prevId = 97; //shows error in editor but compiles and runs correctly
            prev = new Button(this);
            prev.setId(prevId);
            prev.setLayoutParams(pagingParams);
            prev.setTextColor(Color.WHITE);
            prev.setTypeface(null, Typeface.BOLD);
            prev.setText("<-Prev");
            prev.setEnabled(false);
            prev.setOnClickListener(this);
            pagingLayout.addView(prev);

            //nextId = View.generateViewId();
            nextId = 98;
            next = new Button(this);
            next.setId(nextId);
            next.setLayoutParams(pagingParams);
            next.setTextColor(Color.WHITE);
            next.setTypeface(null, Typeface.BOLD);
            next.setText("Next->");
            if (endReached) {
                next.setEnabled(false);
            } else {
                next.setEnabled(true);
            }
            next.setOnClickListener(this);
            pagingLayout.addView(next);

            if(online) layout.addView(pagingLayout);

            currIndex = 0;

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

        for(int i = 0; i < numResults; i++){
            Log.d(getClass().getName(), "Processing book " + i + "...");

            if(endList)
                titles[i] = null;
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
                    titles[i] = null;
                    break;
                }

                tok = st.nextToken();
                tok = st.nextToken();

                titles[i] = tok;
                foundStuff = true;
                while(!tok.equals("authors") && !tok.equals("publishedDate") && !tok.equals("description") && !tok.equals("industryIdentifiers") && !tok.equals("title")){
                    if(!st.hasMoreTokens()){
                        //endList = true;
                        break;
                    }
                    tok = st.nextToken();
                }
                if(tok.equals("authors")) {
                    tok = st.nextToken("\"");
                    authors[i] = st.nextToken("\"");
                }
                else
                    authors[i] = "";

                while(!tok.equals("publishedDate") && !tok.equals("description") && !tok.equals("industryIdentifiers") && !tok.equals("title")) {
                    if(!st.hasMoreTokens()){
                        break;
                    }
                    tok = st.nextToken();
                }
                if(tok.equals("publishedDate")) {
                    tok = st.nextToken("\"");
                    pubdates[i] = st.nextToken("\"");
                }

                while(!tok.equals("description") && !tok.equals("industryIdentifiers") && !tok.equals("title")) {
                    if(!st.hasMoreTokens()){
                        break;
                    }
                    tok = st.nextToken();
                }
                if(tok.equals("description")) {
                    tok = st.nextToken("\"");
                    descs[i] = st.nextToken("\"");
                }

                boolean endOfDesc = false;
                while(!tok.equals("industryIdentifiers") && !tok.equals("title")) {
                    if(!st.hasMoreTokens()) {
                        endList = true;
                        break;
                    }
                    tok = st.nextToken();
                    if(descs[i] != null && !endOfDesc && !descs[i].equals("") && !tok.equals("industryIdentifiers") && !tok.equals("title")) {
                        if(tok.contains("}"))
                            endOfDesc = true;
                        else
                            descs[i] += tok;
                    }
                }
                if(descs[i] != null && !descs[i].equals("")) {
                    descs[i] = descs[i].replaceAll("\\\\", "\""); //fix quotes
                    descs[i] = descs[i].trim();
                    if(!endOfDesc)
                        descs[i] = descs[i].substring(0, descs[i].length() - 1); //remove end-of-field comma
                }


                    boolean isbn10 = false;
                    boolean isbn13 = false;
                    if (tok.equals("industryIdentifiers")) {
                        //determine type of identifier
                        //try to find ISBN13; if not, ISBN10
                        //if neither are present, will have to look up by author/title

                        while (!isbn13 && !tok.equals("title")) {
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
                                        isbns[i] = st.nextToken("\"");
                                    } else {
                                        isbn13 = true;
                                        tok = st.nextToken(":");
                                        tok = st.nextToken("\"");
                                        isbns[i] = st.nextToken("\"");
                                    }

                                }
                            }
                        }
                    }

                    if (!isbn10 && !isbn13)
                        isbns[i] = null;


            }
            Log.d(getClass().getName(), "...Done.");
        }
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
    public void onClick(View v) {
        if(v.getId() == nextId || v.getId() == prevId){
            int oldIndex = currIndex;
            if(v.getId() == nextId)
                currIndex += 10;
            else if(currIndex > 0)
                currIndex -= 10;

            r = fetchResults(q + "&startIndex=" + currIndex);

            if(r.equals("Unable to Connect")){
                currIndex = oldIndex;
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(r);
                builder.show();
            } else {
                parseText(r);
                if(foundStuff) {
                    boolean endReached = false;
                    layout.removeView(pagingLayout);
                    for (int i = 0; i < 10; i++) {
                        layout.removeView(result[i]);
                        if(titles[i] != null) {
                            result[i].setText(authors[i] + ": \"" + titles[i] + "\"");
                            layout.addView(result[i]);
                        } else
                            endReached = true;
                    }
                    if (currIndex > 0) {
                        prev.setEnabled(true);
                    } else {
                        prev.setEnabled(false);
                    }
                    if (endReached) {
                        next.setEnabled(false);
                    } else {
                        next.setEnabled(true);
                    }
                    layout.addView(pagingLayout);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("No Results to Display");
                    builder.show();
                }
            }
        } else {
            int i = v.getId();
            /*String m = "Author: " + authors[i] + "\nTitle: " + titles[i] + "\nISBN: ";
            if(isbns[i] == null)
                m += "None";
            else
                m += isbns[i];
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(m);
            builder.show();*/
            String imgr = "";
            if(online) {
                //retrieve thumbnail url
                String imgq = "https://www.googleapis.com/books/v1/volumes?q=";
                if (isbns[i] == null) {
                    imgq += "intitle:" + titles[i];
                    if (authors[i] != null)
                        imgq += "+inauthor:" + (authors[i]).replaceAll(" ", ",");
                } else
                    imgq += "isbn:" + isbns[i];
                imgq += "&key=AIzaSyBWUqhTT8y4aC9hyFgjenA3lhqi1cnV0R0&fields=items(volumeInfo/imageLinks/thumbnail)";

                imgr = fetchResults(imgq);
                if (!imgr.equals("Unable to Connect")) {
                    StringTokenizer st = new StringTokenizer(imgr, "{}[]\"\t\n");
                    while (st.hasMoreTokens() && !st.nextToken().equals("thumbnail")) {

                        //Log.d(getClass().getName(), "token: " +  st.nextToken());
                    }
                    if (st.hasMoreTokens()) {
                        st.nextToken();
                        imgr = st.nextToken();
                    } else
                        imgr = "";
                }
            }

            Intent detailsIntent = new Intent (v.getContext(), BookDetailsScreen.class);
            detailsIntent.putExtra("author", authors[i]);
            detailsIntent.putExtra("title", titles[i]);
            detailsIntent.putExtra("isbn", isbns[i]);
            detailsIntent.putExtra("published", pubdates[i]);
            detailsIntent.putExtra("description", descs[i]);
            if(online)
                detailsIntent.putExtra("img", imgr);
            else {
                detailsIntent.putExtra("img", imgurls[i]);
                detailsIntent.putExtra("collection", collections[i]);
            }
            startActivityForResult(detailsIntent, 0);

        }
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


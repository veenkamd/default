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


public class SearchResultsScreen extends Activity implements View.OnClickListener{

    String q, r;
    int prevId, nextId, currIndex;
    static int activeColor = 0xff5ff4ff, inActiveColor = 0xeeeeee;
    boolean foundStuff;
    Button prev, next;
    Button[] result;
    String[] authors;
    String[] titles;
    String[] isbns;
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
        params.setMargins(0, 0, 12, 12);

        pagingParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        pagingParams.setMargins(12, 24, 12, 12);

        authors = new String[10];
        titles = new String[10];
        isbns = new String[10];

        Intent searchResultsIntent = getIntent();
        q = searchResultsIntent.getStringExtra("query");
        r = searchResultsIntent.getStringExtra("response");
        parseText(r);
        boolean endReached = false;
        if(foundStuff) {
            result = new Button[10];
            for (int i = 0; i < 10; i++) {
                if (titles[i] != null) {
                    result[i] = new Button(this);
                    result[i].setId(i);
                    result[i].setLayoutParams(params);
                    result[i].setTextColor(Color.WHITE);
                    result[i].setTypeface(null, Typeface.BOLD);
                    result[i].setBackgroundColor(activeColor);
                    result[i].setText(authors[i] + ": \"" + titles[i] + "\"");
                    result[i].setClickable(true);
                    result[i].setOnClickListener(this);
                    layout.addView(result[i]);
                }
                else
                    endReached = true;
            }
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("No Results to Display");
            builder.show();
        }


        //prevId = View.generateViewId(); //conflicted with results buttons
        prevId = 10; //shows error in editor but compiles and runs correctly
        prev = new Button(this);
        prev.setId(prevId);
        prev.setLayoutParams(pagingParams);
        prev.setTextColor(Color.WHITE);
        prev.setTypeface(null, Typeface.BOLD);
        prev.setBackgroundColor(inActiveColor);
        prev.setText("<-Prev");
        prev.setClickable(false);
        prev.setOnClickListener(this);
        pagingLayout.addView(prev);

        //nextId = View.generateViewId();
        nextId = 11;
        next = new Button(this);
        next.setId(nextId);
        next.setLayoutParams(pagingParams);
        next.setTextColor(Color.WHITE);
        next.setTypeface(null, Typeface.BOLD);
        next.setText("Next->");
        if (endReached) {
            next.setBackgroundColor(inActiveColor);
            next.setClickable(false);
        } else {
            next.setBackgroundColor(activeColor);
            next.setClickable(true);
        }
        next.setOnClickListener(this);
        pagingLayout.addView(next);

        layout.addView(pagingLayout);

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

        for(int i = 0; i < 10; i++){
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
                while(!tok.equals("authors") && !tok.equals("industryIdentifiers") && !tok.equals("title")){
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

                while(!tok.equals("industryIdentifiers") && !tok.equals("title")) {
                    if(!st.hasMoreTokens()) {
                        endList = true;
                        break;
                    }
                    tok = st.nextToken();
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
                        prev.setBackgroundColor(activeColor);
                        prev.setClickable(true);
                    } else {
                        prev.setBackgroundColor(inActiveColor);
                        prev.setClickable(false);
                    }
                    if (endReached) {
                        next.setBackgroundColor(inActiveColor);
                        next.setClickable(false);
                    } else {
                        next.setBackgroundColor(activeColor);
                        next.setClickable(true);
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


            Intent detailsIntent = new Intent (v.getContext(), BookDetailsScreen.class);
            detailsIntent.putExtra("author", authors[i]);
            detailsIntent.putExtra("title", titles[i]);
            detailsIntent.putExtra("isbn", isbns[i]);
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
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}


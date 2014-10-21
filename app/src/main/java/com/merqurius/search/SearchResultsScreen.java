package com.merqurius.search;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import java.util.StringTokenizer;

import com.merqurius.R;


public class SearchResultsScreen extends Activity {

    String r;
    Button[] result;
    String[] authors;
    String[] titles;
    String[] isbns;
    LinearLayout layout;
    LinearLayout.LayoutParams params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        layout = (LinearLayout) findViewById(R.id.layout_search_results);

        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 12, 12);

        authors = new String[10];
        titles = new String[10];
        isbns = new String[10];

        Intent searchResultsIntent = getIntent();
        r = searchResultsIntent.getStringExtra("response");
        parseText(r);
        result = new Button[10];
        for(int i = 0; i < 10; i++) {
            if(titles[i].length() > 0) {
                result[i] = new Button(this);
                result[i].setLayoutParams(params);
                result[i].setTextColor(Color.WHITE);
                result[i].setTypeface(null, Typeface.BOLD);
                result[i].setBackgroundColor(0xff5ff4ff);
                result[i].setText(authors[i] + ": \"" + titles[i] + "\"");
                layout.addView(result[i]);
            }
        }
    }

    private void parseText(String response){
        StringTokenizer st = new StringTokenizer(response, ":{}[]\"\t\n");
        boolean endList = false;

        String tok = st.nextToken();
        for(int i = 0; i < 10; i++){

            if(endList)
                titles[i] = "";
            else {

                //find next book
                while (!tok.equals("title")) {
                    tok = st.nextToken();
                    if(tok == null){
                        endList = true;
                        break;
                    }
                }

                tok = st.nextToken();
                tok = st.nextToken();
                if(tok == null)
                    titles[i] = "";
                else {
                    titles[i] = tok;
                    while(!tok.equals("authors") && !tok.equals("title")){
                        tok = st.nextToken();
                        if(tok == null){
                            endList = true;
                            break;
                        }
                    }
                    if(tok.equals("authors")) {
                        tok = st.nextToken("\"");
                        authors[i] = st.nextToken("\"");
                    }
                    else
                        authors[i] = "";
                }
            }
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

/*
{
 "items": [
  {
   "volumeInfo": {
    "title": "The Language of Flowers",
    "authors": [
     "Henrietta Dumont"
    ],
    "industryIdentifiers": [
     {
      "type": "OTHER",
      "identifier": "HARVARD:32044013658547"
     }
    ]
   }
  },
  {
   "volumeInfo": {
    "title": "Diversity and Evolutionary Biology of Tropical Flowers",
    "industryIdentifiers": [
     {
      "type": "ISBN_10",
      "identifier": "0521565103"
     },
     {
      "type": "ISBN_13",
      "identifier": "9780521565103"
     }
    ]
   }
  }
 ]
}
 */
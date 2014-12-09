package com.merqurius.search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.merqurius.R;
import com.merqurius.collections.CollectionsScreen;
import com.merqurius.home.HomeScreen;


public class SearchResults extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_results, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHome: startActivity(new Intent( this, HomeScreen.class));
                break;
            case R.id.menuCollections: startActivity(new Intent(this, CollectionsScreen.class));
                break;

        }
        return super.onOptionsItemSelected(item);
    }

}

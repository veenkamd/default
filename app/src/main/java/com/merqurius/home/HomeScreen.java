package com.merqurius.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.merqurius.R;
import com.merqurius.book.details.BookDetailsScreen;
import com.merqurius.collections.CollectionsScreen;
import com.merqurius.search.SearchScreen;
import com.merqurius.search.SearchScanScreen;


public class HomeScreen extends Activity implements View.OnClickListener {

    Button collections, search, detailsPlaceholder, scan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        collections = (Button) findViewById(R.id.buttonGoToLibrary);
        search = (Button) findViewById(R.id.buttonSearchBook);
        detailsPlaceholder = (Button) findViewById(R.id.buttonDetails);
        scan = (Button) findViewById(R.id.buttonScanBook);

        collections.setOnClickListener(this);
        search.setOnClickListener(this);
        detailsPlaceholder.setOnClickListener(this);
        scan.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.buttonGoToLibrary:
                Intent collectionsIntent = new Intent(v.getContext(), CollectionsScreen.class);
                startActivityForResult(collectionsIntent, 0);
                break;
            case R.id.buttonSearchBook:
                Intent searchIntent = new Intent(v.getContext(), SearchScreen.class);
                searchIntent.putExtra("mode", true);
                startActivityForResult(searchIntent, 0);
                break;
            case R.id.buttonScanBook:
                Intent scanIntent = new Intent(v.getContext(), SearchScanScreen.class);
                startActivityForResult(scanIntent, 0);
                break;
            case R.id.buttonDetails:
                Intent searchIntent2 = new Intent(v.getContext(), SearchScreen.class);
                searchIntent2.putExtra("mode", false);
                startActivityForResult(searchIntent2, 0);
                break;
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

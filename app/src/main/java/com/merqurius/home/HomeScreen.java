package com.merqurius.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.merqurius.R;
import com.merqurius.collections.CollectionsScreen;
import com.merqurius.search.SearchScreen;


public class HomeScreen extends Activity implements View.OnClickListener {

    Button collections, search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        collections = (Button) findViewById(R.id.buttonGoToLibrary);
        search = (Button) findViewById(R.id.buttonSearchBook);

        collections.setOnClickListener(this);
        search.setOnClickListener(this);
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
                startActivityForResult(searchIntent, 0);
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

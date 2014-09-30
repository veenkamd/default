package com.example.merqurius;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class HomeScreen extends Activity {

    /* For strings.xml file
    <string name="home_app_name">Merqurius</string>
    <string name="home_titleLine1">Welcome to</string>
    <string name="home_button_collections">View My Collections</string>
    <string name="home_button_scanBook">Scan a New Book</string>
    <string name="home_button_searchBook">Search for a Book</string>
    <string name="home_button_settings">ViewSettings</string>
    <string name="home_titleLine2">Your personal library manager</string>
    <string name="home_appDescription">The title of the application</string>
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_screen, menu);
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

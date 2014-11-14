package com.merqurius.collections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.merqurius.Database;
import com.merqurius.MySQLiteHelper;
import com.merqurius.book.details.BookDetailsScreen;
import com.merqurius.test.Book;
import com.merqurius.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Toast;


public class CollectionsScreen extends Activity {
    //Expandable list stuff
    ExpandableListAdapter adapter;
    ExpandableListView listView;
    List<String> collectionDataHeader;
    HashMap<String, List<String>> collectionDataChild;

    final Context context = this;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collections);

        prepareListView();
        prepareListData();
        this.adapter = new ExpandableListAdapter(this, this.collectionDataHeader, this.collectionDataChild);
        this.listView.setAdapter(this.adapter);

        addListenerOnButton();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.collections, menu);
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

    private void prepareListData() {
        collectionDataHeader = new ArrayList<String>();
        collectionDataChild = new HashMap<String, List<String>>();

        collectionDataHeader = getAllCollections();

        //Add the books to the collections map
        for(int i = 0; i < collectionDataHeader.size(); i++){
            String collectionName = collectionDataHeader.get(i);
            List<String> titles = getBookTitlesForCollection(collectionName);
            Log.d(getClass().getName(), titles.toString());
            collectionDataChild.put(collectionName, titles);
        }
    }

    private void prepareListView(){
        this.listView = (ExpandableListView) findViewById(R.id.expandableListView);

        this.listView.setOnGroupClickListener(new OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View view,
                                        int groupPosition, long id) {
                // Toast.makeText(getApplicationContext(),
                // "Group Clicked " + listDataHeader.get(groupPosition),
                // Toast.LENGTH_SHORT).show();

                //TODO: do stuff?
                return false;
            }
        });

        // Listview Group expanded listener
        listView.setOnGroupExpandListener(new OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                /*Toast.makeText(getApplicationContext(),
                        collectionDataHeader.get(groupPosition) + " Expanded",
                        Toast.LENGTH_SHORT).show();*/

                //TODO: do stuff if this is expanded
            }
        });

        // Listview Group collapsed listener
        listView.setOnGroupCollapseListener(new OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                /*Toast.makeText(getApplicationContext(),
                        collectionDataHeader.get(groupPosition) + " Collapsed",
                        Toast.LENGTH_SHORT).show();*/

                //TODO: do stuff if this is collapsed?
            }
        });

        // Listview on child click listener
        listView.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View view,
                                        int groupPosition, int childPosition, long id) {
                /*Toast.makeText(
                        getApplicationContext(),
                        collectionDataHeader.get(groupPosition)
                                + " : "
                                + collectionDataChild.get(
                                collectionDataHeader.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();*/

                //TODO: do stuff if this is clicked

                String title = collectionDataChild.get(collectionDataHeader.get(groupPosition)).get(childPosition);
                String collectionName = collectionDataHeader.get(groupPosition);
                MySQLiteHelper db = new MySQLiteHelper(context);
                Cursor cursor = db.selectBookDetails(title, collectionName);

                if(cursor.moveToFirst())
                {
                    Intent detailsIntent = new Intent (listView.getContext(), BookDetailsScreen.class);
                    int columnIndex = cursor.getColumnIndex(Database.AUTHOR);
                    detailsIntent.putExtra("author", cursor.getString(columnIndex));
                    columnIndex = cursor.getColumnIndex(Database.TITLE);
                    detailsIntent.putExtra("title", cursor.getString(columnIndex));
                    columnIndex = cursor.getColumnIndex(Database.ISBN);
                    detailsIntent.putExtra("isbn", cursor.getString(columnIndex));
                    columnIndex = cursor.getColumnIndex(Database.IMGURL);
                    detailsIntent.putExtra("img", cursor.getString(columnIndex));
                    columnIndex = cursor.getColumnIndex(Database.COLLECTION);
                    detailsIntent.putExtra("collection", cursor.getString(columnIndex));
                    startActivityForResult(detailsIntent, 0);

                }



                return false;
            }
        });
    }

    //Creates listener for the "Add Collection" Button
    public void addListenerOnButton() {
        button = (Button) findViewById(R.id.add_collection_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                // get collection_prompt.xml view
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.collections_prompt, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                // set collections_prompt.xml to alertDialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView.findViewById(R.id.newCollectionInput);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(true)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        String newCollection = userInput.getText().toString();

                                        MySQLiteHelper db = new MySQLiteHelper(context);
                                          db.insertCollection(newCollection);

                                        Intent intent = getIntent();
                                        finish();
                                        startActivity(intent);
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }

    protected List getAllCollections(){
        MySQLiteHelper db = new MySQLiteHelper(context);
        Cursor cursor = db.selectAllCollections();

        List<String> collections = new ArrayList<String>();
        while(cursor.moveToNext()){
            int columnIndex = cursor.getColumnIndex(Database.COLLECTION_NAME);
            Log.d(getClass().getName(), "column index: " + columnIndex);
            collections.add(cursor.getString(columnIndex));
        }
        return collections;
    }

    protected List getBookTitlesForCollection(String collectionName){
        MySQLiteHelper db = new MySQLiteHelper(context);
        Cursor cursor = db.selectBookTitlesByCollection(collectionName);

        Log.d("We're in the", "get book titles method");

        List<String> titles = new ArrayList<String>();
        if(cursor.moveToFirst())
            do {
                int columnIndex = cursor.getColumnIndex(Database.TITLE);
                Log.d("columnIndex", "" + columnIndex);
                Log.d("column value", "" + cursor.getString(columnIndex));
                titles.add(cursor.getString(columnIndex));
            } while(cursor.moveToNext());

        return titles;
    }
}

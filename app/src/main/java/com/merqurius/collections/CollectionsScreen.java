package com.merqurius.collections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.merqurius.test.Book;
import com.merqurius.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
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

        List<Book> testBooks = new ArrayList<Book>();
        for(int i = 1; i < 4; i++) {
            testBooks.add(new Book());
        }

        // Adding header data
        //Swap out with:
        //  collectionDataHeader = getAllCollections();
        collectionDataHeader.add("First Collection");
        collectionDataHeader.add("Second Collection");
        collectionDataHeader.add("Third Collection");


        // Adding child data
        List<String> first = new ArrayList<String>();
        first.add(testBooks.get(0).getTitle());
        first.add(testBooks.get(0).getTitle());
        first.add(testBooks.get(0).getTitle());

        List<String> second = new ArrayList<String>();
        second.add(testBooks.get(1).getTitle());
        second.add(testBooks.get(1).getTitle());
        second.add(testBooks.get(1).getTitle());

        List<String> third = new ArrayList<String>();
        third.add(testBooks.get(2).getTitle());
        third.add(testBooks.get(2).getTitle());
        third.add(testBooks.get(2).getTitle());

        collectionDataChild.put(collectionDataHeader.get(0), first); // Header, Child data
        collectionDataChild.put(collectionDataHeader.get(1), second);
        collectionDataChild.put(collectionDataHeader.get(2), third);

        /* Actual implementation once database tasks are done:

           for(int i = 0; i < collectionDataHeader.length; i++){
               String collectionName = collectionDataHeader.get(i);
               List<String> titles = getBookTitlesForCollection(collectionName);
               collectionDataChild.put(collectionName, titles);
           }
         */
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
                Toast.makeText(getApplicationContext(),
                        collectionDataHeader.get(groupPosition) + " Expanded",
                        Toast.LENGTH_SHORT).show();

                //TODO: do stuff if this is expanded
            }
        });

        // Listview Group collapsed listener
        listView.setOnGroupCollapseListener(new OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        collectionDataHeader.get(groupPosition) + " Collapsed",
                        Toast.LENGTH_SHORT).show();

                //TODO: do stuff if this is collapsed?
            }
        });

        // Listview on child click listener
        listView.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View view,
                                        int groupPosition, int childPosition, long id) {
                Toast.makeText(
                        getApplicationContext(),
                        collectionDataHeader.get(groupPosition)
                                + " : "
                                + collectionDataChild.get(
                                collectionDataHeader.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();

                //TODO: do stuff if this is clicked
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

                                        //TODO: Remove this comment to finish implementing
                                        /*SQLiteDatabase db = new SQLiteCustomDatabase();
                                          db.insertCollection(newCollection);
                                         */
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

                /*

                The following methods will be put in place when the database class we need is done


                public void insertCollection(String collectionName) throws SomeException {
                    SQLiteDatabase db = this.getWritableDatabase();
                    ContentValues cv = new ContentValues();
                    cv.put(Database.COLLECTION_ID, null);
                    cv.put(Database.COLLECTION, collectionName);
                    db.insertOrThrow(Database.COLLECTION_TBL, null, cv);
                    db.close();
                }

                public Cursor selectAllCollections(){
                       SQLiteDatabase db = this.getReadableDatabase();
                       return db.rawQuery(Database.SELECT_ALL_COLLECTIONS, new String[]{});
                }

                protected List getAllCollections(){
                    SQLiteDatabase db = new SQLiteDatabaseCustomClass();
                    Cursor cursor = db.selectAllCollections();

                    List<String> collections = new ArrayList<String>();
                    while(cursor.moveToNext()){
                        collections.add(cursor.getColumnIndex(Database.COLLECTION));
                    }

                    return collections;
                }

                public Cursor selectBookTitlesByCollection(String collectionName){
                    SQLiteDatabase db = this.getReadableDatabase();
                    return db.rawQuery(SELECT_BOOK_TITLES_FOR_COLLECTION,new String[]{collectionName});
                }

                protected List getBookTitlesForCollection(String collectionName){
                    SQLiteDatabase db = new SQLiteDatabaseCustomClass();
                    Cursor cursor = db.selectBookTitlesByCollection(collectionName);

                    List<String> titles = new ArrayList<String>();
                    while(cursor.moveToNext()){
                        titles.add(cursor.getColumnIndex(Database.TITLE));
                    }

                    return titles;
                }


                */
        });
    }
}

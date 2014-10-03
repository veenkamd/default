package com.merqurius.collections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.merqurius.test.Book;
import com.merqurius.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Toast;


public class CollectionsScreen extends Activity {
    ExpandableListAdapter adapter;
    ExpandableListView listView;
    List<String> collectionDataHeader;
    HashMap<String, List<String>> collectionDataChild;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collections);

        prepareListView();
        prepareListData();

        this.adapter = new ExpandableListAdapter(this, this.collectionDataHeader, this.collectionDataChild);

        this.listView.setAdapter(this.adapter);
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
            testBooks.add(new Book("title"+i, "author"+i, "isbn"+i, "genre"+i, "desc"+i));
        }

        // Adding header data
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
}

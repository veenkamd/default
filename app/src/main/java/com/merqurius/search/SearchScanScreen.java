package com.merqurius.search;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.merqurius.R;
import com.google.zxing.integration.android.*;
import com.merqurius.home.HomeScreen;

import java.util.concurrent.ExecutionException;

public class SearchScanScreen extends Activity {
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        // starting the scanner
        /*IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.addExtra("SCAN_WIDTH", 640);
        integrator.addExtra("SCAN_HEIGHT", 480);
        integrator.addExtra("SCAN_MODE", "PRODUCT_MODE");

        integrator.addExtra("PROMPT_MESSAGE", "Scanner Start!");
        integrator.initiateScan(IntentIntegrator.PRODUCT_CODE_TYPES);*/

        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.putExtra("SCAN_MODE", "SCAN_MODE");
        startActivityForResult(intent, 0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_scan, menu);
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

    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String barcodeScan;
        View v = this.findViewById(R.id.buttonScanBook);
        if (resultCode == Activity.RESULT_OK){
             barcodeScan = intent.getStringExtra("SCAN_RESULT");
             String q = buildQuery(barcodeScan);
             String r = fetchResults(q);
             if(! r.equals("Unable to Connect")) {
                Intent searchResultsIntent = new Intent(v.getContext(), SearchResultsScreen.class);
                searchResultsIntent.putExtra("query", q);
                searchResultsIntent.putExtra("response", r);
                Log.d(getClass().getName(), "Forwarding to results screen.");
                startActivityForResult(searchResultsIntent, 0);
             } else {
                builder.setMessage(r);
                builder.show();
             }
        } else {
            builder.setMessage("The scan did not work. Please try again.");
            builder.show();
            Intent homeIntent = new Intent(v.getContext(), HomeScreen.class);
            startActivityForResult(homeIntent, 0);
        }

    }

    private String buildQuery(String s) {
        String query = "https://www.googleapis.com/books/v1/volumes?q=";
        if(s.length() > 0){
            s = s.replaceAll(" ", ",");
            query += "isbn:" + s;
        }
        query += "&key=AIzaSyBWUqhTT8y4aC9hyFgjenA3lhqi1cnV0R0&fields=items(volumeInfo"
                + "/title,volumeInfo/authors,volumeInfo/industryIdentifiers)";

        return query;
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
}

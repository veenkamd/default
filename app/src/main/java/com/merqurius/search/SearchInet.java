package com.merqurius.search;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by daniel on 10/20/14.
 */
public class SearchInet extends AsyncTask<String, Void, String> {

    Context context;

    SearchInet(Context c){
        super();
        context = c;
    }

    @Override
    protected String doInBackground(String... query){

        String response = null;

        try{
            Log.d(getClass().getName(), "Attempting to connect...");
            InputStream responseStream = new URL(query[0]).openStream();
            Log.d(getClass().getName(), "Connection established.");

            response = streamToString(responseStream);


        } catch (IOException e){
            Log.d(getClass().getName(), "IO Exception." + query[0]);

           /* String error = "Unable to connect.";

            if(e.getMessage() != null)
                error = e.getMessage();

            new AlertDialog.Builder(context)
                    .setMessage(error)
                    .show();*/
        }

        return response;
    }

    private static String streamToString(InputStream in){
        java.util.Scanner s = new java.util.Scanner(in).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}

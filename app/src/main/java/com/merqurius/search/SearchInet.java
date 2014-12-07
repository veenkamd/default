package com.merqurius.search;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


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
            Log.d(getClass().getName(), "Query is: " + query[0]);

            response = streamToString(responseStream);
            Log.d(getClass().getName(), "Response received.");


        } catch (IOException e){
            Log.d(getClass().getName(), "IO Exception. " + query[0]);
            return "Unable to Connect";

        }

        return response;
    }

    private static String streamToString(InputStream in){
        java.util.Scanner s = new java.util.Scanner(in).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}

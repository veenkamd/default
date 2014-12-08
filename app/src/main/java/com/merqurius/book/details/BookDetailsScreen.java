package com.merqurius.book.details;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import com.merqurius.Database;
import com.merqurius.MySQLiteHelper;
import com.merqurius.R;
import com.merqurius.collections.CollectionsScreen;
import com.merqurius.home.HomeScreen;
import com.merqurius.test.Book;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


public class BookDetailsScreen extends Activity implements View.OnClickListener {
    Button remind, addBook, moveBook, loanButton, descButton;
    Spinner collectionSpinner;
    Book book;
    TextView authortext, titletext, isbntext, loanedtext, collectiontext, publishedtext;
    View promptsView, loanPromptView;
    ImageView thumbView;
    String author, title, isbn, pubdate, desc, imgURL, loanName, loanedTo;

    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_book_details_screen);

        authortext = (TextView) findViewById(R.id.authorText);
        titletext = (TextView) findViewById(R.id.titleText);
        isbntext = (TextView) findViewById(R.id.isbnText);
        loanedtext = (TextView) findViewById(R.id.loanedName);
        collectiontext = (TextView) findViewById(R.id.collectionText);
        publishedtext = (TextView) findViewById(R.id.PublishedText);

        remind = (Button) findViewById(R.id.buttonRemind);
        loanButton = (Button) findViewById(R.id.buttonLoan);
        descButton = (Button) findViewById(R.id.buttonDescription);

        Intent detailsIntent = getIntent();

        try {
            author = detailsIntent.getStringExtra("author");
            title = detailsIntent.getStringExtra("title");
            isbn = detailsIntent.getStringExtra("isbn");
            pubdate = detailsIntent.getStringExtra("published");
            desc = detailsIntent.getStringExtra("description");
            book = new Book(title, author, isbn);
            if(author != null)
                authortext.setText(author);
            else {
                authortext.setText("");
                book.setAuthor("unknown");
            }
            if(title != null)
                titletext.setText(title);
            else {
                titletext.setText("");
                book.setTitle("unknown");
            }
            if(pubdate != null) {
                publishedtext.setText(pubdate);
                book.setPublished(pubdate);
            } else {
                publishedtext.setText("");
                book.setPublished("unknown");
            }
            if(desc != null && desc.length() > 0)
                book.setDescription(desc);
            else {
                book.setDescription("Not available");
            }
            if(isbn != null) {
                fillBook(isbn);
                loanName = book.getLoaned_to();
                isbntext.setText(isbn);
            }
            else {
                isbntext.setText(isbn);
                book.setIsbn("unknown");
                loanName = "Not loaned";
            }
            if(!(loanName.equals("Not loaned"))) {
                loanedtext.setText(loanName);
                remind.setEnabled(true);
            }
            else{
                loanedtext.setText("Not loaned");
                remind.setEnabled(false);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(detailsIntent.getStringExtra("collection") != null)
            book.setCollection(detailsIntent.getStringExtra("collection"));
        collectiontext.setText(book.getCollection());

        thumbView = (ImageView) findViewById(R.id.bookCover);

        try {
            imgURL = detailsIntent.getStringExtra("img");
            book.setImgURL(imgURL);
            Log.d(getClass().getName(), "Image URL: " + imgURL);
            new GetBookThumb().execute(imgURL);
        } catch (Exception e) {
            e.printStackTrace();
            thumbView.setImageResource(R.drawable.generic_book_cover);
        }



        addBook = (Button) findViewById(R.id.buttonCollection);
        /*Going to need something here like:
        if(book != null && !Strings.isEmpty(book.getCollection())){
            moveBook = (Button) findViewById(R.id.buttonMoveCollection);
        } else {
            addBook = (Button) findViewByID(R.id.buttonCollection);
        }

        I'm not currently going to implement this because I'm not sure what we're going to do
        about possibly removing a book
         */





        remind.setOnClickListener(this);
        loanButton.setOnClickListener(this);
        descButton.setOnClickListener(this);
        addListenerOnAddBookButton();

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonRemind:
                Log.d(getClass().getName(), "Sending reminder");
                Intent remindIntent = new Intent(v.getContext(), EmailScreen.class);
                remindIntent.putExtra("loanedName", loanedtext.getText().toString());
                remindIntent.putExtra("bookTitle", titletext.getText().toString());
                startActivityForResult(remindIntent, 0);
                break;
            case R.id.buttonLoan:
                if(!(book.getLoaned_to().equals("Not loaned"))) {
                    book.setLoaned_to("Not loaned");
                    loanedtext.setText("Not loaned");
                    remind.setEnabled(false);
                    //make database method - look at update method at bottom
                    Log.d(getClass().getName(), "Ending loan");
                    MySQLiteHelper db = new MySQLiteHelper(context);
                    db.loanBook(book);
                }
                else{
                    LayoutInflater loanPromptLayout = LayoutInflater.from(context);
                    loanPromptView = loanPromptLayout.inflate(R.layout.book_details_loan_set, null);
                    final EditText loanPrompt = (EditText) loanPromptView.findViewById(R.id.loanedInput);
                    Log.d(getClass().getName(), "Starting loan");
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    builder.setView(loanPromptView);

                    builder.setCancelable(true)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            loanedTo = loanPrompt.getText().toString();
                                            book.setLoaned_to(loanedTo);
                                            loanedtext.setText(loanedTo);
                                            loanName = loanedTo;
                                            remind.setEnabled(true);
                                            MySQLiteHelper db = new MySQLiteHelper(context);
                                            db.loanBook(book);
                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();


                    //add database stuff
                }
                break;
            case R.id.buttonDescription:
                Log.d(getClass().getName(), "Showing description");
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setCancelable(true)
                    .setMessage(desc)
                    .setNegativeButton("Done",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            default:
                Log.d(getClass().getName(), "Clicked: " + v.getId());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.book_details_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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

    public void addListenerOnAddBookButton() {
        addBook = (Button) findViewById(R.id.buttonCollection);

        addBook.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // get book_details_add_prompt.xml view
                LayoutInflater li = LayoutInflater.from(context);
                promptsView = li.inflate(R.layout.book_details_add_prompt, null);

                collectionSpinner = createSpinner();

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                // set book_details_add_prompt.xml to alertDialog builder
                alertDialogBuilder.setView(promptsView);
                //alertDialogBuilder.setAdapter(adapter_state, null);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(true)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if (book.getCollection().equals("Not owned")) {
                                            book.setCollection(collectionSpinner.getSelectedItem().toString());
                                            insertBook(collectionSpinner.getSelectedItem().toString());
                                            Log.d("Inserting to:", collectionSpinner.getSelectedItem().toString());
                                        } else {
                                            book.setCollection(collectionSpinner.getSelectedItem().toString());
                                            updateBook(collectionSpinner.getSelectedItem().toString());
                                            Log.d("Inserting to:", collectionSpinner.getSelectedItem().toString());
                                        }
                                        collectiontext.setText(book.getCollection());

                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }

    public void addListenerOnMoveBookButton() {
        moveBook = (Button) findViewById(R.id.buttonCollection);

        moveBook.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // get book_details_add_prompt.xml view
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.book_details_add_prompt, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                // set book_details_add_prompt.xml to alertDialog builder
                alertDialogBuilder.setView(promptsView);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(true)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        updateBook(String.valueOf(collectionSpinner.getSelectedItem()));
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
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
        cursor.moveToFirst();
        List<String> collections = new ArrayList<String>();
        do {
            int columnIndex = cursor.getColumnIndex(Database.COLLECTION_NAME);
            Log.d(getClass().getName(), "column index: " + columnIndex);
            Log.d(getClass().getName(), "column value: " + cursor.getString(columnIndex));
            collections.add(cursor.getString(columnIndex));
        } while(cursor.moveToNext());
        return collections;
    }

    private void insertBook(String collectionName){
        MySQLiteHelper db = new MySQLiteHelper(context);
        db.insertBook(collectionName, this.book);
    }
    private void fillBook(String isbn) {
        MySQLiteHelper db = new MySQLiteHelper(context);
        Cursor c = db.selectLoanTo(isbn);
        if(c.moveToFirst()) {
            int columnIndex = c.getColumnIndex(Database.LOANED_TO);
            Log.d(getClass().getName(), "column index: " + columnIndex);
            Log.d(getClass().getName(), "column value: " + c.getString(columnIndex));
            book.setLoaned_to(c.getString(columnIndex));
        }
    }

    private void updateBook(String collectionName){
        MySQLiteHelper db = new MySQLiteHelper(context);
        db.moveBook(collectionName, this.book);
    }

    private Spinner createSpinner(){

        Spinner spin = (Spinner) promptsView.findViewById(R.id.collectionSpinner);
        //Spinner spin = new Spinner(this);
        List collections = getAllCollections();

        ArrayAdapter<String> adapter_state = new ArrayAdapter<String>(BookDetailsScreen.this,  android.R.layout.simple_spinner_item, collections);
        adapter_state.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter_state);

        return spin;
    }

    /**
     * Modified from code.tutsplus.com/tutorials/android-sdk-create-a-book-scanning-app-displaying-book-information--mobile-17880
     */
    public class GetBookThumb extends AsyncTask<String, Void, String> {

        private Bitmap thumbImg;

        @Override
        protected String doInBackground(String... thumbURLs){
            try{
                Log.d(getClass().getName(), "Establishing connection...");
                URL thumbURL = new URL(thumbURLs[0]);
                URLConnection thumbConn = thumbURL.openConnection();
                thumbConn.connect();

                Log.d(getClass().getName(), "Downloading image...");
                InputStream thumbIn = thumbConn.getInputStream();
                BufferedInputStream thumbBuff = new BufferedInputStream(thumbIn);

                Log.d(getClass().getName(), "Converting to bitmap...");
                thumbImg = BitmapFactory.decodeStream(thumbBuff);

                thumbBuff.close();
                thumbIn.close();

            } catch(Exception e){
                imgURL = "";
                e.printStackTrace();
            }

            return "";
        }

        @Override
        protected void onPostExecute(String result){
            Log.d(getClass().getName(), "Setting image...");
            thumbView = (ImageView) findViewById(R.id.bookCover);
            if(!imgURL.equals(""))
                thumbView.setImageBitmap(thumbImg);
        }
    }
}

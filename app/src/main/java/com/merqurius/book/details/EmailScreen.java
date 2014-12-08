package com.merqurius.book.details;

import android.app.Activity;
import android.app.LauncherActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.merqurius.R;
import com.merqurius.collections.CollectionsScreen;
import com.merqurius.home.HomeScreen;


public class EmailScreen extends Activity implements View.OnClickListener {
    Button email;
    TextView address;
    EditText message;
    String title, loanName, contactAddress;


    static final int REQUEST_SELECT_EMAIL_ADDRESS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_screen);

        title = getIntent().getStringExtra("bookTitle");
        loanName = getIntent().getStringExtra("loanedName");

        address = (TextView) findViewById((R.id.editEmailAddress));
        message = (EditText) findViewById(R.id.editMessage);

        email = (Button) findViewById(R.id.buttonSend);

        email.setOnClickListener(this);

        selectContact();
        address.setText(contactAddress);

    }
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.buttonSend:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{address.getText().toString()} );
                i.putExtra(Intent.EXTRA_SUBJECT, "YOU HAVE MY BOOK!");
                i.putExtra(Intent.EXTRA_TEXT, "Hello: " + loanName + "\n" +
                        "You have the book " + title + " which belongs to me. Here is a message:\n"
                        + message.getText().toString());
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(EmailScreen.this, "There are no email clients installed.",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.email_screen, menu);
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



    private void selectContact() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.CommonDataKinds.Email.CONTENT_TYPE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_SELECT_EMAIL_ADDRESS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SELECT_EMAIL_ADDRESS && resultCode == RESULT_OK) {

            Uri contactUri = data.getData();
            String[] projection = new String[]{ContactsContract.CommonDataKinds.Email.ADDRESS};
            Cursor cursor = getContentResolver().query(contactUri, projection,
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS);
                contactAddress = cursor.getString(numberIndex);
                Log.d(getClass().getName(), contactAddress);
            }
        }
    }
}

package com.merqurius.book.details;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.merqurius.R;


public class EmailScreen extends Activity implements View.OnClickListener {
    Button email;
    EditText address;
    EditText message;
    String title, loanName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        title = getIntent().getStringExtra("bookTitle");
        loanName = getIntent().getStringExtra("loanedName");

        setContentView(R.layout.activity_email_screen);

        email = (Button) findViewById(R.id.buttonSend);

        email.setOnClickListener(this);
    }
    public void onClick(View v) {
        address = (EditText) v.findViewById((R.id.editEmailAddress));
        message = (EditText) v.findViewById(R.id.editMessage);
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
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

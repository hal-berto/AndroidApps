package librorisposte.android.app.librodellerisposte;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import librorisposte.android.app.database.DataBaseHelper;
import librorisposte.android.app.database.ResponseDAO;
import librorisposte.android.app.util.ReadResponse;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    public final static String RESPONSE_MESSAGE = "librorisposte.android.app.librodellerisposte.RESPONSE_MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DataBaseHelper dbHelper = DataBaseHelper.getInstance(this);
        try {
            dbHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error(getResources().getString(R.string.error_database_creation));
        }

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /** Called when the user clicks the Send button */
    public void queryBook(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, DisplayResponseActivity.class);
        //ReadResponse readResponse = new ReadResponse();
        //AsyncTask<String, Void, String> execute = new ReadResponse().execute();
        String message = null;
        try {
            //message = execute.get();
            ResponseDAO responseDao = new ResponseDAO(this);
            message = responseDao.getRandomResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
        intent.putExtra(RESPONSE_MESSAGE, message);
        startActivity(intent);
    }
}

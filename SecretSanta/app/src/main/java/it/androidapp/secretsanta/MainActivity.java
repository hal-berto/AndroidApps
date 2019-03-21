package it.androidapp.secretsanta;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import it.androidapp.secretsanta.database.AppDatabase;
import it.androidapp.secretsanta.database.DatabaseHandler;
import it.androidapp.secretsanta.database.entity.Event;
import it.androidapp.secretsanta.dialog.ConfirmationDialog;

public class MainActivity extends FragmentActivity implements ConfirmationDialog.NoticeDialogListener{

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fillEventList();

        //TODO: Usare RecyclerView
    }

    private void fillEventList(){
        AppDatabase database = DatabaseHandler.getDatabase(getApplicationContext());

        //Loads all configured events
        List<Event> eventList = database.eventDao().getAll();

        if(eventList == null || eventList.size() <= 0){
            TextView textMessageView = (TextView) findViewById(R.id.textMessage);
            textMessageView.setText(R.string.no_events_configured);
        } else {
            TextView textMessageView = (TextView) findViewById(R.id.textMessage);
            textMessageView.setText("");
            for(Event currEvent : eventList) {
                textMessageView.append(currEvent.getName() + " ");
            }
        }
    }

    /** Called when the user taps the "create new event" button */
    public void createNewEvent(View view) {
        Intent intent = new Intent(this, CreateEventActivity.class);
        startActivity(intent);
    }

    public void cleanDatabase(View view){

        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new ConfirmationDialog();
        dialog.show(getSupportFragmentManager(), "ConfirmationDialog");

    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        AppDatabase database = DatabaseHandler.getDatabase(getApplicationContext());
        database.clearAllTables();
        fillEventList();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        fillEventList();
    }

}

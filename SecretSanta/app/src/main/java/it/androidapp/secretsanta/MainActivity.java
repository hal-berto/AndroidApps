package it.androidapp.secretsanta;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import it.androidapp.secretsanta.database.AppDatabase;
import it.androidapp.secretsanta.database.DatabaseHandler;
import it.androidapp.secretsanta.database.entity.Event;
import it.androidapp.secretsanta.dialog.ConfirmationDialog;
import it.androidapp.secretsanta.fragment.EventFragment;
import it.androidapp.secretsanta.fragment.EventRecyclerViewAdapter;

public class MainActivity extends FragmentActivity implements ConfirmationDialog.NoticeDialogListener, EventFragment.OnListFragmentInteractionListener {

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Clean message text view
        TextView textMessageView = (TextView) findViewById(R.id.textMessage);
        textMessageView.setText("");

        fillEventList();
        //fillParticipantList();

    }

    private void fillEventList(){
        AppDatabase database = DatabaseHandler.getDatabase(getApplicationContext());

        //Loads all configured events
        List<Event> eventList = database.eventDao().getAll();

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.fragment);
        recyclerView.setAdapter(new EventRecyclerViewAdapter(eventList,this));

        if(eventList == null || eventList.size() <= 0){
            TextView textMessageView = (TextView) findViewById(R.id.textMessage);
            textMessageView.setText(R.string.no_events_configured);
        }/* else {
            TextView textMessageView = (TextView) findViewById(R.id.textMessage);
            for(Event currEvent : eventList) {
                textMessageView.append(System.getProperty("line.separator") + currEvent.getName());
            }
        }*/
    }

    /*private void fillParticipantList(){
        AppDatabase database = DatabaseHandler.getDatabase(getApplicationContext());

        //Loads all configured events
        List<Participant> participantList = database.participantDao().getAll();

        if(participantList == null || participantList.size() <= 0){
            TextView textMessageView = (TextView) findViewById(R.id.textMessage);
            textMessageView.append(System.getProperty("line.separator") + R.string.no_participants_configured);
        } else {
            TextView textMessageView = (TextView) findViewById(R.id.textMessage);
            for(Participant currParticipant : participantList) {
                textMessageView.append(System.getProperty("line.separator") + currParticipant.getFirstName() + " " + currParticipant.getLastName());
            }
        }
    }*/

    /** Called when the user taps the "create new event" button */
    public void createNewEvent(View view) {
        Intent intent = new Intent(this, CreateEventActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps the "create new participant" button */
    public void createNewParticipant(View view) {
        Intent intent = new Intent(this, CreateParticipantActivity.class);
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


    @Override
    public void onListFragmentInteraction(Event item) {
        //TODO: gestire evento selezionato
        TextView textMessageView = (TextView) findViewById(R.id.textMessage);
        textMessageView.append(System.getProperty("line.separator") + "Elemento selezionato: " + item.getName());
    }
}

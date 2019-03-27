package it.androidapp.secretsanta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import it.androidapp.secretsanta.database.AppDatabase;
import it.androidapp.secretsanta.database.DatabaseHandler;
import it.androidapp.secretsanta.database.entity.Event;
import it.androidapp.secretsanta.database.entity.Participant;
import it.androidapp.secretsanta.database.entity.ParticipantToEvent;
import it.androidapp.secretsanta.dialog.ConfirmationDialog;
import it.androidapp.secretsanta.fragment.EventFragment;
import it.androidapp.secretsanta.fragment.EventRecyclerViewAdapter;
import it.androidapp.secretsanta.fragment.ParticipantFragment;
import it.androidapp.secretsanta.fragment.ParticipantRecyclerViewAdapter;
import it.androidapp.secretsanta.util.DateConverterUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class ManageEventActivity extends FragmentActivity implements ConfirmationDialog.NoticeDialogListener, ParticipantFragment.OnListFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_event);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        Integer selectedEventId = intent.getIntExtra(NavigationParameters.SELECTED_EVENT_ID, -1);

        AppDatabase database = DatabaseHandler.getDatabase(getApplicationContext());
        Event selectedEvent = database.eventDao().getById(selectedEventId);

        TextView eventNameView = findViewById(R.id.event_name_text);
        eventNameView.setText(selectedEvent.getName());

        TextView eventDateView = findViewById(R.id.event_date_text);
        eventDateView.setText(DateConverterUtil.dateToString(selectedEvent.getDate()));

        TextView eventLocationView = findViewById(R.id.event_location_text);
        eventLocationView.setText(selectedEvent.getLocation());

        TextView eventDescriptionView = findViewById(R.id.event_description_text);
        eventDescriptionView.setText(selectedEvent.getDescription());

        TextView eventMinBudgetView = findViewById(R.id.event_min_budget_text);
        eventMinBudgetView.setText(selectedEvent.getMinimumAmount().toString());

        TextView eventMaxBudgetView = findViewById(R.id.event_max_budget_text);
        eventMaxBudgetView.setText(selectedEvent.getMaximumAmount().toString());

        fillParticipantList();
    }

    private void fillParticipantList(){
        Intent intent = getIntent();
        Integer selectedEventId = intent.getIntExtra(NavigationParameters.SELECTED_EVENT_ID, -1);

        AppDatabase database = DatabaseHandler.getDatabase(getApplicationContext());

        //Loads all configured events
        List<Participant> participantList = database.eventDao().getParticipantByEvent(selectedEventId);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.participant_list);
        recyclerView.setAdapter(new ParticipantRecyclerViewAdapter(participantList,this));

    }

    @Override
    public void onListFragmentInteraction(Participant item) {
        Bundle arguments = new Bundle();
        arguments.putInt(NavigationParameters.SELECTED_PARTICIPANT_ID, item.getId());
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new ConfirmationDialog();
        dialog.setArguments(arguments);
        dialog.show(getSupportFragmentManager(), "ConfirmationDialog");
    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        Intent intent = getIntent();
        Integer selectedEventId = intent.getIntExtra(NavigationParameters.SELECTED_EVENT_ID, -1);
        Integer selectedParticipantId = dialog.getArguments().getInt(NavigationParameters.SELECTED_PARTICIPANT_ID);
        ParticipantToEvent participantToDelete = new ParticipantToEvent();
        participantToDelete.setIdEvent(selectedEventId);
        participantToDelete.setIdParticipant(selectedParticipantId);
        AppDatabase database = DatabaseHandler.getDatabase(getApplicationContext());
        database.participantToEventDao().delete(participantToDelete);
        fillParticipantList();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
    }

    /** Called when the user taps the "back to event list" button */
    public void backToEventList(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps the "add new participant" button */
    public void addParticipantToEvent(View view) {
        Intent intent = getIntent();
        Integer selectedEventId = intent.getIntExtra(NavigationParameters.SELECTED_EVENT_ID, -1);

        Intent intentToCall = new Intent(this, ParticipantListActivity.class);
        intentToCall.putExtra(NavigationParameters.SELECTED_EVENT_ID, selectedEventId);
        startActivity(intentToCall);
    }

    public void deleteEvent(View view){
        Intent intent = getIntent();
        Integer selectedEventId = intent.getIntExtra(NavigationParameters.SELECTED_EVENT_ID, -1);
        AppDatabase database = DatabaseHandler.getDatabase(getApplicationContext());
        database.eventResultDao().deleteByEventId(selectedEventId);
        database.participantToEventDao().deleteByEventId(selectedEventId);
        database.eventDao().delete(database.eventDao().getById(selectedEventId));

        Intent intentToCall = new Intent(this, MainActivity.class);
        startActivity(intentToCall);
    }
}

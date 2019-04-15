package it.androidapp.secretsanta;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import it.androidapp.secretsanta.database.AppDatabase;
import it.androidapp.secretsanta.database.DatabaseHandler;
import it.androidapp.secretsanta.database.entity.Event;
import it.androidapp.secretsanta.database.entity.EventResult;
import it.androidapp.secretsanta.database.entity.Participant;
import it.androidapp.secretsanta.database.entity.ParticipantToEvent;
import it.androidapp.secretsanta.dialog.ConfirmationDialog;
import it.androidapp.secretsanta.extraction.ExtractionFailedException;
import it.androidapp.secretsanta.extraction.ExtractionManager;
import it.androidapp.secretsanta.extraction.ExtractionMapCreationException;
import it.androidapp.secretsanta.fragment.ParticipantFragment;
import it.androidapp.secretsanta.fragment.ParticipantRecyclerViewAdapter;
import it.androidapp.secretsanta.mail.MailManager;
import it.androidapp.secretsanta.util.DateConverterUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class ManageEventActivity extends FragmentActivity implements ConfirmationDialog.NoticeDialogListener, ParticipantFragment.OnListFragmentInteractionListener {

    private enum DeleteType{DELETE_PARTICIPANT,DELETE_EVENT}
    private static String ACTION_TYPE = "ACTION_TYPE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_event);

        TextView textMessageView = (TextView) findViewById(R.id.message_box);
        textMessageView.setText("");

        // Get the Intent that started this activity and extract the string
        Integer selectedEventId = getSelectedEventId();

        AppDatabase database = DatabaseHandler.getDatabase(getApplicationContext());
        Event selectedEvent = database.eventDao().getById(selectedEventId);

        setButtonVisibility();

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

    private void setButtonVisibility(){
        Integer selectedEventId = getSelectedEventId();
        AppDatabase database = DatabaseHandler.getDatabase(getApplicationContext());
        Button extractionButton = findViewById(R.id.extractionButton);
        Button sendMailButton = findViewById(R.id.sendMailButton);
        sendMailButton.setEnabled(false);
        List<Participant> participantList = database.eventDao().getParticipantByEvent(selectedEventId);
        List<EventResult> eventResultList = database.eventResultDao().getAllByEvent(selectedEventId);
        //Se non ci sono partecipanti
        if(participantList == null || participantList.size() <= 0){
            extractionButton.setEnabled(false);
            sendMailButton.setEnabled(false);
        }
        //Se è gia' stata effettuata un'estrazione completa (tutti i partecipanti hanno un destinatario)
        else if(eventResultList != null && eventResultList.size() == participantList.size()){
            extractionButton.setText(R.string.perform_new_extraction_button);
            sendMailButton.setEnabled(true);
        }
    }

    private void fillParticipantList(){
        Integer selectedEventId = getSelectedEventId();

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
        arguments.putString(ACTION_TYPE, DeleteType.DELETE_PARTICIPANT.toString());
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
        Integer selectedEventId = getSelectedEventId();
        Integer selectedParticipantId = dialog.getArguments().getInt(NavigationParameters.SELECTED_PARTICIPANT_ID);
        String deleteType = dialog.getArguments().getString(ACTION_TYPE);
        AppDatabase database = DatabaseHandler.getDatabase(getApplicationContext());

        if(DeleteType.DELETE_PARTICIPANT.toString().equals(deleteType) && selectedParticipantId > 0){
            ParticipantToEvent participantToDelete = new ParticipantToEvent();
            participantToDelete.setIdEvent(selectedEventId);
            participantToDelete.setIdParticipant(selectedParticipantId);
            database.participantToEventDao().delete(participantToDelete);
            fillParticipantList();
        } else if(DeleteType.DELETE_EVENT.toString().equals(deleteType) && selectedEventId > 0){
            database.eventResultDao().deleteByEventId(selectedEventId);
            database.participantToEventDao().deleteByEventId(selectedEventId);
            database.eventDao().delete(database.eventDao().getById(selectedEventId));

            Intent intentToCall = new Intent(this, EventListActivity.class);
            startActivity(intentToCall);
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
    }

    /** Called when the user taps the "back to event list" button */
    public void backToEventList(View view) {
        Intent intent = new Intent(this, EventListActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps the "add new participant" button */
    public void addParticipantToEvent(View view) {
        Integer selectedEventId = getSelectedEventId();

        Intent intentToCall = new Intent(this, ParticipantListActivity.class);
        intentToCall.putExtra(NavigationParameters.SELECTED_EVENT_ID, selectedEventId);
        startActivity(intentToCall);
    }

    public void deleteEvent(View view){
        Bundle arguments = new Bundle();
        arguments.putString(ACTION_TYPE, DeleteType.DELETE_EVENT.toString());
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new ConfirmationDialog();
        dialog.setArguments(arguments);
        dialog.show(getSupportFragmentManager(), "ConfirmationDialog");
    }

    public void sendEmail(View view){
        Integer selectedEventId = getSelectedEventId();
        AppDatabase database = DatabaseHandler.getDatabase(getApplicationContext());
        Event event = database.eventDao().getById(selectedEventId);
        List<EventResult> eventResultList = database.eventResultDao().getAllByEvent(selectedEventId);
        MailManager mailManager = new MailManager(getApplicationContext(), selectedEventId);
        TextView textMessageView = (TextView) findViewById(R.id.message_box);

        int mailSentNumber = 0;
        for(EventResult currEventResult : eventResultList){
            Participant participantFrom = database.participantDao().getById(currEventResult.getIdParticipantFrom());
            Participant participantTo = database.participantDao().getById(currEventResult.getIdParticipantTo());
            try {
                mailManager.processEventResult(event, participantFrom, participantTo);
                mailSentNumber++;
                textMessageView.setText(R.string.mail_sending_completed);
                textMessageView.append(String.valueOf(mailSentNumber));
            }catch(Exception e){
                textMessageView.setText(R.string.mail_sending_failed_exception);
                textMessageView.append(": " + e.getMessage());
            }
        }
    }

    public void performExtraction(View view){
        Integer selectedEventId = getSelectedEventId();

        AppDatabase database = DatabaseHandler.getDatabase(getApplicationContext());
        database.eventResultDao().deleteByEventId(selectedEventId);

        ExtractionManager extractionManager = new ExtractionManager(getApplicationContext(), selectedEventId);
        TextView textMessageView = (TextView) findViewById(R.id.message_box);
        try {
            extractionManager.performExtraction();
            setButtonVisibility();
            textMessageView.setText(R.string.extraction_completed);
        } catch(ExtractionMapCreationException e1) {
            textMessageView.setText(R.string.extraction_map_creation_exception);
        } catch(ExtractionFailedException e2) {
            textMessageView.setText(R.string.extraction_failed_exception);
        } catch(Exception e3) {
            textMessageView.setText(e3.getMessage());
        }
    }

    private Integer getSelectedEventId(){
        Intent intent = getIntent();
        Integer selectedEventId = intent.getIntExtra(NavigationParameters.SELECTED_EVENT_ID, -1);
        return selectedEventId;
    }
}

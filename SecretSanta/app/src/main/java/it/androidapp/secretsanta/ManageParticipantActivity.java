package it.androidapp.secretsanta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import it.androidapp.secretsanta.database.AppDatabase;
import it.androidapp.secretsanta.database.DatabaseHandler;
import it.androidapp.secretsanta.database.entity.ExclusionList;
import it.androidapp.secretsanta.database.entity.Participant;
import it.androidapp.secretsanta.database.entity.ParticipantToEvent;
import it.androidapp.secretsanta.dialog.ConfirmationDialog;
import it.androidapp.secretsanta.fragment.ParticipantFragment;
import it.androidapp.secretsanta.fragment.ParticipantRecyclerViewAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.List;

public class ManageParticipantActivity extends FragmentActivity implements ConfirmationDialog.NoticeDialogListener, ParticipantFragment.OnListFragmentInteractionListener {

    private enum DeleteType{DELETE_PARTICIPANT,DELETE_EXCLUSION}
    private static String ACTION_TYPE = "ACTION_TYPE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_participant);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        Integer selectedParticipantId = intent.getIntExtra(NavigationParameters.SELECTED_PARTICIPANT_ID, -1);

        if(selectedParticipantId > 0){
            fillParticipantData(selectedParticipantId);
        }
    }

    private void fillParticipantData(Integer selectedParticipantId){
        AppDatabase database = DatabaseHandler.getDatabase(getApplicationContext());
        Participant participant = database.participantDao().getById(selectedParticipantId);

        //Fill Participant fields
        EditText editTextFirstName = (EditText) findViewById(R.id.participant_firstname_input);
        editTextFirstName.setText(participant.getFirstName());
        EditText editTextLastName = (EditText) findViewById(R.id.participant_lastname_input);
        editTextLastName.setText(participant.getLastName());
        EditText editTextPhone = (EditText) findViewById(R.id.participant_phone_input);
        editTextPhone.setText(participant.getPhone());
        EditText editTextEmail = (EditText) findViewById(R.id.participant_email_input);
        editTextEmail.setText(participant.getEmail());
        //Fill exclusion list
        fillExclusionList(selectedParticipantId);
    }

    private void fillExclusionList(Integer selectedParticipantId){
        AppDatabase database = DatabaseHandler.getDatabase(getApplicationContext());

        //Loads all configured events
        List<Participant> participantList = database.participantDao().getParticipantExcluded(selectedParticipantId);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.participant_list);
        recyclerView.setAdapter(new ParticipantRecyclerViewAdapter(participantList,this));
    }

    @Override
    public void onListFragmentInteraction(Participant item) {
        Intent intent = getIntent();
        Integer selectedParticipantId = intent.getIntExtra(NavigationParameters.SELECTED_PARTICIPANT_ID, -1);
        Bundle arguments = new Bundle();
        arguments.putInt(NavigationParameters.SELECTED_EXCLUDED_ID, item.getId());
        arguments.putInt(NavigationParameters.SELECTED_PARTICIPANT_ID, selectedParticipantId);
        arguments.putString(ACTION_TYPE, DeleteType.DELETE_EXCLUSION.toString());
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
        Integer selectedExcludedId = dialog.getArguments().getInt(NavigationParameters.SELECTED_EXCLUDED_ID);
        Integer selectedParticipantId = dialog.getArguments().getInt(NavigationParameters.SELECTED_PARTICIPANT_ID);
        String deleteType = dialog.getArguments().getString(ACTION_TYPE);
        AppDatabase database = DatabaseHandler.getDatabase(getApplicationContext());

        if(DeleteType.DELETE_PARTICIPANT.toString().equals(deleteType) && selectedParticipantId > 0){
            database.eventResultDao().deleteByParticipantId(selectedParticipantId);
            database.participantToEventDao().deleteByParticipantId(selectedParticipantId);
            database.exclusionListDao().deleteByParticipantId(selectedParticipantId);
            database.participantDao().delete(database.participantDao().getById(selectedParticipantId));
            Intent intentToCall = new Intent(this, ParticipantListActivity.class);
            startActivity(intentToCall);
        } else if(DeleteType.DELETE_EXCLUSION.toString().equals(deleteType) && selectedExcludedId > 0){
            ExclusionList exclusion = new ExclusionList();
            exclusion.setIdParticipant(selectedParticipantId);
            exclusion.setIdParticipantExcluded(selectedExcludedId);
            database.exclusionListDao().delete(exclusion);
            fillExclusionList(selectedParticipantId);
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
    }

    public void saveParticipant(View view) {
        //Read Event fields
        EditText editTextFirstName = (EditText) findViewById(R.id.participant_firstname_input);
        EditText editTextLastName = (EditText) findViewById(R.id.participant_lastname_input);
        EditText editTextPhone = (EditText) findViewById(R.id.participant_phone_input);
        EditText editTextEmail = (EditText) findViewById(R.id.participant_email_input);

        //Save Event entity
        Participant newParticipant = new Participant();
        newParticipant.setFirstName(editTextFirstName.getText().toString());
        newParticipant.setLastName(editTextLastName.getText().toString());
        newParticipant.setPhone(editTextPhone.getText().toString());
        newParticipant.setEmail(editTextEmail.getText().toString());

        AppDatabase database = DatabaseHandler.getDatabase(getApplicationContext());

        Intent intent = getIntent();
        Integer selectedParticipantId = intent.getIntExtra(NavigationParameters.SELECTED_PARTICIPANT_ID, -1);
        if(selectedParticipantId > 0){
            Participant existingParticipant = database.participantDao().getById(selectedParticipantId);
            newParticipant.setId(existingParticipant.getId());
            database.participantDao().update(newParticipant);
        } else {
            database.participantDao().insertAll(newParticipant);
        }

        //Return to main screen
        Intent intentToCall = new Intent(this, ParticipantListActivity.class);
        startActivity(intentToCall);
    }

    public void deleteParticipant(View view){
        Intent intent = getIntent();
        Integer selectedParticipantId = intent.getIntExtra(NavigationParameters.SELECTED_PARTICIPANT_ID, -1);
        Bundle arguments = new Bundle();
        arguments.putString(ACTION_TYPE, DeleteType.DELETE_PARTICIPANT.toString());
        arguments.putInt(NavigationParameters.SELECTED_PARTICIPANT_ID, selectedParticipantId);
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new ConfirmationDialog();
        dialog.setArguments(arguments);
        dialog.show(getSupportFragmentManager(), "ConfirmationDialog");
    }

    /** Called when the user taps the "add new exclusion" button */
    public void addExclusionToParticipant(View view) {
        Intent intent = getIntent();
        Integer selectedParticipantId = intent.getIntExtra(NavigationParameters.SELECTED_PARTICIPANT_ID, -1);

        Intent intentToCall = new Intent(this, ParticipantListActivity.class);
        intentToCall.putExtra(NavigationParameters.SELECTED_PARTICIPANT_ID, selectedParticipantId);
        startActivity(intentToCall);
    }

    /** Called when the user taps the "back to participant list" button */
    public void backToParticipantList(View view) {
        Intent intent = new Intent(this, ParticipantListActivity.class);
        startActivity(intent);
    }
}

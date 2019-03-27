package it.androidapp.secretsanta;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import it.androidapp.secretsanta.database.AppDatabase;
import it.androidapp.secretsanta.database.DatabaseHandler;
import it.androidapp.secretsanta.database.entity.ExclusionList;
import it.androidapp.secretsanta.database.entity.Participant;
import it.androidapp.secretsanta.database.entity.ParticipantToEvent;
import it.androidapp.secretsanta.fragment.ParticipantFragment;
import it.androidapp.secretsanta.fragment.ParticipantRecyclerViewAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class ParticipantListActivity extends FragmentActivity implements ParticipantFragment.OnListFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participant_list);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        Integer selectedEventId = intent.getIntExtra(NavigationParameters.SELECTED_EVENT_ID, -1);
        Integer selectedParticipantId = intent.getIntExtra(NavigationParameters.SELECTED_PARTICIPANT_ID, -1);

        if(selectedEventId > 0 || selectedParticipantId > 0){
            Button createNewParticipantButton = (Button) findViewById(R.id.createNewParticipant);
            createNewParticipantButton.setVisibility(View.GONE);
            createNewParticipantButton.setEnabled(false);
        }

        fillParticipantList(selectedEventId, selectedParticipantId);
    }

    private void fillParticipantList(Integer selectedEventId, Integer selectedParticipantId){
        AppDatabase database = DatabaseHandler.getDatabase(getApplicationContext());
        List<Participant> participantList = null;
        if(selectedEventId > 0){
            //Event selected, load only participant not yet assigned to selected event
            participantList = database.participantDao().getParticipantNotInEvent(selectedEventId);

        } else if(selectedParticipantId > 0){
            //Participant selected, load only participants not yet excluded for current participant
            participantList = database.participantDao().getParticipantNotExcluded(selectedParticipantId);
        } else {
            //Neither event nor participant selected, load all participants
            participantList = database.participantDao().getAll();
        }

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.fragment);
        recyclerView.setAdapter(new ParticipantRecyclerViewAdapter(participantList,this));

        if(participantList == null || participantList.size() <= 0){
            TextView textMessageView = (TextView) findViewById(R.id.textMessage);
            textMessageView.setText(R.string.no_participants_configured);
        }
    }

    public void createNewParticipant(View view) {
        Intent intent = new Intent(this, ManageParticipantActivity.class);
        startActivity(intent);
    }

    public void backTo(View view) {
        Intent intent = getIntent();
        Integer selectedEventId = intent.getIntExtra(NavigationParameters.SELECTED_EVENT_ID, -1);
        Integer selectedParticipantId = intent.getIntExtra(NavigationParameters.SELECTED_PARTICIPANT_ID, -1);

        //Default Intent to call
        Intent intentToCall = new Intent(this, MainActivity.class);
        if(selectedEventId > 0){
            //Intent to call if an event was selected
            intentToCall = new Intent(this, ManageEventActivity.class);
            intentToCall.putExtra(NavigationParameters.SELECTED_EVENT_ID, selectedEventId);
        } else if(selectedParticipantId > 0){
            //Intent to call if a participant was selected
            intentToCall = new Intent(this, ManageParticipantActivity.class);
            intentToCall.putExtra(NavigationParameters.SELECTED_PARTICIPANT_ID, selectedParticipantId);
        }

        startActivity(intentToCall);
    }

    @Override
    public void onListFragmentInteraction(Participant item) {
        Integer selectedItemId = item.getId();
        Intent intent = getIntent();
        Integer selectedEventId = intent.getIntExtra(NavigationParameters.SELECTED_EVENT_ID, -1);
        Integer selectedParticipantId = intent.getIntExtra(NavigationParameters.SELECTED_PARTICIPANT_ID, -1);
        if(selectedEventId > 0){
            //If an event was selected, add selected participant to event
            ParticipantToEvent participantToEvent = new ParticipantToEvent();
            participantToEvent.setIdParticipant(selectedItemId);
            participantToEvent.setIdEvent(selectedEventId);
            AppDatabase database = DatabaseHandler.getDatabase(getApplicationContext());
            database.participantToEventDao().insertAll(participantToEvent);
            //Return to ManageEventActivity
            Intent intentToCall = new Intent(this, ManageEventActivity.class);
            intentToCall.putExtra(NavigationParameters.SELECTED_EVENT_ID, selectedEventId);
            startActivity(intentToCall);
        } else if(selectedParticipantId > 0){
            //If a participant was selected, add selected participant to exclusion list; this is the case of a user that wants to add a participant to an exclusion list
            ExclusionList exclusionList = new ExclusionList();
            exclusionList.setIdParticipant(selectedParticipantId);
            exclusionList.setIdParticipantExcluded(selectedItemId);
            AppDatabase database = DatabaseHandler.getDatabase(getApplicationContext());
            database.exclusionListDao().insertAll(exclusionList);
            //Open ManageParticipantActivity returning the previously selected participant
            Intent intentToCall = new Intent(this, ManageParticipantActivity.class);
            intentToCall.putExtra(NavigationParameters.SELECTED_PARTICIPANT_ID, selectedParticipantId);
            startActivity(intentToCall);
        } else {
            //Open ManageParticipantActivity to edit selected participant
            Intent intentToCall = new Intent(this, ManageParticipantActivity.class);
            intentToCall.putExtra(NavigationParameters.SELECTED_PARTICIPANT_ID, item.getId());
            startActivity(intentToCall);
        }
    }
}

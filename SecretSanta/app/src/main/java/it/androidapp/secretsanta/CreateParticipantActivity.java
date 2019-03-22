package it.androidapp.secretsanta;

import androidx.appcompat.app.AppCompatActivity;
import it.androidapp.secretsanta.database.AppDatabase;
import it.androidapp.secretsanta.database.DatabaseHandler;
import it.androidapp.secretsanta.database.entity.Participant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class CreateParticipantActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_participant);
    }

    public void saveEvent(View view) {
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
        database.participantDao().insertAll(newParticipant);
        //Return to main screen
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}

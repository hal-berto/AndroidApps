package it.androidapp.secretsanta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import it.androidapp.secretsanta.dialog.ConfirmationDialog;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /** Called when the user taps the "create new participant" button */
    public void manageParticipant(View view) {
        Intent intent = new Intent(this, ParticipantListActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps the "create new participant" button */
    public void manageEvent(View view) {
        Intent intent = new Intent(this, EventListActivity.class);
        startActivity(intent);
    }

    public void cleanDatabase(View view){
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new ConfirmationDialog();
        dialog.show(getSupportFragmentManager(), "ConfirmationDialog");

    }
}

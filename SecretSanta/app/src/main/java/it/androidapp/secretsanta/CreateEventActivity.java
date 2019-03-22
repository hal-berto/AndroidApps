package it.androidapp.secretsanta;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import it.androidapp.secretsanta.database.AppDatabase;
import it.androidapp.secretsanta.database.DatabaseHandler;
import it.androidapp.secretsanta.database.entity.Event;
import it.androidapp.secretsanta.datepicker.SecretSantaDatePicker;
import it.androidapp.secretsanta.util.DateConverterUtil;

public class CreateEventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        EditText editTextDate = (EditText) findViewById(R.id.event_date_edit);
        SecretSantaDatePicker datePicker = new SecretSantaDatePicker(editTextDate, this);
    }

    public void saveEvent(View view) {
        //Read Event fields
        EditText editTextName = (EditText) findViewById(R.id.event_name_edit);
        EditText editTextDate = (EditText) findViewById(R.id.event_date_edit);
        EditText editTextLocation = (EditText) findViewById(R.id.event_location_edit);
        EditText editTextDescription = (EditText) findViewById(R.id.event_description_edit);
        EditText editTextMinimumBudget = (EditText) findViewById(R.id.event_minimum_budget_edit);
        EditText editTextMaximumBudget = (EditText) findViewById(R.id.event_maximum_budget_edit);

        //Save Event entity
        Event newEvent = new Event();
        newEvent.setName(editTextName.getText().toString());
        if(StringUtils.isNotBlank(editTextDate.getText().toString())){
            newEvent.setDate(DateConverterUtil.stringToDate(editTextDate.getText().toString()));
        } else {
            newEvent.setDate(new Date());
        }
        newEvent.setLocation(editTextLocation.getText().toString());
        newEvent.setDescription(editTextDescription.getText().toString());
        if(StringUtils.isNotBlank(editTextMinimumBudget.getText().toString())){
            newEvent.setMinimumAmount(Float.parseFloat(editTextMinimumBudget.getText().toString()));
        } else {
            newEvent.setMinimumAmount(0f);
        }
        if(StringUtils.isNotBlank(editTextMaximumBudget.getText().toString())){
            newEvent.setMaximumAmount(Float.parseFloat(editTextMaximumBudget.getText().toString()));
        } else {
            newEvent.setMaximumAmount(newEvent.getMinimumAmount());
        }

        AppDatabase database = DatabaseHandler.getDatabase(getApplicationContext());
        database.eventDao().insertAll(newEvent);
        //Return to main screen
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}

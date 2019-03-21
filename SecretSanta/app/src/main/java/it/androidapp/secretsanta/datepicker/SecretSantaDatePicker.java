package it.androidapp.secretsanta.datepicker;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SecretSantaDatePicker implements View.OnFocusChangeListener, DatePickerDialog.OnDateSetListener {

    private EditText editText;
    private Calendar myCalendar;
    private Context ctx;

    public SecretSantaDatePicker(EditText editText, Context ctx){
        this.editText = editText;
        this.editText.setOnFocusChangeListener(this);
        this.ctx = ctx;
        myCalendar = Calendar.getInstance();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)     {

        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdformat = new SimpleDateFormat(myFormat, Locale.ITALIAN);
        myCalendar.set(Calendar.YEAR, year);
        myCalendar.set(Calendar.MONTH, monthOfYear);
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        editText.setText(sdformat.format(myCalendar.getTime()));

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus){
            new DatePickerDialog(ctx, this, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        }
    }

}

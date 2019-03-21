package it.androidapp.secretsanta.database;

import android.content.Context;

import androidx.room.Room;

public class DatabaseHandler {

    private static AppDatabase database;

    public static AppDatabase getDatabase(Context cw){
        if(database != null){
            return database;
        }

        //Il metodo allowMainThreadQueries() è utilizzato per permettere al codice di eseguire query nel thread principale
        database = Room.databaseBuilder(cw.getApplicationContext(),
                AppDatabase.class, "secret-santa-db").allowMainThreadQueries().build();

        return database;
    }
}

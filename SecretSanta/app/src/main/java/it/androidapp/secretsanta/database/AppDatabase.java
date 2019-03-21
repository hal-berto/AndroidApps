package it.androidapp.secretsanta.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import it.androidapp.secretsanta.database.dao.EventDao;
import it.androidapp.secretsanta.database.dao.ExclusionListDao;
import it.androidapp.secretsanta.database.dao.ParticipantDao;
import it.androidapp.secretsanta.database.dao.ParticipantToEventDao;
import it.androidapp.secretsanta.database.entity.Event;
import it.androidapp.secretsanta.database.entity.ExclusionList;
import it.androidapp.secretsanta.database.entity.Participant;
import it.androidapp.secretsanta.database.entity.ParticipantToEvent;

@Database(entities = {Event.class, ExclusionList.class, Participant.class, ParticipantToEvent.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract EventDao eventDao();

    public abstract ExclusionListDao exclusionListDao();

    public abstract ParticipantDao participantDao();

    public abstract ParticipantToEventDao participantToEventDao();

}

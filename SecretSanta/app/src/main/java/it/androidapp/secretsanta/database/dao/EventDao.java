package it.androidapp.secretsanta.database.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import it.androidapp.secretsanta.database.entity.Event;
import it.androidapp.secretsanta.database.entity.Participant;

@Dao
public interface EventDao {

    @Query("SELECT * FROM event")
    List<Event> getAll();

    @Query("SELECT * FROM event WHERE id = :id")
    Event getById(Integer id);

    @Query("SELECT p.* FROM event e JOIN participant_to_event pte ON e.id = pte.id_event " +
            "JOIN participant p ON pte.id_participant = p.id WHERE e.id = :eventId")
    List<Participant> getParticipantByEvent(Integer eventId);
    
    @Insert
    void insertAll(Event... event);

    @Delete
    void delete(Event event);
}

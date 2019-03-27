package it.androidapp.secretsanta.database.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import it.androidapp.secretsanta.database.entity.EventResult;
import it.androidapp.secretsanta.database.entity.ParticipantToEvent;

@Dao
public interface EventResultDao {

    @Query("SELECT * FROM event_result")
    List<EventResult> getAll();

    @Query("SELECT * FROM event_result WHERE id_event = :idEvent")
    List<EventResult> getAllByEvent(Integer idEvent);

    @Query("SELECT * FROM event_result WHERE id_participant_from = :idParticipant OR id_participant_to = :idParticipant")
    List<EventResult> getAllByParticipant(Integer idParticipant);

    @Query("DELETE FROM event_result WHERE id_event = :eventId")
    void deleteByEventId(Integer eventId);

    @Query("DELETE FROM event_result WHERE id_participant_from = :participantId OR id_participant_to = :participantId")
    void deleteByParticipantId(Integer participantId);

    @Insert
    void insertAll(EventResult... eventResult);

    @Delete
    void delete(EventResult eventResult);
}

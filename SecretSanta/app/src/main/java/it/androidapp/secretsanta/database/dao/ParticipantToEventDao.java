package it.androidapp.secretsanta.database.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import it.androidapp.secretsanta.database.entity.ParticipantToEvent;

@Dao
public interface ParticipantToEventDao {

    @Query("SELECT * FROM participant_to_event")
    List<ParticipantToEvent> getAll();

    @Query("SELECT * FROM participant_to_event WHERE id_event = :idEvent")
    List<ParticipantToEvent> getAllByEvent(Integer idEvent);

    @Query("SELECT * FROM participant_to_event WHERE id_participant = :idParticipant")
    List<ParticipantToEvent> getAllByParticipant(Integer idParticipant);

    @Query("DELETE FROM participant_to_event where id_event = :eventId")
    void deleteByEventId(Integer eventId);

    @Query("DELETE FROM participant_to_event where id_participant = :participantId")
    void deleteByParticipantId(Integer participantId);

    @Insert
    void insertAll(ParticipantToEvent... participantToEvent);

    @Delete
    void delete(ParticipantToEvent participantToEvent);
}

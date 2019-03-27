package it.androidapp.secretsanta.database.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import it.androidapp.secretsanta.database.entity.Participant;

@Dao
public interface ParticipantDao {

    @Query("SELECT * FROM participant")
    List<Participant> getAll();

    @Query("SELECT * FROM participant WHERE id = :id")
    Participant getById(Integer id);

    @Query("SELECT p.* FROM participant p where p.id NOT IN (SELECT pte.id_participant FROM participant_to_event pte where pte.id_event = :eventId)")
    List<Participant> getParticipantNotInEvent(Integer eventId);

    @Query("SELECT p.* FROM participant p where p.id NOT IN (SELECT el.id_participant_excluded FROM exclusion_list el where el.id_participant = :participantId) AND p.id != :participantId")
    List<Participant> getParticipantNotExcluded(Integer participantId);

    @Query("SELECT p.* FROM participant p where p.id IN (SELECT el.id_participant_excluded FROM exclusion_list el where el.id_participant = :participantId) AND p.id != :participantId")
    List<Participant> getParticipantExcluded(Integer participantId);

    @Insert
    void insertAll(Participant... participant);

    @Update
    void update(Participant participant);

    @Delete
    void delete(Participant participant);

}

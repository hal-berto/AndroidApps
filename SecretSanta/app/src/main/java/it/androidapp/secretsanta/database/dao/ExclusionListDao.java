package it.androidapp.secretsanta.database.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import it.androidapp.secretsanta.database.entity.ExclusionList;
import it.androidapp.secretsanta.database.entity.Participant;

@Dao
public interface ExclusionListDao {

    @Query("SELECT * FROM exclusion_list")
    List<ExclusionList> getAll();

    @Query("SELECT p.* FROM exclusion_list el JOIN participant p on el.id_participant_excluded = p.id WHERE el.id_participant = :idParticipant")
    List<Participant> getAllByExcludedParticipant(Integer idParticipant);

    @Insert
    void insertAll(ExclusionList... exclusionList);

    @Delete
    void delete(ExclusionList exclusionList);

}

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

    @Insert
    void insertAll(Participant... participant);

    @Update
    void update(Participant participant);

    @Delete
    void delete(Participant participant);

}

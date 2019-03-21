package it.androidapp.secretsanta.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "participant_to_event", primaryKeys = {"id_event", "id_participant"})
public class ParticipantToEvent {

    @ForeignKey(entity = Event.class, parentColumns = "id", childColumns = "id_event")
    @ColumnInfo(name = "id_event")
    @NonNull
    Integer idEvent;

    @ForeignKey(entity = Participant.class, parentColumns = "id", childColumns = "id_participant")
    @ColumnInfo(name = "id_participant")
    @NonNull
    Integer idParticipant;

    public Integer getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(Integer idEvent) {
        this.idEvent = idEvent;
    }

    public Integer getIdParticipant() {
        return idParticipant;
    }

    public void setIdParticipant(Integer idParticipant) {
        this.idParticipant = idParticipant;
    }
}

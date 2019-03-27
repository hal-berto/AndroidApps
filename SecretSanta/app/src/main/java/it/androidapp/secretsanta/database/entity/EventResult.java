package it.androidapp.secretsanta.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "event_result", primaryKeys = {"id_event", "id_participant_from", "id_participant_to"})
public class EventResult {

    @ForeignKey(entity = Event.class, parentColumns = "id", childColumns = "id_event")
    @ColumnInfo(name = "id_event")
    @NonNull
    Integer idEvent;

    @ForeignKey(entity = Participant.class, parentColumns = "id", childColumns = "id_participant_from")
    @ColumnInfo(name = "id_participant_from")
    @NonNull
    Integer idParticipantFrom;

    @ForeignKey(entity = Participant.class, parentColumns = "id", childColumns = "id_participant_to")
    @ColumnInfo(name = "id_participant_to")
    @NonNull
    Integer idParticipantTo;

    public Integer getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(Integer idEvent) {
        this.idEvent = idEvent;
    }

    public Integer getIdParticipantFrom() {
        return idParticipantFrom;
    }

    public void setIdParticipantFrom(Integer idParticipant) {
        this.idParticipantFrom = idParticipant;
    }

    public Integer getIdParticipantTo() {
        return idParticipantTo;
    }

    public void setIdParticipantTo(Integer idParticipant) {
        this.idParticipantTo = idParticipant;
    }
}

package it.androidapp.secretsanta.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "exclusion_list", primaryKeys = {"id_participant", "id_participant_excluded"})
public class ExclusionList {

    @ForeignKey(entity = Participant.class, parentColumns = "id", childColumns = "id_participant")
    @ColumnInfo(name = "id_participant")
    @NonNull
    public Integer idParticipant;

    @ForeignKey(entity = Participant.class, parentColumns = "id", childColumns = "id_participant_excluded")
    @ColumnInfo(name = "id_participant_excluded")
    @NonNull
    public Integer idParticipantExcluded;

    public Integer getIdParticipant() {
        return idParticipant;
    }

    public void setIdParticipant(Integer idParticipant) {
        this.idParticipant = idParticipant;
    }

    public Integer getIdParticipantExcluded() {
        return idParticipantExcluded;
    }

    public void setIdParticipantExcluded(Integer idParticipantExcluded) {
        this.idParticipantExcluded = idParticipantExcluded;
    }
}

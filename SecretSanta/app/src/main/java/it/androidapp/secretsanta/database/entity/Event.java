package it.androidapp.secretsanta.database.entity;

import java.util.Date;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Event {

    @PrimaryKey(autoGenerate = true)
    public Integer id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "date")
    public Date date;

    @ColumnInfo(name = "location")
    public String location;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "minimum_amount")
    public Float minimumAmount;

    @ColumnInfo(name = "maximum_amount")
    public Float maximumAmount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getMinimumAmount() {
        return minimumAmount;
    }

    public void setMinimumAmount(Float minimumAmount) {
        this.minimumAmount = minimumAmount;
    }

    public Float getMaximumAmount() {
        return maximumAmount;
    }

    public void setMaximumAmount(Float maximumAmount) {
        this.maximumAmount = maximumAmount;
    }
}

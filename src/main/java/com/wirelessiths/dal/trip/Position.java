package com.wirelessiths.dal.trip;

import com.fasterxml.jackson.annotation.JsonSetter;

import java.time.Instant;
import java.util.Arrays;

public class Position {

    private Instant positionCreated;
    private String[] tags;
    private Location location;
    private PositionData positionData;

    public Instant getPositionCreated() {
        return positionCreated;
    }

    public void setPositionCreated(Instant positionCreated) {
        this.positionCreated = positionCreated;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public PositionData getPositionData() {
        return positionData;
    }

    @JsonSetter("position_data")
    public void setPositionData(PositionData positionData) {
        this.positionData = positionData;
    }

    //---------------------------------------
    @JsonSetter("position_created")
    public void setPositionCreatedString(String time){
        setPositionCreated(Instant.parse(time));
    }

    @Override
    public String toString() {
        return "Position{" +
                "positionCreated=" + positionCreated +
                ", tags=" + Arrays.toString(tags) +
                ", location=" + location +
                ", positionData=" + positionData +
                '}';
    }
}

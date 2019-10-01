package com.wirelessiths.dal.trip;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.wirelessiths.dal.InstantConverter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DynamoDBDocument
public class Trip {

//    {
//        "identifiers": {
//        "vehicle_id": "25C8032BE5CF4071BA65309B83DFA502"
//    },
//        "trip_id": "8326876B7C445950C97B4E9EFDF9D554",
//            "market": "market",
//            "start_position": {
//        "latitude": 57.264160324,
//                "longitude": 13.792256847
//    },
//        "end_position": {
//        "latitude": 57.614259887,
//                "longitude": 12.037655082
//    },
//        "start_time": "2019-08-31T16:09:49Z",
//            "end_time": "2019-08-31T17:41:52Z",
//            "total_distance_meter": 148789.0,
//            "tags": [],
//        "positions": [],
//        "section_type": "SINGLE_TRIP",
//            "custom_attributes": {
//        "driver_behaviour_turns": "85",
//                "driver_behaviour_focused": "0",
//                "driver_behaviour_speed": "0",
//                "driver_behaviour_score": "265",
//                "driver_behaviour_smooth": "180"
//    }
//    }from old p&j
//    "start_time": "2019-09-23T08:40:19Z",
//            "end_time": "2019-09-23T08:43:36Z",
//            "start_odometer": 292.63,
//            "end_odometer": 293.275,
//            "total_distance_meter": 644.9999999999818,
//            "average_speed_kph": 144.0,
//            "electrical_distance_meter": 644.9999999999818,


//from real p&j
//customAttributes={driver_behaviour_turns=85, driver_behaviour_focused=0, driver_behaviour_speed=0, driver_behaviour_score=265, driver_behaviour_smooth=180

    private Map<String, String> identifiers;
    private String tripId;
    private String market;
    private Location startPosition;
    private Location endPosition;
    private Instant startTime;
    private Instant endTime;
    private double totalDistanceMeter;
    private List<String> tags;
    private List<Position> positions;
    private String sectionType;
    private Map<String, String> userTripInformation;
    private Map<String, Double> customAttributes;



    public Trip() {
        identifiers = new HashMap<>();
        tags = new ArrayList<>();
        positions = new ArrayList<>();
        userTripInformation = new HashMap<>();
        customAttributes = new HashMap<>();
    }

    public Map<String, String> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(Map<String, String> identifiers) {
        this.identifiers = identifiers;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public Location getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(Location startPosition) {
        this.startPosition = startPosition;
    }

    public Location getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(Location endPosition) {
        this.endPosition = endPosition;
    }

    @DynamoDBTypeConverted( converter = InstantConverter.class )
    public Instant getStartTime() {
        return startTime;
    }
    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    @DynamoDBTypeConverted( converter = InstantConverter.class )
    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public double getTotalDistanceMeter() {
        return totalDistanceMeter;
    }

    public void setTotalDistanceMeter(double totalDistanceMeter) {
        this.totalDistanceMeter = totalDistanceMeter;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<Position> getPositions() {
        return positions;
    }

    public void setPositions(List<Position> positions) {
        this.positions = positions;
    }

    public String getSectionType() {
        return sectionType;
    }

    public void setSectionType(String sectionType) {
        this.sectionType = sectionType;
    }

    public Map<String, String> getUserTripInformation() {
        return userTripInformation;
    }

    public void setUserTripInformation(Map<String, String> userTripInformation) {
        this.userTripInformation = userTripInformation;
    }

    public Map<String, Double> getCustomAttributes() {
        return customAttributes;
    }

    public void setCustomAttributes(Map<String, Double> customAttributes) {
        this.customAttributes = customAttributes;
    }
    //-----------------------------------
    @JsonSetter("start_time")
    public void setStartTimeString(String time){
        setStartTime(Instant.parse(time));
    }

    @JsonSetter("end_time")
    public void setEndTimeString(String time){
        setEndTime(Instant.parse(time));
    }

    @Override
    public String toString() {
        return "Trip{" +
                "identifiers=" + identifiers +
                ", tripId='" + tripId + '\'' +
                ", market='" + market + '\'' +
                ", startPosition=" + startPosition +
                ", endPosition=" + endPosition +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", totalDistanceMeter=" + totalDistanceMeter +
                ", tags=" + tags +
                ", positions=" + positions +
                ", sectionType='" + sectionType + '\'' +
                ", userTripInformation=" + userTripInformation +
                ", customAttributes=" + customAttributes +
                '}';
    }
}

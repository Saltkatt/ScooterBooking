package com.wirelessiths.dal;

import java.awt.*;
import java.awt.geom.Point2D;
import java.time.Instant;
import java.util.List;
import java.util.Map;

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
//    }

    private String tripId;
    private String vehicleId;
    private String market;
    private Point startPosition;
    private Point endPosition;
    private Instant startTime;
    private Instant endTime;
    private double totalDistanceMeters;
    private List<String> tags;
    private List<Point> positions;
    private String sectionType;
    private Map<String, String> customAttributes;



    public Trip() {
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public Point getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(Point startPosition) {
        this.startPosition = startPosition;
    }

    public Point getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(Point endPosition) {
        this.endPosition = endPosition;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public double getTotalDistanceMeters() {
        return totalDistanceMeters;
    }

    public void setTotalDistanceMeters(double totalDistanceMeters) {
        this.totalDistanceMeters = totalDistanceMeters;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<Point> getPositions() {
        return positions;
    }

    public void setPositions(List<Point> positions) {
        this.positions = positions;
    }


    public Map<String, String> getCustomAttributes() {
        return customAttributes;
    }

    public void setCustomAttributes(Map<String, String> customAttributes) {
        this.customAttributes = customAttributes;
    }

    public String getSectionType() {
        return sectionType;
    }

    public void setSectionType(String sectionType) {
        this.sectionType = sectionType;
    }
}

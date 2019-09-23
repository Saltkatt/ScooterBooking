package com.wirelessiths.dal.trip;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.Map;

@DynamoDBDocument
public class PositionData {

    private double speedKph;
    private double altitudeMeter;
    private Map<String, String> customAttributes;

    public double getSpeedKph() {
        return speedKph;
    }

    //@JsonSetter("speed_kph")
    public void setSpeedKph(double speedKph) {
        this.speedKph = speedKph;
    }

    public double getAltitudeMeter() {
        return altitudeMeter;
    }

    //@JsonSetter("altitude_meter")
    public void setAltitudeMeter(double altitudeMeter) {
        this.altitudeMeter = altitudeMeter;
    }

    public Map<String, String> getCustomAttributes() {
        return customAttributes;
    }
    //@JsonSetter("custom_attributes")
    public void setCustomAttributes(Map<String, String> customAttributes) {
        this.customAttributes = customAttributes;
    }

    @Override
    public String toString() {
        return "PositionData{" +
                "speedKph=" + speedKph +
                ", altitudeMeter=" + altitudeMeter +
                ", customAttributes=" + customAttributes +
                '}';
    }
}

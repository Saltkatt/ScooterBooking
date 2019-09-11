package com.wirelessiths.s3;

import java.io.IOException;
import java.util.Map;

import static com.wirelessiths.s3.ReadFile.readFileInBucket;

public class Settings {

    private int buffer;
    private int maxDuration;
    private int notCheckedOut;
    private int maxBookings;

    public Settings(int buffer, int maxDuration, int notCheckedOut, int maxBookings) {
        this.buffer = buffer;
        this.maxDuration = maxDuration;
        this.notCheckedOut = notCheckedOut;
        this.maxBookings = maxBookings;
    }

    public Settings() {

    }

    public int getBuffer() {
        return buffer;
    }

    public void setBuffer(int buffer) {
        this.buffer = buffer;
    }

    public int getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(int maxDuration) {
        this.maxDuration = maxDuration;
    }

    public int getNotCheckedOut() {
        return notCheckedOut;
    }

    public void setNotCheckedOut(int notCheckedOut) {
        this.notCheckedOut = notCheckedOut;
    }

    public int getMaxBookings() {
        return maxBookings;
    }

    public void setMaxBookings(int maxBookings) {
        this.maxBookings = maxBookings;
    }

    /**
     * Creates new hashmap for values retrieved from readFileInBucket() and sets the values to Settings variables.
     * @return settings
     */
    public static Settings getSettings() throws IOException {

        //Create new hashmap "config" to receive hashmap in readFileInBucket().
        Map<String, Integer > config = readFileInBucket();
        Settings settings = new Settings();

        //setBuffer with config buffer value.
        settings.setBuffer(config.get("buffer"));
        settings.setMaxDuration(config.get("maxDuration"));
        settings.setMaxBookings(config.get("maxBookingsPerUser"));
        settings.setNotCheckedOut(config.get("notCheckedOut"));

        return settings;
    }
}
